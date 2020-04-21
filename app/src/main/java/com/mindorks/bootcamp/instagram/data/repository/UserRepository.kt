package com.mindorks.bootcamp.instagram.data.repository

import com.mindorks.bootcamp.instagram.data.local.db.DatabaseService
import com.mindorks.bootcamp.instagram.data.local.prefs.UserPreferences
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.NetworkService
import com.mindorks.bootcamp.instagram.data.remote.request.LoginRequest
import com.mindorks.bootcamp.instagram.data.remote.request.SignUpRequest
import com.mindorks.bootcamp.instagram.data.remote.request.UpdateInfoRequest
import com.mindorks.bootcamp.instagram.data.remote.response.GeneralResponse
import com.mindorks.bootcamp.instagram.data.remote.response.UserInfoResponse
import io.reactivex.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val networkService: NetworkService,
    private val databaseService: DatabaseService,
    private val userPreferences: UserPreferences
) {

    fun saveCurrentUser(user: User) {
        userPreferences.setUserId(user.id)
        userPreferences.setUserName(user.name)
        userPreferences.setUserEmail(user.email)
        userPreferences.setAccessToken(user.accessToken)
    }

    fun removeCurrentUser() {
        userPreferences.removeUserId()
        userPreferences.removeUserName()
        userPreferences.removeUserEmail()
        userPreferences.removeAccessToken()
    }

    fun getCurrentUser(): User? {

        val userId = userPreferences.getUserId()
        val userName = userPreferences.getUserName()
        val userEmail = userPreferences.getUserEmail()
        val accessToken = userPreferences.getAccessToken()

        return if (userId !== null && userName != null && userEmail != null && accessToken != null)
            User(userId, userName, userEmail, accessToken)
        else
            null
    }

    fun doLoginUser(email: String, password: String): Single<User> =
        networkService.doLoginCall(LoginRequest(email, password))
            .map {
                return@map User(
                    it.userId,
                    it.userName,
                    it.userEmail,
                    it.accessToken,
                    it.profilePicUrl
                )
            }

    fun doSignUpUser(email: String, password: String, name: String): Single<User> =
        networkService.doSignUpCall(SignUpRequest(email, password, name))
            .map {
                User(
                    it.userId,
                    it.userName,
                    it.userEmail,
                    it.accessToken
                )
            }

    fun doFetchUserInfo(user: User): Single<UserInfoResponse.UserInfo> =
        networkService.doFetchUserInfoCall(user.id, user.accessToken)
            .map { it.data }

    fun doLogoutUser(user: User): Single<GeneralResponse> =
        networkService.doLogOutCall(user.id, user.accessToken)

    fun doUpdateUserInfo(updateInfo: UpdateInfoRequest, user: User): Single<GeneralResponse> =
        networkService.doUpdateUserInfoCall(updateInfo, user.id, user.accessToken)

}