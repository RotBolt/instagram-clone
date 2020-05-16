package com.mindorks.bootcamp.instagram.ui.photo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.FragmentComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseFragment
import com.mindorks.bootcamp.instagram.ui.main.MainSharedViewModel
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.paracamera.Camera
import kotlinx.android.synthetic.main.fragment_photo.*
import java.io.FileNotFoundException
import javax.inject.Inject

class PhotoFragment : BaseFragment<PhotoViewModel>() {

    companion object {
        const val TAG = "PhotoFragment"
        const val RESULT_IMG_GALLERY = 1000
        fun newInstance(): PhotoFragment {
            val args = Bundle()
            val fragment = PhotoFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var camera: Camera

    @Inject
    lateinit var mainSharedViewModel: MainSharedViewModel

    override fun provideLayoutId(): Int = R.layout.fragment_photo

    override fun injectDependencies(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.loading.observe(this, Observer {
            pbLoading.visibility = if(it) View.VISIBLE else View.GONE
        })

        viewModel.post.observe(this, Observer {
            it.getIfNotHandled()?.run {
                mainSharedViewModel.newPost.postValue(Event(this))
                mainSharedViewModel.onHomeRedirection()
            }
        })
    }

    override fun setupView(view: View) {
        viewGallery.setOnClickListener {
            Intent(Intent.ACTION_PICK)
                .apply {
                    type = "image/*"
                }.run {
                    startActivityForResult(this, RESULT_IMG_GALLERY)
                }
        }

        viewCamera.setOnClickListener {
            try {
                camera.takePicture()
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(reqCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK) {
            when (reqCode) {
                RESULT_IMG_GALLERY -> {
                    try {
                        intent?.data?.let {
                            activity?.contentResolver?.openInputStream(it)?.run {
                                viewModel.onGalleryImageSelected(this)
                            }
                        } ?: showMessage(R.string.try_again)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                        showMessage(R.string.try_again)
                    }
                }
                Camera.REQUEST_TAKE_PHOTO -> {
                    viewModel.onCameraImageTaken { camera.cameraBitmapPath }
                }
            }
        }
    }

}