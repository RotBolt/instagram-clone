package com.mindorks.bootcamp.instagram.ui.profile

import androidx.lifecycle.MutableLiveData
import com.mindorks.bootcamp.instagram.data.remote.request.UpdateInfoRequest
import com.mindorks.bootcamp.instagram.data.repository.PhotoRepository
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.common.FileHelper
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.io.File
import java.io.InputStream

class EditProfileViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelperImpl: NetworkHelper,
    private val userRepository: UserRepository,
    private val photoRepository: PhotoRepository,
    private val directory: File,
    private val fileHelper: FileHelper
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelperImpl) {

    private val user = userRepository.getCurrentUser()!!
    val loading = MutableLiveData<Boolean>()
    val nameField = MutableLiveData<String>()
    val bioField = MutableLiveData<String>()
    val imageFile = MutableLiveData<File?>()
    val emailText = MutableLiveData<String>()
    val profilePicUrl = MutableLiveData<String>()
    val successUpload = MutableLiveData<Event<Boolean>>()
    val nameValidator = MutableLiveData<Pair<Boolean, String>>()

    override fun onCreate() {
        emailText.postValue(user.email)
    }

    fun onNameChanged(name: String) = nameField.postValue(name)

    fun onBioChanged(bio: String) = bioField.postValue(bio)

    fun onProfilePicUrlReceived(url: String) = profilePicUrl.postValue(url)


    fun onUploadDetails() {
        val name = nameField.value
        val bio = bioField.value ?: ""
        val file = imageFile.value
        val url = profilePicUrl.value
        if (name != null && name.isNotEmpty()) {
            if (file != null) {
                nameValidator.postValue(true to "validated")
                loading.postValue(true)
                compositeDisposable.add(
                    photoRepository.uploadImage(file, user)
                        .flatMap {
                            userRepository.doUpdateUserInfo(
                                UpdateInfoRequest(
                                    name,
                                    it,
                                    bio
                                ),
                                user
                            )
                        }
                        .subscribeOn(schedulerProvider.io())
                        .subscribe({
                            loading.postValue(false)
                            successUpload.postValue(Event(true))
                        }, {
                            loading.postValue(false)
                            messageString.postValue(Resource.error(it.message))
                        })
                )
            } else {
                nameValidator.postValue(true to "validated")
                loading.postValue(true)
                compositeDisposable.add(
                    userRepository.doUpdateUserInfo(
                        UpdateInfoRequest(
                            name,
                            url,
                            bio
                        ),
                        user
                    )
                        .subscribeOn(schedulerProvider.io())
                        .subscribe({
                            loading.postValue(false)
                            successUpload.postValue(Event(true))
                        }, {
                            loading.postValue(false)
                            messageString.postValue(Resource.error(it.message))
                        })
                )
            }
        } else {
            nameValidator.postValue(false to "Name cannot be empty")
        }
    }

    fun onImageSelected(inputStream: InputStream) {
        loading.postValue(true)
        compositeDisposable.add(
            Single.fromCallable {
                fileHelper.saveInputStreamToFile(inputStream, directory, "profile_img", 100)
            }
                .subscribeOn(schedulerProvider.io())
                .subscribe({
                    loading.postValue(false)
                    imageFile.postValue(it)
                }, {
                    loading.postValue(false)
                    messageString.postValue(Resource.error(it.message))
                })
        )
    }
}