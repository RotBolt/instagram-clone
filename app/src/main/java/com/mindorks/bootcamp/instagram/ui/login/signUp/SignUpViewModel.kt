package com.mindorks.bootcamp.instagram.ui.login.signUp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.common.Status
import com.mindorks.bootcamp.instagram.utils.common.Validator
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class SignUpViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelperImpl: NetworkHelper,
    private val userRepository: UserRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelperImpl) {

    private val validationResult = MutableLiveData<Validator.ValidationResult>()

    val launchMain: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()

    val emailField = MutableLiveData<String>()
    val passwordField = MutableLiveData<String>()
    val nameField = MutableLiveData<String>()

    val loggingIn = MutableLiveData<Boolean>()

    val emailValidation: LiveData<Resource<Int>> = filterValidationField(Validator.Validation.Field.EMAIL)
    val passwordValidation: LiveData<Resource<Int>> = filterValidationField(Validator.Validation.Field.PASSWORD)

    private fun filterValidationField(field: Validator.Validation.Field): LiveData<Resource<Int>> =
        Transformations.map(validationResult) {
            when (field) {
                Validator.Validation.Field.EMAIL -> it.email.resource
                Validator.Validation.Field.PASSWORD -> it.password.resource
            }
        }

    override fun onCreate() {}

    fun onEmailChanged(email: String) = emailField.postValue(email)

    fun onPasswordChanged(email: String) = passwordField.postValue(email)

    fun onNameChanged(name: String) = nameField.postValue(name)


    fun onResetEmailField() = emailField.postValue("")

    fun onResetPasswordField() = passwordField.postValue("")

    fun onResetNameField() = nameField.postValue("")

    fun onSignUp() {
        val email = emailField.value
        val password = passwordField.value
        val name = nameField.value

        val validation = Validator.validateLoginFields(email, password)
        // to notify its observers and transformations, switchmaps etc
        validationResult.postValue(validation)

        val emailStatus = validation.email.resource.status
        val passwordStatus = validation.password.resource.status

        if (email != null &&
            password != null &&
            name != null &&
            emailStatus == Status.SUCCESS &&
            passwordStatus == Status.SUCCESS &&
            checkInternetConnectionWithMessage()
        ) {
            loggingIn.postValue(true)
            compositeDisposable.addAll(
                userRepository.doSignUpUser(email, password, name)
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