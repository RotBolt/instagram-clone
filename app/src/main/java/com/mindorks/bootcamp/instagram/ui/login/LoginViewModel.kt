package com.mindorks.bootcamp.instagram.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.common.Status
import com.mindorks.bootcamp.instagram.utils.common.Validator
import com.mindorks.bootcamp.instagram.utils.common.Validator.Validation.Field
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class LoginViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelperImpl: NetworkHelper,
    private val userRepository: UserRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelperImpl) {

    private val validationResult = MutableLiveData<Validator.ValidationResult>()

    val launchMain: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()

    val emailField: MutableLiveData<String> = MutableLiveData()
    val passwordField: MutableLiveData<String> = MutableLiveData()
    val loggingIn = MutableLiveData<Boolean>()

    val emailValidation: LiveData<Resource<Int>> = filterValidationField(Field.EMAIL)
    val passwordValidation: LiveData<Resource<Int>> = filterValidationField(Field.PASSWORD)

    private fun filterValidationField(field: Field): LiveData<Resource<Int>> =
        Transformations.map(validationResult) {
            when (field) {
                Field.EMAIL -> it.email.resource
                Field.PASSWORD -> it.password.resource
            }
        }

    override fun onCreate() {

    }

    fun onEmailChanged(email: String) = emailField.postValue(email)

    fun onPasswordChanged(email: String) = passwordField.postValue(email)

    fun onResetEmailField() = emailField.postValue("")

    fun onResetPasswordField() = passwordField.postValue("")


    fun onLogin() {
        val email = emailField.value
        val password = passwordField.value

        val validation = Validator.validateLoginFields(email, password)
        // to notify its observers and transformations, switchmaps etc
        validationResult.postValue(validation)

        val emailStatus = validation.email.resource.status
        val passwordStatus = validation.password.resource.status

        if (email != null &&
            password != null &&
            emailStatus == Status.SUCCESS &&
            passwordStatus == Status.SUCCESS &&
            checkInternetConnectionWithMessage()
        ) {
            loggingIn.postValue(true)
            compositeDisposable.add(
                userRepository.doLoginUser(email, password)
                    .subscribeOn(schedulerProvider.io())
                    .subscribe({
                        userRepository.saveCurrentUser(it)
                        loggingIn.postValue(false)
                        launchMain.postValue(Event(emptyMap()))
                    }, {
                        handleNetworkError(it)
                        loggingIn.postValue(false)
                    })
            )
        }
    }

}