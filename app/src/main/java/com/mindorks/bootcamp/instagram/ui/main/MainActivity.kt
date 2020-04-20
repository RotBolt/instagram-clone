package com.mindorks.bootcamp.instagram.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.ActivityComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseActivity
import com.mindorks.bootcamp.instagram.ui.home.HomeFragment
import com.mindorks.bootcamp.instagram.ui.photo.PhotoFragment
import com.mindorks.bootcamp.instagram.ui.profile.ProfileFragment
import com.mindorks.bootcamp.instagram.ui.splash.SplashActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity<MainViewModel>() {

    private var activeFragment: Fragment? = null

    @Inject
    lateinit var mainSharedViewModel: MainSharedViewModel

    override fun provideLayoutId(): Int = R.layout.activity_main

    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        bottomNavView.run {
            itemIconTintList = null
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.itemHome -> {
                        viewModel.onHomeSelected()
                        true
                    }
                    R.id.itemPhoto -> {
                        viewModel.onPhotoSelected()
                        true
                    }
                    R.id.itemProfile -> {
                        viewModel.onProfileSelected()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.homeNavigation.observe(this, Observer {
            it.getIfNotHandled()?.run { showHome() }
        })

        viewModel.photoNavigation.observe(this, Observer {
            it.getIfNotHandled()?.run { showAddPhoto() }
        })

        viewModel.profileNavigation.observe(this, Observer {
            it.getIfNotHandled()?.run { showProfile() }
        })

        mainSharedViewModel.homeRedirection.observe(this, Observer {
            it.getIfNotHandled()?.run {
                bottomNavView.selectedItemId = R.id.itemHome
            }
        })

        mainSharedViewModel.logoutRedirection.observe(this, Observer {
            it.getIfNotHandled()?.run {
                startActivity(Intent(this@MainActivity, SplashActivity::class.java))
                finish()
            }
        })
    }

    private fun showHome() {
        if (activeFragment is HomeFragment) return

        val fragmentTransaction = supportFragmentManager.beginTransaction()

        var fragment = supportFragmentManager.findFragmentByTag(HomeFragment.TAG) as HomeFragment?

        if (fragment == null) {
            fragment = HomeFragment.newInstance()
            fragmentTransaction.add(R.id.containerFragment, fragment, HomeFragment.TAG)
        } else {
            fragmentTransaction.show(fragment)
        }

        if (activeFragment != null) fragmentTransaction.hide(activeFragment as Fragment)

        fragmentTransaction.commit()

        activeFragment = fragment
    }

    private fun showProfile() {
        if (activeFragment is ProfileFragment) return

        val fragmentTransaction = supportFragmentManager.beginTransaction()

        var fragment =
            supportFragmentManager.findFragmentByTag(ProfileFragment.TAG) as ProfileFragment?

        if (fragment == null) {
            fragment = ProfileFragment.newInstance()
            fragmentTransaction.add(R.id.containerFragment, fragment, ProfileFragment.TAG)
        } else {
            fragmentTransaction.show(fragment)
        }

        if (activeFragment != null) fragmentTransaction.hide(activeFragment as Fragment)

        fragmentTransaction.commit()

        activeFragment = fragment
    }

    private fun showAddPhoto() {
        if (activeFragment is PhotoFragment) return

        val fragmentTransaction = supportFragmentManager.beginTransaction()

        var fragment = supportFragmentManager.findFragmentByTag(PhotoFragment.TAG) as PhotoFragment?

        if (fragment == null) {
            fragment = PhotoFragment.newInstance()
            fragmentTransaction.add(R.id.containerFragment, fragment, PhotoFragment.TAG)
        } else {
            fragmentTransaction.show(fragment)
        }

        if (activeFragment != null) fragmentTransaction.hide(activeFragment as Fragment)

        fragmentTransaction.commit()

        activeFragment = fragment
    }

}