package com.mindorks.bootcamp.instagram.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.TestSchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var networkHelper: NetworkHelper

    @Mock
    lateinit var profileNavObserver: Observer<Event<Boolean>>

    @Mock
    lateinit var homeNavObserver: Observer<Event<Boolean>>

    @Mock
    lateinit var photoNavObserver: Observer<Event<Boolean>>

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setup() {
        mainViewModel = MainViewModel(
            TestSchedulerProvider(TestScheduler()),
            CompositeDisposable(),
            networkHelper
        )

        mainViewModel.apply {
            profileNavigation.observeForever(profileNavObserver)
            homeNavigation.observeForever(homeNavObserver)
            photoNavigation.observeForever(photoNavObserver)
        }
    }

    @Test
    fun onNavToHomeFragment(){
        mainViewModel.onHomeSelected()
        verify(homeNavObserver).onChanged(Event(true))
    }

    @Test
    fun onNavToPhotoFragment(){
        mainViewModel.onPhotoSelected()
        verify(photoNavObserver).onChanged(Event(true))
    }

    @Test
    fun onNavToProfileFragment(){
        mainViewModel.onProfileSelected()
        verify(profileNavObserver).onChanged(Event(true))
    }

    @Test
    fun onCreate(){
        mainViewModel.onCreate()
        verify(homeNavObserver).onChanged(Event(true))
    }

    @After
    fun tearDown(){
        mainViewModel.apply {
            profileNavigation.removeObserver(profileNavObserver)
            homeNavigation.removeObserver(homeNavObserver)
            photoNavigation.removeObserver(photoNavObserver)
        }
    }

}