package com.mindorks.bootcamp.instagram.di.module

import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindorks.bootcamp.instagram.data.repository.DummyRepository
import com.mindorks.bootcamp.instagram.data.repository.PhotoRepository
import com.mindorks.bootcamp.instagram.data.repository.PostRepository
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.di.TempDirectory
import com.mindorks.bootcamp.instagram.ui.base.BaseFragment
import com.mindorks.bootcamp.instagram.ui.dummies.DummiesAdapter
import com.mindorks.bootcamp.instagram.ui.dummies.DummiesViewModel
import com.mindorks.bootcamp.instagram.ui.home.HomeViewModel
import com.mindorks.bootcamp.instagram.ui.home.posts.PostAdapter
import com.mindorks.bootcamp.instagram.ui.main.MainSharedViewModel
import com.mindorks.bootcamp.instagram.ui.photo.PhotoViewModel
import com.mindorks.bootcamp.instagram.ui.profile.ProfileViewModel
import com.mindorks.bootcamp.instagram.ui.profile.myPosts.MyPostsAdapter
import com.mindorks.bootcamp.instagram.utils.ViewModelProviderFactory
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import com.mindorks.paracamera.Camera
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import java.io.File

@Module
class FragmentModule(private val fragment: BaseFragment<*>) {

    @Provides
    fun provideLinearLayoutManager(): LinearLayoutManager = LinearLayoutManager(fragment.context)

    @Provides
    fun provideGridLayoutManager(): GridLayoutManager = GridLayoutManager(fragment.context, 3)

    @Provides
    fun provideDummiesAdapter() = DummiesAdapter(fragment.lifecycle, ArrayList())

    @Provides
    fun provideDummiesViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        dummyRepository: DummyRepository
    ): DummiesViewModel =
        ViewModelProviders.of(fragment,
            ViewModelProviderFactory(DummiesViewModel::class) {
                DummiesViewModel(
                    schedulerProvider,
                    compositeDisposable,
                    networkHelper,
                    dummyRepository
                )
            }
        ).get(DummiesViewModel::class.java)


    @Provides
    fun providePostAdapter() = PostAdapter(fragment.lifecycle, ArrayList())

    @Provides
    fun provideMyPostAdapter() = MyPostsAdapter(fragment.lifecycle, ArrayList())

    @Provides
    fun provideHomeViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        userRepository: UserRepository,
        postRepository: PostRepository
    ): HomeViewModel = ViewModelProviders.of(
        fragment, ViewModelProviderFactory(HomeViewModel::class) {
            HomeViewModel(
                schedulerProvider, compositeDisposable, networkHelper, userRepository,
                postRepository, ArrayList(), PublishProcessor.create()
            )
        }).get(HomeViewModel::class.java)

    @Provides
    fun providePhotoViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        userRepository: UserRepository,
        photoRepository: PhotoRepository,
        postRepository: PostRepository,
        networkHelper: NetworkHelper,
        @TempDirectory directory: File
    ): PhotoViewModel = ViewModelProviders.of(
        fragment, ViewModelProviderFactory(PhotoViewModel::class) {
            PhotoViewModel(
                schedulerProvider, compositeDisposable, networkHelper,
                userRepository, postRepository, photoRepository, directory
            )
        }).get(PhotoViewModel::class.java)

    @Provides
    fun provideProfileViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        userRepository: UserRepository,
        postRepository: PostRepository
    ): ProfileViewModel = ViewModelProviders.of(
        fragment, ViewModelProviderFactory(ProfileViewModel::class) {
            ProfileViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper,
                userRepository,
                postRepository
            )
        }).get(ProfileViewModel::class.java)

    @Provides
    fun provideCamera() = Camera.Builder()
        .resetToCorrectOrientation(true)
        .setTakePhotoRequestCode(Camera.REQUEST_TAKE_PHOTO)
        .setDirectory("instaClone")
        .setName("instaClick ${System.currentTimeMillis()}")
        .setCompression(75)
        .setImageFormat(Camera.IMAGE_JPEG)
        .setImageHeight(500)
        .build(fragment)


    @Provides
    fun provideMainSharedViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): MainSharedViewModel = ViewModelProviders.of(
        fragment.activity!!, ViewModelProviderFactory(MainSharedViewModel::class) {
            MainSharedViewModel(schedulerProvider, compositeDisposable, networkHelper)
        }).get(MainSharedViewModel::class.java)

}