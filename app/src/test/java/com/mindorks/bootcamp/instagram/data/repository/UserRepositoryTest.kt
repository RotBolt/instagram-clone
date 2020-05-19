package com.mindorks.bootcamp.instagram.data.repository

import com.mindorks.bootcamp.instagram.data.local.db.DatabaseService
import com.mindorks.bootcamp.instagram.data.local.prefs.UserPreferences
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.NetworkService
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.data.remote.request.LoginRequest
import com.mindorks.bootcamp.instagram.data.remote.request.SignUpRequest
import com.mindorks.bootcamp.instagram.data.remote.request.UpdateInfoRequest
import com.mindorks.bootcamp.instagram.data.remote.response.GeneralResponse
import com.mindorks.bootcamp.instagram.data.remote.response.LoginResponse
import com.mindorks.bootcamp.instagram.data.remote.response.SignUpResponse
import com.mindorks.bootcamp.instagram.data.remote.response.UserInfoResponse
import com.mindorks.bootcamp.instagram.utils.TestHelper
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UserRepositoryTest {

    @Mock
    private lateinit var networkService: NetworkService

    @Mock
    private lateinit var databaseService: DatabaseService

    @Mock
    private lateinit var userPreferences: UserPreferences

    private lateinit var userRepository: UserRepository

    private lateinit var user: User

    @Before
    fun setup() {
        user = TestHelper.getTestUser()
        Networking.API_KEY = "fake-api-key"
        userRepository = UserRepository(
            networkService, databaseService, userPreferences
        )
    }

    @Test
    fun saveCurrentUser_shouldSaveUser() {
        userRepository.saveCurrentUser(user)

        verify(userPreferences).setUserId(user.id)
        verify(userPreferences).setUserName(user.name)
        verify(userPreferences).setUserEmail(user.email)
        verify(userPreferences).setAccessToken(user.accessToken)
    }

    @Test
    fun removeCurrentUser_shouldRemoveUser() {
        userRepository.removeCurrentUser()

        verify(userPreferences).removeUserId()
        verify(userPreferences).removeUserName()
        verify(userPreferences).removeUserEmail()
        verify(userPreferences).removeAccessToken()
    }

    @Test
    fun getCurrentUser_shouldGetUser() {

        doReturn(user.id)
            .`when`(userPreferences)
            .getUserId()

        doReturn(user.name)
            .`when`(userPreferences)
            .getUserName()

        doReturn(user.email)
            .`when`(userPreferences)
            .getUserEmail()

        doReturn(user.accessToken)
            .`when`(userPreferences)
            .getAccessToken()

        val thisUser = userRepository.getCurrentUser()
        assert(thisUser == User(user.id, user.name, user.email, user.accessToken))

        verify(userPreferences).getUserId()
        verify(userPreferences).getUserName()
        verify(userPreferences).getUserEmail()
        verify(userPreferences).getAccessToken()
    }

    @Test
    fun getCurrentUserDetailNull_shouldGetNullUser() {

        doReturn(user.id)
            .`when`(userPreferences)
            .getUserId()

        doReturn(null)
            .`when`(userPreferences)
            .getUserName()

        doReturn(user.email)
            .`when`(userPreferences)
            .getUserEmail()

        doReturn(user.accessToken)
            .`when`(userPreferences)
            .getAccessToken()

        val thisUser = userRepository.getCurrentUser()
        assert(thisUser == null)

        verify(userPreferences).getUserId()
        verify(userPreferences).getUserName()
        verify(userPreferences).getUserEmail()
        verify(userPreferences).getAccessToken()
    }

    @Test
    fun doLoginUser_shouldRequestLoginCall() {
        val email = user.email
        val password = "puipuipui"

        val loginResponse = LoginResponse(
            "success",
            200,
            "ok",
            user.accessToken,
            user.id,
            user.name,
            email,
            user.profilePicUrl
        )
        doReturn(Single.just(loginResponse))
            .`when`(networkService)
            .doLoginCall(LoginRequest(email, password))

        val data = userRepository.doLoginUser(email, password)

        val testObserver = TestObserver<User>()
        data.subscribe(testObserver)

        testObserver.assertValue {
            it == user
        }

        verify(networkService).doLoginCall(LoginRequest(email, password))
    }

    @Test
    fun doSignUpUser_shouldRequestSignUpCall() {
        val email = user.email
        val password = "puipuipui"
        val name = user.name

        val signUpResponse = SignUpResponse(
            "success",
            200,
            "ok",
            user.accessToken,
            "refresh-token",
            user.id,
            user.name,
            email
        )
        doReturn(Single.just(signUpResponse))
            .`when`(networkService)
            .doSignUpCall(SignUpRequest(email, password, name))

        val data = userRepository.doSignUpUser(email, password, name)

        val testObserver = TestObserver<User>()
        data.subscribe(testObserver)

        testObserver.assertValue {
            it.id == user.id && it.name == user.name && it.email == user.email && it.accessToken == user.accessToken
        }

        verify(networkService).doSignUpCall(SignUpRequest(email, password, name))
    }

    @Test
    fun fetchUserInfo_requestFetchUserInfoCall() {
        val userInfoResponse = UserInfoResponse(
            "success",
            200,
            "ok",
            UserInfoResponse.UserInfo(
                user.id,
                user.name,
                user.profilePicUrl,
                null
            )
        )

        doReturn(Single.just(userInfoResponse))
            .`when`(networkService)
            .doFetchUserInfoCall(user.id, user.accessToken)

        val data = userRepository.doFetchUserInfo(user)

        val testObserver = TestObserver<UserInfoResponse.UserInfo>()
        data.subscribe(testObserver)

        testObserver.assertValue {
            it.id == user.id
        }

        verify(networkService)
            .doFetchUserInfoCall(user.id, user.accessToken)
    }

    @Test
    fun onLogoutUser_requestLogOutCall() {
        val generalResponse = GeneralResponse(
            "success",
            "ok"
        )

        doReturn(Single.just(generalResponse))
            .`when`(networkService)
            .doLogOutCall(user.id, user.accessToken)

        userRepository.doLogoutUser(user)

        verify(networkService)
            .doLogOutCall(user.id, user.accessToken)

    }

    @Test
    fun onUpdateUser_requestUpdateCall() {
        val generalResponse = GeneralResponse(
            "success",
            "ok"
        )

        val updateRequest = UpdateInfoRequest(
            user.name,
            user.profilePicUrl,
            "No bio set"
        )

        doReturn(Single.just(generalResponse))
            .`when`(networkService)
            .doUpdateUserInfoCall(updateRequest, user.id, user.accessToken)

        userRepository.doUpdateUserInfo(updateRequest, user)

        verify(networkService)
            .doUpdateUserInfoCall(updateRequest, user.id, user.accessToken)

    }

}