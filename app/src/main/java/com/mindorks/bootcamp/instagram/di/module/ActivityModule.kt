package com.mindorks.bootcamp.instagram.di.module

import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindorks.bootcamp.instagram.data.repository.DummyRepository
import com.mindorks.bootcamp.instagram.data.repository.PhotoRepository
import com.mindorks.bootcamp.instagram.data.repository.PostRepository
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.di.TempDirectory
import com.mindorks.bootcamp.instagram.ui.base.BaseActivity
import com.mindorks.bootcamp.instagram.ui.dummy.DummyViewModel
import com.mindorks.bootcamp.instagram.ui.login.LoginViewModel
import com.mindorks.bootcamp.instagram.ui.login.signUp.SignUpViewModel
import com.mindorks.bootcamp.instagram.ui.main.MainSharedViewModel
import com.mindorks.bootcamp.instagram.ui.main.MainViewModel
import com.mindorks.bootcamp.instagram.ui.postDetails.PostDetailsViewModel
import com.mindorks.bootcamp.instagram.ui.profile.EditProfileViewModel
import com.mindorks.bootcamp.instagram.ui.splash.SplashViewModel
import com.mindorks.bootcamp.instagram.utils.ViewModelProviderFactory
import com.mindorks.bootcamp.instagram.utils.common.FileHelper
import com.mindorks.bootcamp.instagram.utils.display.ScreenResourceProvider
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import com.mindorks.paracamera.Camera
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import java.io.File

/**
 * Kotlin Generics Reference: https://kotlinlang.org/docs/reference/generics.html
 * Basically it means that we can pass any class that extends BaseActivity which take
 * BaseViewModel subclass as parameter
 */
@Module
class ActivityModule(private val activity: BaseActivity<*>) {

    @Provides
    fun provideLinearLayoutManager(): LinearLayoutManager = LinearLayoutManager(activity)

    @Provides
    fun provideSplashViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelperImpl: NetworkHelper,
        userRepository: UserRepository
    ): SplashViewModel = ViewModelProviders.of(
        activity, ViewModelProviderFactory(SplashViewModel::class) {
            SplashViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelperImpl,
                userRepository
            )
            //this lambda creates and return SplashViewModel
        }).get(SplashViewModel::class.java)

    @Provides
    fun provideDummyViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelperImpl: NetworkHelper,
        dummyRepository: DummyRepository
    ): DummyViewModel = ViewModelProviders.of(
        activity, ViewModelProviderFactory(DummyViewModel::class) {
            DummyViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelperImpl,
                dummyRepository
            )
        }).get(DummyViewModel::class.java)

    @Provides
    fun provideLoginViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelperImpl: NetworkHelper,
        userRepository: UserRepository
    ): LoginViewModel = ViewModelProviders.of(
        activity, ViewModelProviderFactory(LoginViewModel::class) {
            LoginViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelperImpl,
                userRepository
            )
        }).get(LoginViewModel::class.java)

    @Provides
    fun provideSignUpViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelperImpl: NetworkHelper,
        userRepository: UserRepository
    ): SignUpViewModel = ViewModelProviders.of(
        activity, ViewModelProviderFactory(SignUpViewModel::class) {
            SignUpViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelperImpl,
                userRepository
            )
        }).get(SignUpViewModel::class.java)

    @Provides
    fun provideMainViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelperImpl: NetworkHelper
    ): MainViewModel = ViewModelProviders.of(
        activity, ViewModelProviderFactory(MainViewModel::class) {
            MainViewModel(schedulerProvider, compositeDisposable, networkHelperImpl)
        }).get(MainViewModel::class.java)

    @Provides
    fun provideMainSharedViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelperImpl: NetworkHelper
    ): MainSharedViewModel = ViewModelProviders.of(
        activity, ViewModelProviderFactory(MainSharedViewModel::class) {
            MainSharedViewModel(schedulerProvider, compositeDisposable, networkHelperImpl)
        }).get(MainSharedViewModel::class.java)

    @Provides
    fun providePostDetailsViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelperImpl: NetworkHelper,
        userRepository: UserRepository,
        postRepository: PostRepository,
        screenResourceProvider: ScreenResourceProvider
    ): PostDetailsViewModel = ViewModelProviders.of(
        activity, ViewModelProviderFactory(PostDetailsViewModel::class) {
            PostDetailsViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelperImpl,
                userRepository,
                postRepository,
                screenResourceProvider
            )
        }).get(PostDetailsViewModel::class.java)

    @Provides
    fun provideEditProfileViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelperImpl: NetworkHelper,
        userRepository: UserRepository,
        photoRepository: PhotoRepository,
        @TempDirectory directory: File,
        fileHelper: FileHelper
    ): EditProfileViewModel = ViewModelProviders.of(
        activity, ViewModelProviderFactory(EditProfileViewModel::class) {
            EditProfileViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelperImpl,
                userRepository,
                photoRepository,
                directory,
                fileHelper
            )
        }).get(EditProfileViewModel::class.java)

    @Provides
    fun provideCamera() = Camera.Builder()
        .resetToCorrectOrientation(true)
        .setTakePhotoRequestCode(Camera.REQUEST_TAKE_PHOTO)
        .setDirectory("instaClone")
        .setName("instaClick ${System.currentTimeMillis()}")
        .setCompression(75)
        .setImageFormat(Camera.IMAGE_JPEG)
        .setImageHeight(100)
        .build(activity)
}