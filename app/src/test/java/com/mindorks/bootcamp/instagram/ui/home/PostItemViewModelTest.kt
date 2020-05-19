package com.mindorks.bootcamp.instagram.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.data.model.Image
import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.data.repository.PostRepository
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.home.posts.PostItemViewModel
import com.mindorks.bootcamp.instagram.utils.TestHelper
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.common.TimeUtils
import com.mindorks.bootcamp.instagram.utils.display.ScreenResourceProvider
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelperImpl
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
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class PostItemViewModelTest {

    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var networkHelper: NetworkHelperImpl

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var postRepository: PostRepository

    @Mock
    lateinit var screenResourceProvider: ScreenResourceProvider

    @Mock
    lateinit var postDataObserver: Observer<Post>

    @Mock
    lateinit var nameObserver: Observer<String>

    @Mock
    lateinit var postTimeObserver: Observer<String>

    @Mock
    lateinit var likesCountObserver: Observer<Int>

    @Mock
    lateinit var isLikedObserver: Observer<Boolean>

    @Mock
    lateinit var profileImageObserver: Observer<Image?>

    @Mock
    lateinit var postImageDetailObserver: Observer<Image>

    @Mock
    lateinit var messageStringIdObserver: Observer<Resource<Int>>


    lateinit var testScheduler: TestScheduler

    lateinit var postItemViewModel: PostItemViewModel

    lateinit var user: User

    @Before
    fun setup() {
        Networking.API_KEY = "fake_api_key"

        user = TestHelper.getTestUser()

        doReturn(user)
            .`when`(userRepository)
            .getCurrentUser()

        doReturn(1080)
            .`when`(screenResourceProvider)
            .getScreenWidth()

        doReturn(1920)
            .`when`(screenResourceProvider)
            .getScreenHeight()

        testScheduler = TestScheduler()
        val testSchedulerProvider = TestSchedulerProvider(testScheduler)
        val compositeDisposable = CompositeDisposable()
        postItemViewModel = PostItemViewModel(
            testSchedulerProvider,
            compositeDisposable,
            networkHelper,
            userRepository,
            postRepository,
            screenResourceProvider
        )

        postItemViewModel.apply {
            data.observeForever(postDataObserver)
            name.observeForever(nameObserver)
            postTime.observeForever(postTimeObserver)
            likesCount.observeForever(likesCountObserver)
            isLiked.observeForever(isLikedObserver)
            profileImage.observeForever(profileImageObserver)
            postImageDetail.observeForever(postImageDetailObserver)
            messageStringId.observeForever(messageStringIdObserver)
        }
    }

    @Test
    fun givenPostNotLiked_onLikeCall_shouldLikePost() {
        val fakePost = Post(
            "id",
            "https://cloudStorage.api.com/90u2803nd/puiPost.jpeg",
            1080,
            720,
            Post.User(
                "userId-r",
                "Rohan Maity",
                null
            ),
            mutableListOf(
                Post.User(
                    "userId-ra",
                    "Raven",
                    null
                )
            ),
            Date()
        )

        postItemViewModel.updateData(fakePost)

        doReturn(true)
            .`when`(networkHelper)
            .isNetworkConnected()

        assert(postItemViewModel.isLiked.value == false)

        fakePost.likedBy?.add(
            Post.User(
                user.id,
                user.name,
                user.profilePicUrl
            )
        )

        doReturn(Single.just(fakePost))
            .`when`(postRepository)
            .makeLikePost(fakePost, user)

        postItemViewModel.onLikeClick()
        testScheduler.triggerActions()

        verify(postDataObserver, times(2)).onChanged(fakePost)
        verify(nameObserver, times(2)).onChanged(fakePost.creator.name)
        verify(postTimeObserver, times(2)).onChanged(TimeUtils.getTimeAgo(fakePost.createdAt))
        verify(isLikedObserver).onChanged(false)
        verify(isLikedObserver).onChanged(true)

        assert(postItemViewModel.isLiked.value == true)

    }

    @Test
    fun givenPostLiked_onLikeCall_shouldUnlikePost(){

        val currentUser =  Post.User(
            user.id,
            user.name,
            user.profilePicUrl
        )
        val fakePost = Post(
            "id",
            "https://cloudStorage.api.com/90u2803nd/puiPost.jpeg",
            1080,
            720,
            Post.User(
                "userId-r",
                "Rohan Maity",
                null
            ),
            mutableListOf(
                Post.User(
                    "userId-ra",
                    "Raven",
                    null
                ),
                currentUser
            ),
            Date()
        )

        postItemViewModel.updateData(fakePost)

        doReturn(true)
            .`when`(networkHelper)
            .isNetworkConnected()

        assert(postItemViewModel.isLiked.value == true)

        fakePost.likedBy?.remove(currentUser)

        doReturn(Single.just(fakePost))
            .`when`(postRepository)
            .makeUnlikePost(fakePost,user)

        postItemViewModel.onLikeClick()

        testScheduler.triggerActions()

        verify(postDataObserver, times(2)).onChanged(fakePost)
        verify(nameObserver, times(2)).onChanged(fakePost.creator.name)
        verify(postTimeObserver, times(2)).onChanged(TimeUtils.getTimeAgo(fakePost.createdAt))
        verify(isLikedObserver).onChanged(true)
        verify(isLikedObserver).onChanged(false)

        assert(postItemViewModel.isLiked.value == false)

    }

    @Test
    fun givenNoInternet_shouldShowNetworkConnectionError(){
        val fakePost = Post(
            "id",
            "https://cloudStorage.api.com/90u2803nd/puiPost.jpeg",
            1080,
            720,
            Post.User(
                "userId-r",
                "Rohan Maity",
                null
            ),
            mutableListOf(
                Post.User(
                    "userId-ra",
                    "Raven",
                    null
                )
            ),
            Date()
        )

        postItemViewModel.updateData(fakePost)

        doReturn(false)
            .`when`(networkHelper)
            .isNetworkConnected()

        postItemViewModel.onLikeClick()

        testScheduler.triggerActions()

        verify(messageStringIdObserver).onChanged(Resource.error(R.string.network_connection_error))
        assert(postItemViewModel.messageStringId.value == Resource.error(R.string.network_connection_error))
    }


    @After
    fun tearDown() {
        postItemViewModel.apply {
            data.removeObserver(postDataObserver)
            name.removeObserver(nameObserver)
            postTime.removeObserver(postTimeObserver)
            likesCount.removeObserver(likesCountObserver)
            isLiked.removeObserver(isLikedObserver)
            profileImage.removeObserver(profileImageObserver)
            postImageDetail.removeObserver(postImageDetailObserver)
            messageStringId.removeObserver(messageStringIdObserver)
        }
    }


}