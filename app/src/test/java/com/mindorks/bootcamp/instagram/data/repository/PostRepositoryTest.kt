package com.mindorks.bootcamp.instagram.data.repository

import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.NetworkService
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.data.remote.request.PostCreationRequest
import com.mindorks.bootcamp.instagram.data.remote.request.PostLikedModifyRequest
import com.mindorks.bootcamp.instagram.data.remote.response.*
import com.mindorks.bootcamp.instagram.utils.TestHelper
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
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class PostRepositoryTest {

    @Mock
    private lateinit var networkSerice: NetworkService

    private lateinit var postRepository: PostRepository

    private lateinit var user: User

    @Before
    fun setUp() {
        Networking.API_KEY = "FAKE_API_KEY"
        user = TestHelper.getTestUser()
        postRepository = PostRepository(networkSerice)
    }

    @Test
    fun fetchHomePostList_requestDoHomePostListCall() {

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

    @Test
    fun makeLikePost_requestDoPostLikeCall() {
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

        val generalResponse = GeneralResponse("success", "Ok")
        doReturn(Single.just(generalResponse))
            .`when`(networkSerice)
            .doPostLikeCall(
                PostLikedModifyRequest(fakePost.id),
                user.id,
                user.accessToken
            )

        val data = postRepository.makeLikePost(fakePost, user)
        val testObserver = TestObserver<Post>()
        data.subscribe(testObserver)

        testObserver.assertValue {
            it.likedBy?.find { postUser ->
                postUser.id == user.id
            } != null
        }

        verify(networkSerice).doPostLikeCall(
            PostLikedModifyRequest(fakePost.id),
            user.id,
            user.accessToken
        )
    }

    @Test
    fun makeUnLikePost_requestDoPostUnLikeCall() {
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
                Post.User(
                    user.id,
                    user.name,
                    user.profilePicUrl
                )
            ),
            Date()
        )

        val generalResponse = GeneralResponse("success", "Ok")
        doReturn(Single.just(generalResponse))
            .`when`(networkSerice)
            .doPostUnlikeCall(
                PostLikedModifyRequest(fakePost.id),
                user.id,
                user.accessToken
            )

        val data = postRepository.makeUnlikePost(fakePost, user)
        val testObserver = TestObserver<Post>()
        data.subscribe(testObserver)

        testObserver.assertValue {
            it.likedBy?.find { postUser ->
                postUser.id == user.id
            } == null
        }

        verify(networkSerice).doPostUnlikeCall(
            PostLikedModifyRequest(fakePost.id),
            user.id,
            user.accessToken
        )
    }

    @Test
    fun createPost_requestCreatePost() {
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
                Post.User(
                    user.id,
                    user.name,
                    user.profilePicUrl
                )
            ),
            Date()
        )

        val postCreationResponse = PostCreationResponse(
            "success",
            200,
            "ok",
            PostCreationResponse.PostData(
                fakePost.id,
                fakePost.imageUrl,
                fakePost.imageWidth,
                fakePost.imageHeight,
                fakePost.createdAt
            )
        )

        doReturn(Single.just(postCreationResponse))
            .`when`(networkSerice)
            .doPostCreateCall(
                PostCreationRequest(
                    fakePost.imageUrl,
                    fakePost.imageWidth!!,
                    fakePost.imageHeight!!
                ),
                user.id,
                user.accessToken
            )

        val data = postRepository.createPost(
            fakePost.imageUrl,
            fakePost.imageWidth!!,
            fakePost.imageHeight!!,
            user
        )

        val testObserver = TestObserver<Post>()

        data.subscribe(testObserver)

        testObserver.assertValue {
            it.id == fakePost.id && it.creator.id == user.id
        }

        verify(networkSerice).doPostCreateCall(
            PostCreationRequest(
                fakePost.imageUrl,
                fakePost.imageWidth!!,
                fakePost.imageHeight!!
            ),
            user.id,
            user.accessToken
        )
    }

    @Test
    fun fetchMyPostList_requestMyPostsCall() {
        val fakeMyPosts = listOf(
            MyPostListResponse.MyPost(
                "id1",
                "https://cloudstorage.api.com/9990b/pui.jpeg",
                300,
                400,
                Date()
            )
        )
        val myPostListResponse = MyPostListResponse(
            "success",
            200,
            "ok",
            fakeMyPosts
        )
        doReturn(Single.just(myPostListResponse))
            .`when`(networkSerice)
            .doFetchMyPostsCall(user.id, user.accessToken)

        val data = postRepository.fetchMyPostList(user)

        val testObserver = TestObserver<List<MyPostListResponse.MyPost>>()
        data.subscribe(testObserver)

        testObserver.assertComplete()
        testObserver.assertValue { it.isNotEmpty() }
        testObserver.assertValue { it.size == 1 }

        verify(networkSerice).doFetchMyPostsCall(user.id, user.accessToken)
    }

    @Test
    fun fetchPostDetail_requestPostDetailCall() {
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

        val postDetailResponse = PostDetailResponse(
            "success",
            200,
            "ok",
            fakePost
        )

        doReturn(Single.just(postDetailResponse))
            .`when`(networkSerice)
            .doPostDetailCall(fakePost.id, user.id, user.accessToken)

        val data = postRepository.fetchPostDetail(fakePost.id, user)

        val testObserver = TestObserver<Post>()
        data.subscribe(testObserver)

        testObserver.assertComplete()
        testObserver.assertValue {
            it.id == fakePost.id
        }

        verify(networkSerice).doPostDetailCall(fakePost.id, user.id, user.accessToken)
    }

    @After
    fun tearDown() {
    }
}