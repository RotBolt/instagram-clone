package com.mindorks.bootcamp.instagram.ui.postDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.data.model.Image
import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.data.repository.PostRepository
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.common.TimeUtils
import com.mindorks.bootcamp.instagram.utils.display.ScreenResourceProvider
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class PostDetailsViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelperImpl: NetworkHelper,
    userRepository: UserRepository,
    private val postRepository: PostRepository,
    screenResourceProvider: ScreenResourceProvider
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelperImpl) {

    val postDetail = MutableLiveData<Post>()
    val loading = MutableLiveData<Boolean>()

    private val user = userRepository.getCurrentUser()!!
    private val screenWidth = screenResourceProvider.getScreenWidth()
    private val screenHeight = screenResourceProvider.getScreenHeight()

    private val headers = mapOf(
        Networking.HEADER_API_KEY to Networking.API_KEY,
        Networking.HEADER_USER_ID to user.id,
        Networking.HEADER_ACCESS_TOKEN to user.accessToken
    )

    val name: LiveData<String> = Transformations.map(postDetail) { it.creator.name }
    val postTime: LiveData<String> =
        Transformations.map(postDetail) { TimeUtils.getTimeAgo(it.createdAt) }
    val likesCount: LiveData<Int> = Transformations.map(postDetail) { it.likedBy?.size ?: 0 }
    val isLiked: LiveData<Boolean> = Transformations.map(postDetail) {
        it.likedBy?.find { postUser -> postUser.id == user.id } !== null
    }

    val profileImage: LiveData<Image> = Transformations.map(postDetail) {
        it.creator.profilePicUrl?.run { Image(this, headers) }
    }

    val postImageDetail: LiveData<Image> = Transformations.map(postDetail) {
        Image(
            it.imageUrl,
            headers,
            screenWidth,
            it.imageHeight?.let { height ->
                (calculateScaleFactor(it) * height).toInt()
            } ?: screenHeight / 3
        )
    }

    private fun calculateScaleFactor(post: Post) = post.imageWidth?.let {
        screenWidth.toFloat() / it
    } ?: 1f

    override fun onCreate() {
    }

    fun onFetchPostDetail(postId: String) {
        loading.postValue(true)
        compositeDisposable.add(
            postRepository.fetchPostDetail(postId, user)
                .subscribeOn(schedulerProvider.io())
                .subscribe({
                    loading.postValue(false)
                    postDetail.postValue(it)
                }, {
                    loading.postValue(false)
                    handleNetworkError(it)
                    messageString.postValue(Resource.error(it.message))
                })
        )
    }

    fun onLikeClick() = postDetail.value?.let {
        if (networkHelperImpl.isNetworkConnected()) {
            val api = if (isLiked.value == true) {
                postRepository.makeUnlikePost(it, user)
            } else {
                postRepository.makeLikePost(it, user)
            }
            compositeDisposable.add(
                api
                    .subscribeOn(schedulerProvider.io())
                    .subscribe({ post ->
                        postDetail.postValue(post)
                    }, { err ->
                        handleNetworkError(err)
                        messageString.postValue(Resource.error(err.message))
                    })
            )
        } else {
            messageStringId.postValue(Resource.error(R.string.network_connection_error))
        }
    }


}