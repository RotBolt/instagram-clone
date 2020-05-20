package com.mindorks.bootcamp.instagram.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.request.UpdateInfoRequest
import com.mindorks.bootcamp.instagram.data.remote.response.GeneralResponse
import com.mindorks.bootcamp.instagram.data.repository.PhotoRepository
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.utils.TestHelper
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.common.FileHelper
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
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
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.io.File
import java.io.InputStream

@RunWith(MockitoJUnitRunner::class)
class EditProfileViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var networkHelper: NetworkHelper

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var photoRepository: PhotoRepository

    @Mock
    lateinit var directory: File

    @Mock
    lateinit var fileHelper: FileHelper

    @Mock
    lateinit var loadingObserver: Observer<Boolean>

    @Mock
    lateinit var nameObserver: Observer<String>

    @Mock
    lateinit var bioObserver: Observer<String>

    @Mock
    lateinit var imageFileObserver: Observer<File?>

    @Mock
    lateinit var emailObserver: Observer<String>

    @Mock
    lateinit var profilePicUrlObserver: Observer<String>

    @Mock
    lateinit var successObserver: Observer<Event<Boolean>>

    @Mock
    lateinit var nameValidatorObserver: Observer<Pair<Boolean, String>>

    lateinit var testScheduler: TestScheduler

    lateinit var editProfileViewModel: EditProfileViewModel

    lateinit var user: User

    @Before
    fun setup() {
        user = TestHelper.getTestUser()

        doReturn(user)
            .`when`(userRepository)
            .getCurrentUser()


        testScheduler = TestScheduler()
        val testSchedulerProvider = TestSchedulerProvider(testScheduler)

        editProfileViewModel = EditProfileViewModel(
            testSchedulerProvider,
            CompositeDisposable(),
            networkHelper,
            userRepository,
            photoRepository, directory, fileHelper
        )

        editProfileViewModel.apply {
            loading.observeForever(loadingObserver)
            nameField.observeForever(nameObserver)
            bioField.observeForever(bioObserver)
            imageFile.observeForever(imageFileObserver)
            emailText.observeForever(emailObserver)
            profilePicUrl.observeForever(profilePicUrlObserver)
            successUpload.observeForever(successObserver)
            nameValidator.observeForever(nameValidatorObserver)
        }
    }

    @Test
    fun givenImageFile_ValidDetails_onUpload_shouldUploadDetails() {

        val fakeImageFile = mock(File::class.java)
        val fakeBio = "Sample Bio : Pui"

        editProfileViewModel.apply {
            onBioChanged(fakeBio)
            onNameChanged(user.name)
            onProfilePicUrlReceived(user.profilePicUrl ?: "")
            imageFile.postValue(fakeImageFile)
        }
        val uploadedUrl = "https://cloudStorage.api.com/hfwejkhew/puiProfile.jpg"

        doReturn(Single.just(uploadedUrl))
            .`when`(photoRepository)
            .uploadImage(fakeImageFile, user)

        doReturn(Single.just(GeneralResponse("200", "Ok")))
            .`when`(userRepository)
            .doUpdateUserInfo(UpdateInfoRequest(user.name, uploadedUrl, fakeBio), user)

        editProfileViewModel.onUploadDetails()

        testScheduler.triggerActions()

        verify(loadingObserver).onChanged(true)
        verify(loadingObserver).onChanged(false)
        verify(nameValidatorObserver).onChanged(true to "validated")
        verify(successObserver).onChanged(Event(true))
    }

    @Test
    fun givenNoImageFile_ValidDetails_onUpload_shouldUploadDetails() {

        val fakeBio = "Sample Bio : Pui"

        editProfileViewModel.apply {
            onBioChanged(fakeBio)
            onNameChanged(user.name)
            onProfilePicUrlReceived(user.profilePicUrl ?: "")
        }

        doReturn(Single.just(GeneralResponse("200", "Ok")))
            .`when`(userRepository)
            .doUpdateUserInfo(UpdateInfoRequest(user.name, user.profilePicUrl, fakeBio), user)

        editProfileViewModel.onUploadDetails()

        testScheduler.triggerActions()

        verify(loadingObserver).onChanged(true)
        verify(loadingObserver).onChanged(false)
        verify(nameValidatorObserver).onChanged(true to "validated")
        verify(successObserver).onChanged(Event(true))
    }


    @Test
    fun givenInvalidDetails_shouldDShowInvalidation() {
        editProfileViewModel.onUploadDetails()
        testScheduler.triggerActions()

        verify(nameValidatorObserver).onChanged(false to "Name cannot be empty")
    }

    @Test
    fun givenImageSelected_saveImageToFile() {
        val inputStream = mock(InputStream::class.java)
        val file = mock(File::class.java)

        doReturn(file)
            .`when`(fileHelper)
            .saveInputStreamToFile(inputStream, directory, "profile_img", 100)

        editProfileViewModel.onImageSelected(inputStream)
        testScheduler.triggerActions()

        verify(loadingObserver).onChanged(true)
        verify(loadingObserver).onChanged(false)

        verify(imageFileObserver).onChanged(file)
    }

    @After
    fun tearDown() {
        editProfileViewModel.apply {
            loading.removeObserver(loadingObserver)
            nameField.removeObserver(nameObserver)
            bioField.removeObserver(bioObserver)
            imageFile.removeObserver(imageFileObserver)
            emailText.removeObserver(emailObserver)
            profilePicUrl.removeObserver(profilePicUrlObserver)
            successUpload.removeObserver(successObserver)
            nameValidator.removeObserver(nameValidatorObserver)
        }
    }

}