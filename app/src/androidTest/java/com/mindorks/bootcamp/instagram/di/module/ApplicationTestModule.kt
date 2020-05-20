package com.mindorks.bootcamp.instagram.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.mindorks.bootcamp.instagram.InstagramApplication
import com.mindorks.bootcamp.instagram.data.local.db.DatabaseService
import com.mindorks.bootcamp.instagram.data.remote.FakeNetworkService
import com.mindorks.bootcamp.instagram.data.remote.NetworkService
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.di.ApplicationContext
import com.mindorks.bootcamp.instagram.di.TempDirectory
import com.mindorks.bootcamp.instagram.utils.common.FileHelper
import com.mindorks.bootcamp.instagram.utils.common.FileUtils
import com.mindorks.bootcamp.instagram.utils.display.ScreenResourceProvider
import com.mindorks.bootcamp.instagram.utils.display.ScreenUtils
import com.mindorks.bootcamp.instagram.utils.network.FakeNetworkHelperImpl
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.RxSchedulerProvider
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Module
class ApplicationTestModule(private val application: InstagramApplication) {

    @Provides
    @Singleton
    fun provideApplication(): Application = application

    @Provides
    @Singleton
    @ApplicationContext
    fun provideContext(): Context = application

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    fun provideSchedulerProvider(): SchedulerProvider = RxSchedulerProvider()

    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences =
        application.getSharedPreferences("bootcamp-instagram-project-prefs", Context.MODE_PRIVATE)

    /**
     * We need to write @Singleton on the provide method if we are create the instance inside this method
     * to make it singleton. Even if we have written @Singleton on the instance's class
     */
    @Provides
    @Singleton
    fun provideDatabaseService(): DatabaseService =
        Room.databaseBuilder(
            application, DatabaseService::class.java,
            "bootcamp-instagram-project-db"
        ).build()

    @Provides
    @Singleton
    fun provideNetworkService(): NetworkService {
        Networking.API_KEY = "FAKE_API_KEY"
        return FakeNetworkService()
    }


    @Singleton
    @Provides
    fun provideNetworkHelper(): NetworkHelper = FakeNetworkHelperImpl(application)


    @Provides
    @Singleton
    @TempDirectory
    fun provideTempDirectory() = FileUtils.getDirectory(application, "temp")

    @Provides
    @Singleton
    fun provideScreenResourceProvider() : ScreenResourceProvider = ScreenUtils

    @Provides
    @Singleton
    fun provideFileHelper(): FileHelper = FileUtils
}