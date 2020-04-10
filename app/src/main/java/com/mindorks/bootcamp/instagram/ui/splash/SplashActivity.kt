package com.mindorks.bootcamp.instagram.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.ActivityComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseActivity
import com.mindorks.bootcamp.instagram.ui.login.LoginActivity
import com.mindorks.bootcamp.instagram.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity<SplashViewModel>() {

    companion object {
        const val TAG = "SplashActivity"
    }

    private var shouldFinish = false

    override fun onStop() {
        super.onStop()
        if(shouldFinish){
            finish()
        }
    }

    override fun provideLayoutId(): Int = R.layout.activity_splash

    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun setupView(savedInstanceState: Bundle?) {
    }

    override fun setupObservers() {
        // Event is used by the view model to tell the activity to launch another activity
        // view model also provided the Bundle in the event that is needed for the Activity
        viewModel.launchMain.observe(this, Observer {
            it.getIfNotHandled()?.run {
                Handler().postDelayed({
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                },500)
            }
        })

        viewModel.launchLogin.observe(this, Observer {
            it.getIfNotHandled()?.run {
                Handler().postDelayed({

                    // Put the options here so that it can get enough time to calculate the
                    // positions of current view over there
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@SplashActivity,
                        ivLogo,
                        getString(R.string.shared_element_app_logo)
                    )
                    startActivity(Intent(applicationContext, LoginActivity::class.java),options.toBundle())
                    shouldFinish = true
                },500)
            }
        })
    }


}