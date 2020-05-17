package com.mindorks.bootcamp.instagram.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.data.model.Image
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.data.remote.response.MyPostListResponse
import com.mindorks.bootcamp.instagram.data.remote.response.UserInfoResponse
import com.mindorks.bootcamp.instagram.data.repository.PostRepository
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.common.Constants
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class ProfileViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelperImpl: NetworkHelper,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelperImpl) {

    private val user = userRepository.getCurrentUser()!!

    val logout = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
    val loadingMyPosts = MutableLiveData<Boolean>()

    private val headers = mapOf(
        Networking.HEADER_API_KEY to Networking.API_KEY,
        Networking.HEADER_USER_ID to user.id,
        Networking.HEADER_ACCESS_TOKEN to user.accessToken
    )
    private val userInfo = MutableLiveData<UserInfoResponse.UserInfo>()

    val userName: LiveData<String> = Transformations.map(userInfo) { it.name }
    val tagLine: LiveData<String> = Transformations.map(userInfo) {
        it.tagline ?: "No Bio Set"
    }
    val profilePic: LiveData<Image?> = Transformations.map(userInfo) {
        it.profilePicUrl?.run {
            Image(this, headers)
        }
    }

    val myAllPosts = MutableLiveData<List<MyPostListResponse.MyPost>>()

    override fun onCreate() {
        onFetchUserInfo()
        onFetchMyPosts()
    }

    fun onLogout() {
        loading.postValue(true)
        compositeDisposable.add(
            userRepository.doLogoutUser(user)
                .subscribeOn(schedulerProvider.io())
                .subscribe(
                    {
                        loading.postValue(false)
                        if (it.statusCode == Constants.STATUS_SUCESS) {
                            logout.postValue(true)
                            userRepository.removeCurrentUser()
                        } else {
                            messageStringId.postValue(Resource.error(R.string.network_default_error))
                        }
                    }, {
                        loading.postValue(false)
                        handleNetworkError(it)
                        messageString.postValue(Resource.error(it.message))
                    }
                )
        )
    }

    fun onFetchUserInfo() {
        loading.postValue(true)
        compositeDisposable.add(
            userRepository.doFetchUserInfo(user)
                .subscribeOn(schedulerProvider.io())
                .subscribe({
                    loading.postValue(false)
                    userInfo.postValue(it)
                }, {
                    loading.postValue(false)
                    handleNetworkError(it)
                    messageString.postValue(Resource.error(it.message))
                })
        )
    }

    fun onFetchMyPosts() {
        loadingMyPosts.postValue(true)
        compositeDisposable.add(
            postRepository.fetchMyPostList(user)
                .subscribeOn(schedulerProvider.io())
                .subscribe({
                    loadingMyPosts.postValue(false)
                    myAllPosts.postValue(it)
                }, {
                    loadingMyPosts.postValue(false)
                    handleNetworkError(it)
                    messageString.postValue(Resource.error(it.message))
                })
        )
    }


}