package com.mindorks.bootcamp.instagram.data.remote

import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.data.remote.request.*
import com.mindorks.bootcamp.instagram.data.remote.response.*
import io.reactivex.Single
import okhttp3.MultipartBody
import java.util.*

class FakeNetworkService : NetworkService {
    override fun doDummyCall(request: DummyRequest, apiKey: String): Single<DummyResponse> {
        TODO("Not yet implemented")
    }

    override fun doLoginCall(loginRequest: LoginRequest, apiKey: String): Single<LoginResponse> {
        return Single.just(
            LoginResponse(
                "statusCode",
                200,
                "message",
                "accessToken",
                "userId",
                "userName",
                "userEmail",
                "profilePicUrl"
            )
        )
    }

    override fun doSignUpCall(
        signUpRequest: SignUpRequest,
        apiKey: String
    ): Single<SignUpResponse> {
        return Single.just(
            SignUpResponse(
                "success",
                200,
                "message",
                "accessToken",
                "refreshToken",
                "userId",
                "userName",
                "userEmail"
            )
        )
    }

    override fun doHomePostListCall(
        firstPostId: String?,
        lastPostId: String?,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<PostListResponse> {

        val creator1 = Post.User("userId1", "name1", "profilePicurl1")
        val creator2 = Post.User("userId2", "name2", "profilePicurl2")

        val likedBy = mutableListOf<Post.User>().apply {
            Post.User("userId3", "name3", "profilePicurl3")
            Post.User("userId4", "name4", "profilePicurl4")
        }

        val post1 = Post("postId1", "postUrl1", 400, 400, creator1, likedBy, Date())
        val post2 = Post("postId2", "postUrl2", 400, 400, creator2, likedBy, Date())

        val postListResponse = PostListResponse("statusCode", 200, "message", listOf(post1, post2))

        return Single.just(postListResponse)
    }

    override fun doPostLikeCall(
        request: PostLikedModifyRequest,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<GeneralResponse> {
        TODO("Not yet implemented")
    }

    override fun doPostUnlikeCall(
        request: PostLikedModifyRequest,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<GeneralResponse> {
        TODO("Not yet implemented")
    }

    override fun doImageUploadCall(
        image: MultipartBody.Part,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<ImageResponse> {
        TODO("Not yet implemented")
    }

    override fun doPostCreateCall(
        request: PostCreationRequest,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<PostCreationResponse> {
        TODO("Not yet implemented")
    }

    override fun doFetchUserInfoCall(
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<UserInfoResponse> {
        return Single.just(
            UserInfoResponse(
                "success",
                200,
                "message",
                UserInfoResponse.UserInfo(
                    "id",
                    "haruka",
                    null,
                    "Pui pui"
                )
            )
        )
    }

    override fun doLogOutCall(
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<GeneralResponse> {
        return Single.just(
            GeneralResponse(
                "success",
                "message"
            )
        )
    }

    override fun doFetchMyPostsCall(
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<MyPostListResponse> {
       return Single.just(
            MyPostListResponse(
                "success",
                200,
                "message",
                listOf(
                    MyPostListResponse.MyPost(
                        "id1",
                        "https://cloudStorage.api.com/dhek/pui.jpeg",
                        300,
                        400,
                        Date()
                    ),
                    MyPostListResponse.MyPost(
                        "id2",
                        "https://cloudStorage.api.com/dhek/pui7.jpeg",
                        300,
                        400,
                        Date()
                    )
                )
            )
        )
    }

    override fun doUpdateUserInfoCall(
        updateInfoBody: UpdateInfoRequest,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<GeneralResponse> {
        TODO("Not yet implemented")
    }

    override fun doPostDetailCall(
        postId: String,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<PostDetailResponse> {
        TODO("Not yet implemented")
    }

}