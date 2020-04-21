package com.mindorks.bootcamp.instagram.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.ActivityComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseActivity
import com.mindorks.bootcamp.instagram.ui.base.BaseBottomSheetDialog
import com.mindorks.paracamera.Camera
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.File
import java.io.FileNotFoundException
import java.lang.Exception
import javax.inject.Inject

class EditProfileActivity : BaseActivity<EditProfileViewModel>(),
    BaseBottomSheetDialog.OnItemClickListener {

    companion object {
        const val RESULT_IMG_GALLERY = 1002
        const val NAME_FIELD = "name"
        const val BIO_FIELD = "bio"
        const val PROFILE_PIC_URL = "profile_pic_url"
    }

    @Inject
    lateinit var camera: Camera

    override fun provideLayoutId(): Int = R.layout.activity_edit_profile

    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.emailText.observe(this, Observer {
            tvEmail.text = it
        })

        viewModel.bioField.observe(this, Observer {
            if (etBio.text.toString() != it) etBio.setText(it)
        })

        viewModel.nameField.observe(this, Observer {
            if (etName.text.toString() != it) etName.setText(it)
        })

        viewModel.nameValidator.observe(this, Observer {
            when (it.first) {
                false -> layoutName.error = it.second
                true -> layoutName.isErrorEnabled = false
            }
        })

        viewModel.successUpload.observe(this, Observer {
            it.getIfNotHandled()?.run {
                if (this) {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        })

        viewModel.profilePicUrl.observe(this, Observer {
            if (it.isNotEmpty()) {
                loadImageIntoView(it)
            }
        })

        viewModel.imageFile.observe(this, Observer {
            if (it != null) {
                loadImageIntoView(it)
            } else {
                showMessage(R.string.try_again)
            }
        })
    }

    override fun setupView(savedInstanceState: Bundle?) {

        intent?.run {
            getStringExtra(NAME_FIELD)?.run {
                viewModel.onNameChanged(this)
            }

            getStringExtra(BIO_FIELD)?.run {
                viewModel.onBioChanged(this)
            }

            getStringExtra(PROFILE_PIC_URL)?.run {
                viewModel.onProfilePicUrlReceived(this)
            }

        }

        ivAddProfilePic.setOnClickListener {
            showChangeProfilePicBottomFrag()
        }
        tvChangePhoto.setOnClickListener {
            showChangeProfilePicBottomFrag()
        }

        ivClose.setOnClickListener {
            finish()
        }

        ivDone.setOnClickListener {
            viewModel.onUploadDetails()
        }

        etBio.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.onBioChanged(s.toString())
            }
        })

        etName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.onNameChanged(s.toString())
            }
        })
    }

    private fun showChangeProfilePicBottomFrag() {
        ChangeProfilePicBottomFragment().show(
            supportFragmentManager,
            ChangeProfilePicBottomFragment.TAG
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RESULT_IMG_GALLERY -> {
                    try {
                        data?.data?.let {
                            contentResolver?.openInputStream(it)?.run {
                                viewModel.onImageSelected(this)
                            }
                        }
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                        showMessage(R.string.try_again)
                    }
                }
                Camera.REQUEST_TAKE_PHOTO -> {
                    viewModel.onImageSelected(camera.cameraBitmapPath.byteInputStream())
                }
            }
        }
    }

    private fun loadImageIntoView(file: File) {
        val glideRequest = Glide.with(this@EditProfileActivity)
            .load(file)
            .apply(RequestOptions().circleCrop())
        glideRequest.into(ivAddProfilePic)
    }

    private fun loadImageIntoView(url: String) {
        val glideRequest = Glide.with(this@EditProfileActivity)
            .load(url)
            .apply(RequestOptions().circleCrop())
        glideRequest.into(ivAddProfilePic)
    }

    override fun onItemClick(id: Int) {
        when (id) {
            R.id.viewCamera -> {
                try {
                    camera.takePicture()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            R.id.viewGallery -> {
                Intent(Intent.ACTION_PICK)
                    .apply {
                        type = "image/*"
                    }.run {
                        startActivityForResult(this, RESULT_IMG_GALLERY)
                    }
            }
        }
    }

}