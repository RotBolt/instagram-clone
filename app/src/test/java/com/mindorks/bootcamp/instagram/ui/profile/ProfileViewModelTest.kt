package com.mindorks.bootcamp.instagram.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.data.model.Image
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.data.remote.response.GeneralResponse
import com.mindorks.bootcamp.instagram.data.remote.response.MyPostListResponse
import com.mindorks.bootcamp.instagram.data.remote.response.UserInfoResponse
import com.mindorks.bootcamp.instagram.data.repository.PostRepository
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.utils.TestHelper
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.TestSchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class ProfileViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var networkHelper: NetworkHelper

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var postRepository: PostRepository

    @Mock
    lateinit var logoutObserver: Observer<Boolean>

    @Mock
    lateinit var loadingObserver: Observer<Boolean>

    @Mock
    lateinit var loadingMyPostsObserver: Observer<Boolean>

    @Mock
    lateinit var userNameObserver: Observer<String>

    @Mock
    lateinit var taglineObserver: Observer<String>

    @Mock
    lateinit var profilePicObserver: Observer<Image?>

    @Mock
    lateinit var myAllPostsObserver: Observer<List<MyPostListResponse.MyPost>>

    @Mock
    lateinit var messageStringIdObserver: Observer<Resource<Int>>

    lateinit var testScheduler: TestScheduler

    lateinit var profileViewModel: ProfileViewModel

    lateinit var user: User

    @Before
    fun setup() {

        user = TestHelper.getTestUser()

        doReturn(user)
            .`when`(userRepository)
            .getCurrentUser()


        Networking.API_KEY = "fake_api_key"
        testScheduler = TestScheduler()
        val testSchedulerProvider = TestSchedulerProvider(testScheduler)

        profileViewModel = ProfileViewModel(
            testSchedulerProvider,
            CompositeDisposable(),
            networkHelper,
            userRepository, postRepository
        )

        profileViewModel.apply {
            logout.observeForever(logoutObserver)
            loading.observeForever(loadingObserver)
            loadingMyPosts.observeForever(loadingMyPostsObserver)

            userName.observeForever(userNameObserver)
            tagLine.observeForever(taglineObserver)
            profilePic.observeForever(profilePicObserver)
            myAllPosts.observeForever(myAllPostsObserver)
            messageStringId.observeForever(messageStringIdObserver)
        }
    }

    @Test
    fun givenValidUser_onFetchUserInfo_shouldLoadUserInfo() {
        val userInfo = UserInfoResponse.UserInfo(
            user.id,
            user.name,
            user.profilePicUrl,
            null
        )
        doReturn(Single.just(userInfo))
            .`when`(userRepository)
            .doFetchUserInfo(user)

        profileViewModel.onFetchUserInfo()

        testScheduler.triggerActions()

        verify(userNameObserver).onChanged(userInfo.name)
        verify(taglineObserver).onChanged("No Bio Set")

        assert(profileViewModel.tagLine.value == "No Bio Set")

        verify(loadingObserver).onChanged(true)
        verify(loadingObserver).onChanged(false)
    }

    @Test
    fun givenValidUser_onFetchMyPosts_shouldLoadMyPosts() {
        val fakeMyPostList = listOf(
            MyPostListResponse.MyPost(
                "id1",
                "http://cloudStorage.api.com/djhkhd/post1.jpeg",
                300,
                300,
                Date()
            ),
            MyPostListResponse.MyPost(
                "id2",
                "http://cloudStorage.api.com/djhkhd/post2.jpeg",
                300,
                300,
                Date()
            )
        )

        doReturn(Single.just(fakeMyPostList))
            .`when`(postRepository)
            .fetchMyPostList(user)

        profileViewModel.onFetchMyPosts()

        testScheduler.triggerActions()

        verify(loadingMyPostsObserver).onChanged(true)
        verify(loadingMyPostsObserver).onChanged(false)

        verify(myAllPostsObserver).onChanged(fakeMyPostList)

        assert(profileViewModel.myAllPosts.value!!.isNotEmpty())
        assert(profileViewModel.myAllPosts.value == fakeMyPostList)
    }

    @Test
    fun givenValidUserLogout_onSuccess_shouldLogout(){
        val fakeGeneralResponse = GeneralResponse("success", "Ok")

        doReturn(Single.just(fakeGeneralResponse))
            .`when`(userRepository)
            .doLogoutUser(user)

        profileViewModel.onLogout()

        testScheduler.triggerActions()

        verify(loadingObserver).onChanged(true)
        verify(loadingObserver).onChanged(false)
        verify(logoutObserver).onChanged(true)
    }

    @Test
    fun givenValidUserLogout_onError_shouldLogout(){
        val fakeGeneralResponse = GeneralResponse("error", "Ok")

        doReturn(Single.just(fakeGeneralResponse))
            .`when`(userRepository)
            .doLogoutUser(user)

        profileViewModel.onLogout()

        testScheduler.triggerActions()

        verify(loadingObserver).onChanged(true)
        verify(loadingObserver).onChanged(false)
        verify(messageStringIdObserver).onChanged(Resource.error(R.string.network_default_error))
    }

    @After
    fun tearDown() {
        profileViewModel.apply {
            logout.removeObserver(logoutObserver)
            loading.removeObserver(loadingObserver)
            loadingMyPosts.removeObserver(loadingMyPostsObserver)

            userName.removeObserver(userNameObserver)
            tagLine.removeObserver(taglineObserver)
            profilePic.removeObserver(profilePicObserver)
            myAllPosts.removeObserver(myAllPostsObserver)
            messageStringId.removeObserver(messageStringIdObserver)
        }
    }
}