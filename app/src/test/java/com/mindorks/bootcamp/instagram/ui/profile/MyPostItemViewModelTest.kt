package com.mindorks.bootcamp.instagram.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.data.model.Image
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.data.remote.response.MyPostListResponse
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.profile.myPosts.MyPostItemViewModel
import com.mindorks.bootcamp.instagram.utils.TestHelper
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.TestSchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class MyPostItemViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var networkHelper: NetworkHelper

    @Mock
    lateinit var myPostImageDetailsObserver: Observer<Image>

    @Mock
    lateinit var launchPostDetailObserver: Observer<Event<String?>>

    lateinit var testScheduler: TestScheduler

    lateinit var myPostItemViewModel: MyPostItemViewModel

    lateinit var user: User

    @Before
    fun setup() {
        user = TestHelper.getTestUser()

        Mockito.doReturn(user)
            .`when`(userRepository)
            .getCurrentUser()

        Networking.API_KEY = "fake-api-key"

        testScheduler = TestScheduler()
        val testSchedulerProvider = TestSchedulerProvider(testScheduler)

        myPostItemViewModel = MyPostItemViewModel(
            testSchedulerProvider,
            CompositeDisposable(),
            networkHelper,
            userRepository
        )

        myPostItemViewModel.apply {
            launchPostDetail.observeForever(launchPostDetailObserver)
            myPostImageDetails.observeForever(myPostImageDetailsObserver)
        }
    }

    @Test
    fun givenOnMyPostClick_shouldUpdateLaunchPostDetail() {
        val fakeMyPost = MyPostListResponse.MyPost(
            "id",
            "http://cloudstorage.api.com/hjfkhfk/pui.jpeg",
            300,
            400,
            Date()
        )

        val headers = mapOf(
            Networking.HEADER_API_KEY to Networking.API_KEY,
            Networking.HEADER_USER_ID to user.id,
            Networking.HEADER_ACCESS_TOKEN to user.accessToken
        )

        myPostItemViewModel.updateData(fakeMyPost)

        verify(myPostImageDetailsObserver).onChanged(Image(fakeMyPost.imgUrl, headers))

        myPostItemViewModel.onMyPostClick()

        verify(launchPostDetailObserver).onChanged(Event(fakeMyPost.id))
    }

    @After
    fun tearDown() {
        myPostItemViewModel.apply {
            launchPostDetail.removeObserver(launchPostDetailObserver)
            myPostImageDetails.removeObserver(myPostImageDetailsObserver)
        }
    }

}