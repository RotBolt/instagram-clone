package com.mindorks.bootcamp.instagram.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.ActivityComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseActivity
import com.mindorks.bootcamp.instagram.ui.login.signUp.SignUpActivity
import com.mindorks.bootcamp.instagram.ui.main.MainActivity
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.common.Status
import com.mindorks.bootcamp.instagram.utils.common.handleClearIcon
import com.mindorks.bootcamp.instagram.utils.common.setupRightDrawable
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity<LoginViewModel>() {

    private var shouldFinish = false

    override fun provideLayoutId(): Int = R.layout.activity_login

    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun onStop() {
        super.onStop()
        if (shouldFinish) {
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finishAffinity()
    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.launchMain.observe(this, Observer {
            it.getIfNotHandled()?.run {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
        })

        viewModel.emailValidation.observe(this, Observer {
            when (it.status) {
                Status.ERROR -> layoutEmail.error = it.data?.run { getString(this) }
                else -> layoutEmail.isErrorEnabled = false
            }
        })
        viewModel.passwordValidation.observe(this, Observer {
            when (it.status) {
                Status.ERROR -> layoutPassword.error = it.data?.run { getString(this) }
                else -> layoutPassword.isErrorEnabled = false
            }
        })

        viewModel.loggingIn.observe(this, Observer {
            pbLoading.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.passwordField.observe(this, Observer {
            if (etPassword.text.toString() != it) etPassword.setText(it)
        })

        viewModel.emailField.observe(this, Observer {
            if (etEmail.text.toString() != it) etEmail.setText(it)
        })

    }

    override fun setupView(savedInstanceState: Bundle?) {

        val clearIcon = ContextCompat.getDrawable(this, R.drawable.ic_cancel)

        etEmail.setupRightDrawable(clearIcon) { viewModel.onResetEmailField() }
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                etEmail.handleClearIcon(p0.toString(), clearIcon)
                viewModel.onEmailChanged(p0.toString())
            }
        })

        etPassword.setupRightDrawable(clearIcon) { viewModel.onResetPasswordField() }
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                etPassword.handleClearIcon(p0.toString(), clearIcon)
                viewModel.onPasswordChanged(p0.toString())
            }
        })

        btnLogin.setOnClickListener { viewModel.onLogin() }

        val launchSignUp = Event<Map<String, View>>(
            mapOf(
                getString(R.string.shared_element_app_logo) to ivLogo,
                getString(R.string.shared_element_email) to etEmail,
                getString(R.string.shared_element_password) to etPassword
            )
        )

        tvSignUpEmail.setOnClickListener {
            launchSignUp.getIfNotHandled()?.run {

                val sharedElements = this.map {
                    Pair.create(it.value,it.key)
                }.toTypedArray()

                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@LoginActivity,
                    *sharedElements
                )
                startActivity(
                    Intent(this@LoginActivity, SignUpActivity::class.java),
                    options.toBundle()
                )
                shouldFinish = true
            }
        }
    }


}
