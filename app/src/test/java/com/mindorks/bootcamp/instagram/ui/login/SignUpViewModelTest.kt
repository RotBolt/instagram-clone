package com.mindorks.bootcamp.instagram.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.login.signUp.SignUpViewModel
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelperImpl
import com.mindorks.bootcamp.instagram.utils.rx.TestSchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SignUpViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var networkHelperImpl: NetworkHelperImpl

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var launchMainObserver: Observer<Event<Map<String, String>>>

    @Mock
    lateinit var logginInObserver: Observer<Boolean>

    @Mock
    lateinit var emailFieldObserver:Observer<String>

    @Mock
    lateinit var nameFieldObserver:Observer<String>

    @Mock
    lateinit var passwordFieldObserver:Observer<String>

    @Mock
    lateinit var messageStringIdObserver: Observer<Resource<Int>>

    private lateinit var testScheduler: TestScheduler

    private lateinit var signUpViewModel: SignUpViewModel

    @Before
    fun setUp() {
        val compositeDisposable = CompositeDisposable()
        testScheduler = TestScheduler()
        val testSchedulerProvider = TestSchedulerProvider(testScheduler)
        signUpViewModel = SignUpViewModel(
            testSchedulerProvider,
            compositeDisposable,
            networkHelperImpl,
            userRepository
        )

        signUpViewModel.apply {
            loggingIn.observeForever(logginInObserver)
            launchMain.observeForever(launchMainObserver)
            messageStringId.observeForever(messageStringIdObserver)
            emailField.observeForever(emailFieldObserver)
            nameField.observeForever(nameFieldObserver)
            passwordField.observeForever(passwordFieldObserver)
        }
    }

    @Test
    fun givenServerResponse200_whenSignUp_shouldLaunchMain() {
        val name = "haruka"
        val email = "haruka@pui.com"
        val password = "puipuipui"
        val user = User("id", name, email, "accessToken")

        signUpViewModel.onEmailChanged(email)
        signUpViewModel.onNameChanged(name)
        signUpViewModel.onPasswordChanged(password)

        doReturn(true)
            .`when`(networkHelperImpl)
            .isNetworkConnected()

        doReturn(Single.just(user))
            .`when`(userRepository)
            .doSignUpUser(email, password, name)

        signUpViewModel.onSignUp()
        testScheduler.triggerActions()

        verify(userRepository).saveCurrentUser(user)
        assert(signUpViewModel.loggingIn.value == false)
        assert(signUpViewModel.launchMain.value == Event(hashMapOf<String,String>()))

        verify(logginInObserver).onChanged(true)
        verify(logginInObserver).onChanged(false)

        verify(launchMainObserver).onChanged(Event(hashMapOf()))
    }

    @Test
    fun givenNoInternet_whenSignUp_shouldShowNetworkError(){
        val name = "haruka"
        val email = "haruka@pui.com"
        val password = "puipuipui"

        signUpViewModel.onEmailChanged(email)
        signUpViewModel.onNameChanged(name)
        signUpViewModel.onPasswordChanged(password)

        doReturn(false)
            .`when`(networkHelperImpl)
            .isNetworkConnected()

        signUpViewModel.onSignUp()
        assert(signUpViewModel.messageStringId.value == Resource.error(R.string.network_connection_error))
        verify(messageStringIdObserver).onChanged(Resource.error(R.string.network_connection_error))
    }

    @Test
    fun givenReset_shouldClearFields(){
        val name = "haruka"
        val email = "haruka@pui.com"
        val password = "puipuipui"

        signUpViewModel.onEmailChanged(email)
        signUpViewModel.onNameChanged(name)
        signUpViewModel.onPasswordChanged(password)

        assert(signUpViewModel.emailField.value == "haruka@pui.com")
        assert(signUpViewModel.passwordField.value == "puipuipui")
        assert(signUpViewModel.nameField.value == "haruka")

        signUpViewModel.apply {
            onResetNameField()
            onResetPasswordField()
            onResetEmailField()
        }

        assert(signUpViewModel.emailField.value == "")
        assert(signUpViewModel.passwordField.value == "")
        assert(signUpViewModel.nameField.value == "")

        verify(emailFieldObserver).onChanged(email)
        verify(emailFieldObserver).onChanged("")

        verify(passwordFieldObserver).onChanged(password)
        verify(passwordFieldObserver).onChanged("")

        verify(nameFieldObserver).onChanged(name)
        verify(nameFieldObserver).onChanged("")
    }

    fun tearDown(){
        signUpViewModel.apply {
            loggingIn.removeObserver(logginInObserver)
            launchMain.removeObserver(launchMainObserver)
            messageStringId.removeObserver(messageStringIdObserver)
            emailField.removeObserver(emailFieldObserver)
            nameField.removeObserver(nameFieldObserver)
            passwordField.removeObserver(passwordFieldObserver)
        }
    }
}