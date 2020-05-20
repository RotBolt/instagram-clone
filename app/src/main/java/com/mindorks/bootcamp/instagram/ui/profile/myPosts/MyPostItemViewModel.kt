package com.mindorks.bootcamp.instagram.ui.profile.myPosts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mindorks.bootcamp.instagram.data.model.Image
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.data.remote.response.MyPostListResponse
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseItemViewModel
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MyPostItemViewModel @Inject constructor(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelperImpl: NetworkHelper,
    userRepository: UserRepository
) : BaseItemViewModel<MyPostListResponse.MyPost>(
    schedulerProvider,
    compositeDisposable,
    networkHelperImpl
) {

    private val user = userRepository.getCurrentUser()!!

    private var postId: String? = null
    val launchPostDetail = MutableLiveData<Event<String?>>()

    private val headers = mapOf(
        Networking.HEADER_API_KEY to Networking.API_KEY,
        Networking.HEADER_USER_ID to user.id,
        Networking.HEADER_ACCESS_TOKEN to user.accessToken
    )

    override fun onCreate() {}

    val myPostImageDetails: LiveData<Image> = Transformations.map(data) {
        postId = it.id
        Image(it.imgUrl, headers)
    }

    fun onMyPostClick() {
        launchPostDetail.postValue(Event(postId))
    }


}