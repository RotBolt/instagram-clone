package com.mindorks.bootcamp.instagram.ui.profile.myPosts

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.mindorks.bootcamp.instagram.data.model.Image
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.data.remote.response.MyPostListResponse
import com.mindorks.bootcamp.instagram.data.repository.PostRepository
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseItemViewModel
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MyPostItemViewModel @Inject constructor (
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
): BaseItemViewModel<MyPostListResponse.MyPost>(schedulerProvider, compositeDisposable, networkHelper){

    private val user = userRepository.getCurrentUser()!!

    private val headers = mapOf(
        Networking.HEADER_API_KEY to Networking.API_KEY,
        Networking.HEADER_USER_ID to user.id,
        Networking.HEADER_ACCESS_TOKEN to user.accessToken
    )

    override fun onCreate() {

    }

    val myPostImageDetails :LiveData<Image> = Transformations.map(data){
        Image(it.imgUrl, headers)
    }

    fun onMyPostClick(){

    }


}