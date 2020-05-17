package com.mindorks.bootcamp.instagram.data.repository

import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.NetworkService
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.data.remote.response.PostListResponse
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PostRepositoryTest {

    @Mock
    private lateinit var networkSerice: NetworkService

    private lateinit var postRepository: PostRepository

    @Before
    fun setUp() {
        Networking.API_KEY = "FAKE_API_KEY"
        postRepository = PostRepository(networkSerice)
    }

    @Test
    fun fetchHomePostList_requestDoHomePostListCall() {
        val user = User(
            "id", "pui-man", "user-mail",
            "accessToken", "profilePicUrl"
        )

        doReturn(Single.just(PostListResponse("statusCode", 200, "message", listOf())))
            .`when`(networkSerice)
            .doHomePostListCall(
                "firstPostId",
                "lastPostId",
                user.id,
                user.accessToken,
                Networking.API_KEY
            )

        val data = postRepository.fetchHomePostList("firstPostId", "lastPostId", user)

        val testObserver = TestObserver<List<Post>>()
        data.subscribe(testObserver)

        testObserver.assertValue { it.isEmpty() }

        verify(networkSerice).doHomePostListCall(
            "firstPostId",
            "lastPostId",
            user.id,
            user.accessToken,
            Networking.API_KEY
        )

    }

    @After
    fun tearDown() {
    }
}