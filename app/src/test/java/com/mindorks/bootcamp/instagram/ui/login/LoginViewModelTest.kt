package com.mindorks.bootcamp.instagram.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.R

import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelperImpl
import com.mindorks.bootcamp.instagram.utils.rx.TestSchedulerProvider
import io.reactivex.Single
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
class LoginViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var networkHelperImpl: NetworkHelperImpl

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var loggingInObserver: Observer<Boolean>

    @Mock
    private lateinit var launchMainObserver: Observer<Event<Map<String, String>>>

    @Mock
    private lateinit var messageStringIdObserver: Observer<Resource<Int>>

    private lateinit var testScheduler: TestScheduler

    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        // did not mock, as no need to behaviour change
        val compositeDisposable = CompositeDisposable()
        testScheduler = TestScheduler()
        val testSchedulerProvider = TestSchedulerProvider(testScheduler)
        loginViewModel = LoginViewModel(
            testSchedulerProvider,
            compositeDisposable,
            networkHelperImpl,
            userRepository
        )

        loginViewModel.apply {
            loggingIn.observeForever(loggingInObserver)
            messageStringId.observeForever(messageStringIdObserver)
            launchMain.observeForever(launchMainObserver)
        }
    }

    @Test
    fun givenServerResponse200_whenLogin_shouldLaunchMain() {
        val email = "test@pui.com"
        val password = "puipuipui"
        val user = User("id", "pui-man", email, "accessToken")
        loginViewModel.onEmailChanged(email)
        loginViewModel.onPasswordChanged(password)

        doReturn(true)
            .`when`(networkHelperImpl)
            .isNetworkConnected()
        doReturn(Single.just(user))
            .`when`(userRepository)
            .doLoginUser(email, password)
        loginViewModel.onLogin()

        // trigger the actions on scheduler which were present before
        testScheduler.triggerActions()
        verify(userRepository).saveCurrentUser(user)
        assert(loginViewModel.loggingIn.value == false)
        assert(loginViewModel.launchMain.value == Event(hashMapOf<String, String>()))

        // because logginIn liveData has been changed with true & false
        verify(loggingInObserver).onChanged(true)
        verify(loggingInObserver).onChanged(false)

        verify(launchMainObserver).onChanged(Event(hashMapOf()))
    }

    @Test
    fun givenNoInternet_whenLogin_shouldShowNetworkError() {
        val email = "test@pui.com"
        val password = "puipuipui"

        loginViewModel.onEmailChanged(email)
        loginViewModel.onPasswordChanged(password)

        doReturn(false)
            .`when`(networkHelperImpl)
            .isNetworkConnected()

        loginViewModel.onLogin()
        assert(loginViewModel.messageStringId.value == Resource.error(R.string.network_connection_error))
        verify(messageStringIdObserver).onChanged(Resource.error(R.string.network_connection_error))
    }

    @After
    fun tearDown() {
        loginViewModel.apply {
            loggingIn.removeObserver(loggingInObserver)
            messageStringId.removeObserver(messageStringIdObserver)
            launchMain.removeObserver(launchMainObserver)
        }
    }


}