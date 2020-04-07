package com.mindorks.bootcamp.instagram.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.common.Status
import com.mindorks.bootcamp.instagram.utils.common.Validator
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.RxSchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import com.mindorks.bootcamp.instagram.utils.common.Validator.Validation.Field
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import timber.log.Timber

class LoginViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val userRepository: UserRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    private val validationResult = MutableLiveData<Validator.ValidationResult>()

    val launchDummy: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()

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
            passwordStatus == Status.SUCCESS
        ) {
            loggingIn.postValue(true)
            compositeDisposable.add(
                userRepository.doLoginUser(email, password)
                    .subscribeOn(schedulerProvider.io())
                    .subscribe({
                        userRepository.saveCurrentUser(it)
                        loggingIn.postValue(false)
                        launchDummy.postValue(Event(emptyMap()))
                    },{
                        handleNetworkError(it)
                        loggingIn.postValue(false)
                    })
            )
        }
    }

}