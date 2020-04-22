package com.mindorks.bootcamp.instagram.ui.postDetails

import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.ActivityComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseActivity
import com.mindorks.bootcamp.instagram.utils.common.GlideHelper
import kotlinx.android.synthetic.main.activity_post_detail.*

class PostDetailActivity : BaseActivity<PostDetailsViewModel>() {

    companion object {
        const val POST_ID = "post_id"
    }

    override fun provideLayoutId(): Int = R.layout.activity_post_detail

    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        val postId = intent.getStringExtra(POST_ID)
        viewModel.onFetchPostDetail(postId)
        ivLike.setOnClickListener { viewModel.onLikeClick() }
        ivPrev.setOnClickListener { goBack() }
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.isLiked.observe(this, Observer {
            if (it) ivLike.setImageResource(R.drawable.ic_heart_selected)
            else ivLike.setImageResource(R.drawable.ic_heart_unselected)
        })

        viewModel.likesCount.observe(this, Observer {
            tvLikesCount.text = getString(R.string.post_like_label, it)
        })

        viewModel.name.observe(this, Observer {
            tvName.text = it
        })

        viewModel.postTime.observe(this, Observer {
            tvTime.text = it
        })

        viewModel.postImageDetail.observe(this, Observer {
            it?.run {
                val glideRequest = Glide.with(this@PostDetailActivity)
                    .load(GlideHelper.getProtectedUrl(url, headers))


                if (placeholderHeight > 0 && placeholderWidth > 0) {
                    val layoutParams = ivPost.layoutParams as ViewGroup.LayoutParams
                    layoutParams.height = placeholderHeight
                    layoutParams.width = placeholderWidth
                    ivPost.layoutParams = layoutParams

                    glideRequest
                        .apply(RequestOptions().override(placeholderWidth, placeholderHeight))
                        .apply(RequestOptions().placeholder(R.drawable.ic_photo))
                }

                glideRequest.into(ivPost)
            }
        })

        viewModel.profileImage.observe(this, Observer {
            it?.run {
                val glideRequest = Glide.with(this@PostDetailActivity)
                    .load(GlideHelper.getProtectedUrl(url, headers))
                    .apply(RequestOptions().circleCrop())
                    .apply(RequestOptions().placeholder(R.drawable.ic_profile))

                if (placeholderWidth > 0 && placeholderHeight > 0) {
                    val layoutParams = ivProfile.layoutParams as ViewGroup.LayoutParams
                    layoutParams.height = placeholderHeight
                    layoutParams.width = placeholderWidth
                    ivPost.layoutParams = layoutParams

                    glideRequest
                        .apply(RequestOptions().override(placeholderWidth, placeholderHeight))
                }

                glideRequest.into(ivProfile)
            }
        })
    }
}