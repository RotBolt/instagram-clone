package com.mindorks.bootcamp.instagram.ui.postDetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.data.model.Image
import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.data.repository.PostRepository
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.utils.TestHelper
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.common.TimeUtils
import com.mindorks.bootcamp.instagram.utils.display.ScreenResourceProvider
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
import org.mockito.Mockito
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class PostDetailsViewModelTest {

    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var networkHelper: NetworkHelper

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var postRepository: PostRepository

    @Mock
    lateinit var screenResourceProvider: ScreenResourceProvider

    @Mock
    lateinit var loadingObserver: Observer<Boolean>

    @Mock
    lateinit var nameObserver: Observer<String>

    @Mock
    lateinit var postTimeObserver: Observer<String>

    @Mock
    lateinit var likesCountObserver: Observer<Int>

    @Mock
    lateinit var isLikedObserver: Observer<Boolean>

    @Mock
    lateinit var profileImageObserver: Observer<Image>

    @Mock
    lateinit var postImageDetailObserver: Observer<Image>

    @Mock
    lateinit var messageStringIdObserver: Observer<Resource<Int>>

    lateinit var testScheduler: TestScheduler

    lateinit var postDetailsViewModel: PostDetailsViewModel

    lateinit var user: User

    @Before
    fun setup() {
        user = TestHelper.getTestUser()

        testScheduler = TestScheduler()

        doReturn(user)
            .`when`(userRepository)
            .getCurrentUser()

        Networking.API_KEY = "fake-api-key"

        postDetailsViewModel = PostDetailsViewModel(
            TestSchedulerProvider(testScheduler),
            CompositeDisposable(),
            networkHelper,
            userRepository,
            postRepository,
            screenResourceProvider
        )

        postDetailsViewModel.apply {
            name.observeForever(nameObserver)
            loading.observeForever(loadingObserver)
            postTime.observeForever(postTimeObserver)
            likesCount.observeForever(likesCountObserver)
            isLiked.observeForever(isLikedObserver)
            profileImage.observeForever(profileImageObserver)
            postImageDetail.observeForever(postImageDetailObserver)
            messageStringId.observeForever(messageStringIdObserver)
        }

    }

    @Test
    fun givenValidUserPostId_onFetchPostDetail_shouldFetchPost(){
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

        doReturn(Single.just(fakePost))
            .`when`(postRepository)
            .fetchPostDetail(fakePost.id,user)

        postDetailsViewModel.onFetchPostDetail(fakePost.id)
        testScheduler.triggerActions()

        verify(loadingObserver).onChanged(true)
        verify(loadingObserver).onChanged(false)

        verify(nameObserver).onChanged(fakePost.creator.name)
        verify(postTimeObserver).onChanged(TimeUtils.getTimeAgo(fakePost.createdAt))
        verify(isLikedObserver).onChanged(false)
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

        postDetailsViewModel.postDetail.postValue(fakePost)

        doReturn(true)
            .`when`(networkHelper)
            .isNetworkConnected()

        assert(postDetailsViewModel.isLiked.value == false)

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

        postDetailsViewModel.onLikeClick()
        testScheduler.triggerActions()

        verify(nameObserver, Mockito.times(2)).onChanged(fakePost.creator.name)
        verify(postTimeObserver, Mockito.times(2))
            .onChanged(TimeUtils.getTimeAgo(fakePost.createdAt))
        verify(isLikedObserver).onChanged(false)
        verify(isLikedObserver).onChanged(true)

        assert(postDetailsViewModel.isLiked.value == true)

    }

    @Test
    fun givenPostLiked_onLikeCall_shouldUnlikePost() {

        val currentUser = Post.User(
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

        postDetailsViewModel.postDetail.postValue(fakePost)

        doReturn(true)
            .`when`(networkHelper)
            .isNetworkConnected()

        assert(postDetailsViewModel.isLiked.value == true)

        fakePost.likedBy?.remove(currentUser)

        doReturn(Single.just(fakePost))
            .`when`(postRepository)
            .makeUnlikePost(fakePost, user)

        postDetailsViewModel.onLikeClick()

        testScheduler.triggerActions()

        verify(nameObserver, Mockito.times(2)).onChanged(fakePost.creator.name)
        verify(postTimeObserver, Mockito.times(2))
            .onChanged(TimeUtils.getTimeAgo(fakePost.createdAt))
        verify(isLikedObserver).onChanged(true)
        verify(isLikedObserver).onChanged(false)

        assert(postDetailsViewModel.isLiked.value == false)

    }

    @Test
    fun givenNoInternet_shouldShowNetworkConnectionError() {
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

        postDetailsViewModel.postDetail.postValue(fakePost)

        doReturn(false)
            .`when`(networkHelper)
            .isNetworkConnected()

        postDetailsViewModel.onLikeClick()

        testScheduler.triggerActions()

        Mockito.verify(messageStringIdObserver)
            .onChanged(Resource.error(R.string.network_connection_error))
        assert(postDetailsViewModel.messageStringId.value == Resource.error(R.string.network_connection_error))
    }


    @After
    fun tearDown() {
        postDetailsViewModel.apply {
            name.removeObserver(nameObserver)
            loading.removeObserver(loadingObserver)
            postTime.removeObserver(postTimeObserver)
            likesCount.removeObserver(likesCountObserver)
            isLiked.removeObserver(isLikedObserver)
            profileImage.removeObserver(profileImageObserver)
            postImageDetail.removeObserver(postImageDetailObserver)
            messageStringId.removeObserver(messageStringIdObserver)
        }
    }

}