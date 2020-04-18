package com.mindorks.bootcamp.instagram.ui.main

import androidx.lifecycle.MutableLiveData
import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class MainSharedViewModel (
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper){

    val homeRedirection = MutableLiveData<Event<Boolean>>()
    val newPost = MutableLiveData<Event<Post>>()

    override fun onCreate() {}

    fun onHomeRedirection(){
        homeRedirection.postValue(Event(true))
    }
}