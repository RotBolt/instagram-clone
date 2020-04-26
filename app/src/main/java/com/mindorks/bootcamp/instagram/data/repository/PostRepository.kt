package com.mindorks.bootcamp.instagram.data.repository

import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.NetworkService
import com.mindorks.bootcamp.instagram.data.remote.request.PostCreationRequest
import com.mindorks.bootcamp.instagram.data.remote.request.PostLikedModifyRequest
import com.mindorks.bootcamp.instagram.data.remote.response.MyPostListResponse
import io.reactivex.Single
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val networkService: NetworkService
) {

    fun fetchHomePostList(
        firstPostId: String?,
        lastPostId: String?,
        user: User
    ): Single<List<Post>> =
        networkService.doHomePostListCall(
            firstPostId,
            lastPostId,
            user.id,
            user.accessToken
        ).map {
            it.data
        }

    fun makeLikePost(post: Post, user: User): Single<Post> {
        return networkService.doPostLikeCall(
            PostLikedModifyRequest(post.id),
            user.id,
            user.accessToken
        ).map {
            post.likedBy?.apply {
                // adding the current user in like list, if not present
                this.find { it.id == user.id } ?: this.add(
                    Post.User(
                        user.id,
                        user.name,
                        user.profilePicUrl
                    )
                )
            }
            return@map post
        }
    }

    fun makeUnlikePost(post: Post, user: User): Single<Post> {
        return networkService.doPostUnlikeCall(
            PostLikedModifyRequest(post.id),
            user.id,
            user.accessToken
        ).map {
            // removing the current user from like list
            post.likedBy?.apply {
                this.find { it.id == user.id }?.let { this.remove(it) }
            }
            return@map post
        }
    }

    fun createPost(imgUrl: String, imgWidth: Int, imgHeight: Int, user: User): Single<Post> =
        networkService.doPostCreateCall(
            PostCreationRequest(imgUrl, imgWidth, imgHeight),
            user.id,
            user.accessToken
        ).map {
            return@map with(it.data) {
                Post(
                    id,
                    imageUrl,
                    imageWidth,
                    imageHeight,
                    Post.User(
                        user.id,
                        user.name,
                        user.profilePicUrl
                    ),
                    mutableListOf(),
                    createdAt
                )
            }
        }

    fun fetchMyPostList(user: User): Single<List<MyPostListResponse.MyPost>> =
        networkService.doFetchMyPostsCall(user.id, user.accessToken)
            .map { it.data }

    fun fetchPostDetail(postId: String, user: User): Single<Post> =
        networkService.doPostDetailCall(postId, user.id, user.accessToken)
            .map { it.data }
}