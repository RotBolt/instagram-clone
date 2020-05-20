package com.mindorks.bootcamp.instagram.ui.photo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.repository.PhotoRepository
import com.mindorks.bootcamp.instagram.data.repository.PostRepository
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
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class PhotoViewModelTest {
    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var networkHelper: NetworkHelper

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var postRepository: PostRepository

    @Mock
    lateinit var photoRepository: PhotoRepository

    @Mock
    lateinit var directory: File

    @Mock
    lateinit var fileHelper: FileHelper

    @Mock
    lateinit var loadingObserver: Observer<Boolean>

    @Mock
    lateinit var postObserver: Observer<Event<Post>>

    lateinit var testScheduler: TestScheduler

    lateinit var photoViewModel: PhotoViewModel

    lateinit var user: User

    @Before
    fun setup() {
        user = TestHelper.getTestUser()

        doReturn(user)
            .`when`(userRepository)
            .getCurrentUser()

        testScheduler = TestScheduler()
        val testSchedulerProvider = TestSchedulerProvider(testScheduler)
        photoViewModel = PhotoViewModel(
            testSchedulerProvider,
            CompositeDisposable(),
            networkHelper,
            userRepository,
            postRepository,
            photoRepository,
            directory,
            fileHelper
        )

        photoViewModel.apply {
            loading.observeForever(loadingObserver)
            post.observeForever(postObserver)
        }
    }

    @Test
    fun givenGalleryImageSelected_shouldUploadAndCreatePost() {
        val inputStream = mock(InputStream::class.java)

        val file = mock(File::class.java)
        doReturn(file)
            .`when`(fileHelper)
            .saveInputStreamToFile(inputStream, directory, "gallery_img_temp", 500)

        val imageSize = Pair(300, 500)
        doReturn(imageSize)
            .`when`(fileHelper)
            .getImageSize(file)

        val samplePhotoUrl = "https://cloudStorage.api.com/hfjkhfkwe/puipost.jpeg"
        doReturn(Single.just(samplePhotoUrl))
            .`when`(photoRepository)
            .uploadImage(file, user)

        val fakePost = Post(
            "newPostId",
            samplePhotoUrl,
            imageSize.first,
            imageSize.second,
            Post.User(
                user.id,
                user.name,
                user.profilePicUrl
            ),
            null,
            Date()
        )

        doReturn(Single.just(fakePost))
            .`when`(postRepository)
            .createPost(samplePhotoUrl, imageSize.first, imageSize.second, user)

        photoViewModel.onGalleryImageSelected(inputStream)

        testScheduler.triggerActions()

        assert(photoViewModel.loading.value == false)
        verify(loadingObserver, times(2)).onChanged(true)
        verify(loadingObserver).onChanged(false)

        verify(postObserver).onChanged(Event(fakePost))
        assert(photoViewModel.post.value == Event(fakePost))

    }

    @Test
    fun givenCameraImageTaken_shouldUploadAndCreatePost() {

        val cameraImagePath = "/DCIM/pictures/10093819831.jpeg"
        val fakeCameraImageProcessor: () -> String = { cameraImagePath }

        val file = mock(File::class.java)
        val imageSize = Pair(300, 500)

        doReturn(file)
            .`when`(fileHelper)
            .makeFile(cameraImagePath)

        doReturn(imageSize)
            .`when`(fileHelper)
            .getImageSize(file)

        val samplePhotoUrl = "https://cloudStorage.api.com/hfjkhfkwe/puipost.jpeg"
        doReturn(Single.just(samplePhotoUrl))
            .`when`(photoRepository)
            .uploadImage(file, user)

        val fakePost = Post(
            "newPostId",
            samplePhotoUrl,
            imageSize.first,
            imageSize.second,
            Post.User(
                user.id,
                user.name,
                user.profilePicUrl
            ),
            null,
            Date()
        )

        doReturn(Single.just(fakePost))
            .`when`(postRepository)
            .createPost(samplePhotoUrl, imageSize.first, imageSize.second, user)

        photoViewModel.onCameraImageTaken(fakeCameraImageProcessor)

        testScheduler.triggerActions()

        assert(photoViewModel.loading.value == false)
        verify(loadingObserver,times(2)).onChanged(true)
        verify(loadingObserver).onChanged(false)

        verify(postObserver).onChanged(Event(fakePost))
        assert(photoViewModel.post.value == Event(fakePost))


    }

    @After
    fun tearDown() {
        photoViewModel.apply {
            loading.removeObserver(loadingObserver)
            post.removeObserver(postObserver)
        }
    }
}