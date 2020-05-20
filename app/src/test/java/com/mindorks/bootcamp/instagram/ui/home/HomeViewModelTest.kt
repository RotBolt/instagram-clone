package com.mindorks.bootcamp.instagram.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.repository.PostRepository
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.utils.TestHelper
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelperImpl
import com.mindorks.bootcamp.instagram.utils.rx.TestSchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
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
class HomeViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var networkHelper: NetworkHelperImpl

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var postRepository: PostRepository

    @Mock
    lateinit var postsObserver: Observer<Resource<List<Post>>>

    @Mock
    lateinit var refreshPostsObserver: Observer<Resource<List<Post>>>

    @Mock
    lateinit var loadingObserver: Observer<Boolean>

    lateinit var testScheduler: TestScheduler

    lateinit var homeViewModel: HomeViewModel

    lateinit var user :User

    @Before
    fun setUp() {

        user = TestHelper.getTestUser()

        doReturn(user)
            .`when`(userRepository)
            .getCurrentUser()

        testScheduler = TestScheduler()
        val testSchedulerProvider = TestSchedulerProvider(testScheduler)
        val compositeDisposable = CompositeDisposable()
        homeViewModel = HomeViewModel(
            testSchedulerProvider,
            compositeDisposable,
            networkHelper,
            userRepository,
            postRepository,
            arrayListOf(),
            PublishProcessor.create()
        )

        homeViewModel.apply {
            posts.observeForever(postsObserver)
            loading.observeForever(loadingObserver)
            refreshPostList.observeForever(refreshPostsObserver)
        }
    }

    @Test
    fun givenUserLoggedIn_whenFetch_shouldLoadPosts() {

        doReturn(true)
            .`when`(networkHelper)
            .isNetworkConnected()

        val fakePostList = listOf(
            Post(
                "id1", "url1", 100, 100,
                Post.User("uid1", "uname1", null),
                null, Date()
            ),
            Post(
                "id2", "url2", 100, 100,
                Post.User("uid2", "uname2", null),
                null, Date()
            )
        )

        doReturn(Single.just(fakePostList))
            .`when`(postRepository)
            .fetchHomePostList(null, null, user)

        homeViewModel.onCreate()

        testScheduler.triggerActions()

        verify(postsObserver).onChanged(Resource.success(fakePostList))

    }

    @Test
    fun givenUserLoggedIn_onLoadMore_shouldLoadPosts() {

        val millisInDay = 24 * 60 * 60 * 1000

        doReturn(true)
            .`when`(networkHelper)
            .isNetworkConnected()

        val fakePostList = mutableListOf(
            Post(
                "id1", "url1", 100, 100,
                Post.User("uid1", "uname1", null),
                null, Date()
            ),
            Post(
                "id2", "url2", 100, 100,
                Post.User("uid2", "uname2", null),
                null, Date(System.currentTimeMillis() - millisInDay)
            )
        )

        doReturn(Single.just(fakePostList))
            .`when`(postRepository)
            .fetchHomePostList(null, null, user)


        homeViewModel.onCreate()

        testScheduler.triggerActions()

        verify(postsObserver).onChanged(Resource.success(fakePostList))

        assert(homeViewModel.posts.value?.data == fakePostList)

        val fakePostListMore = listOf(
            Post(
                "id3", "url3", 100, 100,
                Post.User("uid3", "uname3", null),
                null, Date()
            ),
            Post(
                "id4", "url4", 100, 100,
                Post.User("uid3", "uname4", null),
                null, Date()
            )
        )


        doReturn(Single.just(fakePostListMore))
            .`when`(postRepository)
            .fetchHomePostList("id1", "id2", user)

        homeViewModel.onLoadMore()

        testScheduler.triggerActions()

        verify(postsObserver).onChanged(Resource.success(fakePostListMore))

        assert(homeViewModel.posts.value?.data == fakePostListMore)
    }


    @Test
    fun givenUserLoggedIn_whenNewPost_shouldNewPostAtTop() {

        val millisInDay = 24 * 60 * 60 * 1000

        doReturn(true)
            .`when`(networkHelper)
            .isNetworkConnected()

        val fakePostList = mutableListOf(
            Post(
                "id1", "url1", 100, 100,
                Post.User("uid1", "uname1", null),
                null, Date()
            ),
            Post(
                "id2", "url2", 100, 100,
                Post.User("uid2", "uname2", null),
                null, Date(System.currentTimeMillis() - millisInDay)
            )
        )

        doReturn(Single.just(fakePostList))
            .`when`(postRepository)
            .fetchHomePostList(null, null, user)


        homeViewModel.onCreate()

        testScheduler.triggerActions()

        verify(postsObserver).onChanged(Resource.success(fakePostList))

        assert(homeViewModel.posts.value?.data == fakePostList)

        val newPost = Post(
            "id5", "url5", 100, 100,
            Post.User("uid5", "uname5", null),
            null, Date()
        )

        homeViewModel.onNewPost(newPost)

        fakePostList.add(0, newPost)
        verify(refreshPostsObserver).onChanged(Resource.success(fakePostList))

        assert(homeViewModel.posts.value?.data?.get(0) == newPost)
        assert(homeViewModel.posts.value?.data == fakePostList)
    }

    @After
    fun tearDown() {
        homeViewModel.apply {
            posts.removeObserver(postsObserver)
            loading.removeObserver(loadingObserver)
            refreshPostList.removeObserver(refreshPostsObserver)
        }
    }


}