package com.mindorks.bootcamp.instagram.ui.splash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.utils.TestHelper
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
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SplashViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var networkHelper: NetworkHelper

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var launchMainObserver: Observer<Event<Map<String,String>>>

    @Mock
    private lateinit var launchLoginObserver: Observer<Event<Map<String,String>>>

    private lateinit var splashViewModel: SplashViewModel

    @Before
    fun setup(){
        splashViewModel = SplashViewModel(
            TestSchedulerProvider(TestScheduler()),
            CompositeDisposable(),
            networkHelper,
            userRepository
        )

        splashViewModel.apply {
            launchLogin.observeForever(launchLoginObserver)
            launchMain.observeForever(launchMainObserver)
        }
    }

    @Test
    fun givenUserNull_shouldLaunchLogin(){
        doReturn(null)
            .`when`(userRepository)
            .getCurrentUser()
        splashViewModel.onCreate()
        verify(launchLoginObserver).onChanged(Event(emptyMap()))
    }

    @Test
    fun givenUser_shouldLaunchLogin(){
        doReturn(TestHelper.getTestUser())
            .`when`(userRepository)
            .getCurrentUser()
        splashViewModel.onCreate()
        verify(launchMainObserver).onChanged(Event(emptyMap()))
    }

    @After
    fun tearDown(){
        splashViewModel.apply {
            launchLogin.removeObserver(launchLoginObserver)
            launchMain.removeObserver(launchMainObserver)
        }
    }
}