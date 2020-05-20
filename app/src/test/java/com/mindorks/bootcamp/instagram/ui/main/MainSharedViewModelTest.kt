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
class MainSharedViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var networkHelper: NetworkHelper

    @Mock
    private lateinit var homeRedirectionObserver: Observer<Event<Boolean>>

    @Mock
    private lateinit var logoutRedirectionObserver: Observer<Event<Boolean>>

    private lateinit var mainSharedViewModel: MainSharedViewModel

    @Before
    fun setup(){
        mainSharedViewModel = MainSharedViewModel(
            TestSchedulerProvider(TestScheduler()),
            CompositeDisposable(),
            networkHelper
        )

        mainSharedViewModel.apply {
            homeRedirection.observeForever(homeRedirectionObserver)
            logoutRedirection.observeForever(logoutRedirectionObserver)
        }
    }

    @Test
    fun onHomeRedirectionTest(){
        mainSharedViewModel.onHomeRedirection()
        verify(homeRedirectionObserver).onChanged(Event(true))
    }

    @Test
    fun onLogoutRedirectionTest(){
        mainSharedViewModel.onLogoutRedirection()
        verify(logoutRedirectionObserver).onChanged(Event(true))
    }


    @After
    fun tearDown(){
        mainSharedViewModel.apply {
            homeRedirection.removeObserver(homeRedirectionObserver)
            logoutRedirection.removeObserver(logoutRedirectionObserver)
        }
    }
}