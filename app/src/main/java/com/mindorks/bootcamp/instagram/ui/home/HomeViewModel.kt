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
    networkHelperImpl: NetworkHelper,
    userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val allPosts: ArrayList<Post>,
    private val paginator: PublishProcessor<Pair<String?, String?>>
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelperImpl) {

    val loading = MutableLiveData<Boolean>()
    val posts = MutableLiveData<Resource<List<Post>>>()

    private val user = userRepository.getCurrentUser()!!

    val refreshPostList = MutableLiveData<Resource<List<Post>>>()

    var firstPostId: String? = null
    var lastPostId: String? = null

    init {
        compositeDisposable.add(
            paginator
                .onBackpressureDrop()
                .doOnNext {
                    loading.postValue(true)
                }
                .concatMapSingle { pageIds ->
                    return@concatMapSingle postRepository.fetchHomePostList(
                        pageIds.first,
                        pageIds.second,
                        user
                    )
                        .subscribeOn(schedulerProvider.io())
                        .doOnError {
                            handleNetworkError(it)
                        }
                }
                .subscribe(
                    {
                        allPosts.addAll(it)
                        firstPostId = allPosts.maxBy { post -> post.createdAt }?.id
                        lastPostId = allPosts.minBy { post -> post.createdAt }?.id

                        loading.postValue(false)
                        posts.postValue(Resource.success(it))
                    }, {
                        handleNetworkError(it)
                    }
                )
        )
    }

    override fun onCreate() {
        loadMorePosts()
    }

    private fun loadMorePosts() {
        if (checkInternetConnectionWithMessage()) paginator.onNext(Pair(firstPostId, lastPostId))
    }

    fun onLoadMore() {
        if (loading.value !== null && loading.value == false) loadMorePosts()
    }

    fun onNewPost(post: Post) {
        allPosts.add(0, post)
        refreshPostList.postValue(Resource.success(mutableListOf<Post>().apply { addAll(allPosts) }))
    }
}