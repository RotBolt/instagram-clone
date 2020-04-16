package com.mindorks.bootcamp.instagram.ui.home

import androidx.lifecycle.MutableLiveData
import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.data.repository.PostRepository
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor

class HomeViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val allPosts : ArrayList<Post>,
    private val paginator:PublishProcessor<Pair<String?,String?>>
):BaseViewModel(schedulerProvider, compositeDisposable, networkHelper){

    val loading = MutableLiveData<Boolean>()
    val posts = MutableLiveData<Resource<List<Post>>>()

    private val user = userRepository.getCurrentUser()!!

    init {
        compositeDisposable.add(
            paginator
                .onBackpressureDrop()
                .doOnNext{
                    loading.postValue(true)
                }
                .concatMapSingle { pageIds ->
                    return@concatMapSingle postRepository.fetchHomePostList(pageIds.first, pageIds.second, user)
                        .subscribeOn(schedulerProvider.io())
                        .doOnError {
                            handleNetworkError(it)
                        }
                }
                .subscribe(
                    {
                        allPosts.addAll(it)
                        loading.postValue(false)
                        posts.postValue(Resource.success(it))
                    },{
                        handleNetworkError(it)
                    }
                )
        )
    }
    override fun onCreate() {
        loadMorePosts()
    }

    private fun loadMorePosts(){
        val firstPostId = if(allPosts.isNotEmpty()) allPosts[0].id else null
        val lastPostId = if(allPosts.size > 1) allPosts[allPosts.size -1].id else null
        if(checkInternetConnectionWithMessage()) paginator.onNext(Pair(firstPostId,lastPostId))
    }


    fun onLoadMore(){
        if(loading.value !== null && loading.value == false) loadMorePosts()
    }
}