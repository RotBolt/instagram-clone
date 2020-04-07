package com.mindorks.bootcamp.instagram.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.ActivityComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseActivity
import com.mindorks.bootcamp.instagram.ui.dummy.DummyActivity
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.common.Status
import kotlinx.android.synthetic.main.activity_login.*
import timber.log.Timber

class LoginActivity : BaseActivity<LoginViewModel>() {

    override fun provideLayoutId(): Int = R.layout.activity_login

    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.launchDummy.observe(this, Observer {
            it.getIfNotHandled()?.run {
                startActivity(Intent(applicationContext, DummyActivity::class.java))
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
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                viewModel.onEmailChanged(p0.toString())
            }
        })

        etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                viewModel.onPasswordChanged(p0.toString())
            }
        })

        btnLogin.setOnClickListener { viewModel.onLogin() }
    }


}
