package com.mindorks.bootcamp.instagram.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.FragmentComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseFragment
import com.mindorks.bootcamp.instagram.ui.home.posts.PostAdapter
import com.mindorks.bootcamp.instagram.ui.main.MainSharedViewModel
import com.mindorks.bootcamp.instagram.ui.profile.myPosts.MyPostsAdapter
import com.mindorks.bootcamp.instagram.utils.common.GlideHelper
import com.mindorks.bootcamp.instagram.utils.display.RvItemDecoration
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject

class ProfileFragment : BaseFragment<ProfileViewModel>() {

    companion object {
        const val TAG = "ProfileFragment"

        const val EDIT_PROFILE_REQUEST = 1003
        fun newInstance(): ProfileFragment {
            val args = Bundle()
            val fragment = ProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var bio: String = ""
    private var name: String = ""
    private var profilePicUrl: String? = null

    @Inject
    lateinit var mainSharedViewModel: MainSharedViewModel

    @Inject
    lateinit var myPostAdapter: MyPostsAdapter

    @Inject
    lateinit var gridLayoutManager: GridLayoutManager

    override fun provideLayoutId(): Int = R.layout.fragment_profile

    override fun injectDependencies(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.loading.observe(this, Observer {
            pbLoading.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.userName.observe(this, Observer {
            tvUserName.text = it
            name = it
        })

        viewModel.tagLine.observe(this, Observer {
            tvBio.text = it
            bio = it
        })

        viewModel.profilePic.observe(this, Observer {
            it?.run {
                profilePicUrl = url
                val glideRequest = Glide.with(this@ProfileFragment.requireContext())
                    .load(GlideHelper.getProtectedUrl(url, headers))
                    .apply(RequestOptions().circleCrop())
                    .apply(RequestOptions().placeholder(R.drawable.ic_profile))
                glideRequest.into(ivProfilePic)
            }
        })

        viewModel.logout.observe(this, Observer {
            if (it) {
                mainSharedViewModel.onLogoutRedirection()
            }
        })

        viewModel.loadingMyPosts.observe(this, Observer {
            pbPostsLoading.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.myAllPosts.observe(this, Observer {
            myPostAdapter.updateList(it)
            tvPostCount.text = getString(R.string.post_count_label, it.size)
            tvNoPosts.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        })

        mainSharedViewModel.newPost.observe(this, Observer {
            viewModel.onFetchMyPosts()
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == EDIT_PROFILE_REQUEST) {
                viewModel.onFetchUserInfo()
            }
        }
    }

    override fun setupView(view: View) {
        tvPostCount.text = getString(R.string.post_count_label, 0)

        btnLogOut.setOnClickListener {
            viewModel.onLogout()
        }

        rvMyPosts.apply {
            layoutManager = gridLayoutManager
            adapter = myPostAdapter
        }

        btnEditProfile.setOnClickListener {
            startActivityForResult(Intent(context, EditProfileActivity::class.java).apply {
                putExtra(EditProfileActivity.NAME_FIELD, name)
                putExtra(EditProfileActivity.BIO_FIELD, bio)
                putExtra(EditProfileActivity.PROFILE_PIC_URL, profilePicUrl)
            }, EDIT_PROFILE_REQUEST)
        }
    }
}