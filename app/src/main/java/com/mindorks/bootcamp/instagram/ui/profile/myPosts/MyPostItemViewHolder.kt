package com.mindorks.bootcamp.instagram.ui.profile.myPosts

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.data.remote.response.MyPostListResponse
import com.mindorks.bootcamp.instagram.di.component.ViewHolderComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseItemViewHolder
import com.mindorks.bootcamp.instagram.ui.postDetails.PostDetailActivity
import com.mindorks.bootcamp.instagram.utils.common.GlideHelper
import kotlinx.android.synthetic.main.layout_item_my_post.view.*

class MyPostItemViewHolder(parent: ViewGroup) :
    BaseItemViewHolder<MyPostListResponse.MyPost, MyPostItemViewModel>(
        R.layout.layout_item_my_post,
        parent
    ) {
    override fun injectDependencies(viewHolderComponent: ViewHolderComponent) {
        viewHolderComponent.inject(this)
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.myPostImageDetails.observe(this, Observer {
            val glideRequest = Glide
                .with(itemView.ivMyPostImage.context)
                .load(GlideHelper.getProtectedUrl(it.url, it.headers))
                .apply(RequestOptions().placeholder(R.drawable.ic_photo))
                .apply(RequestOptions().centerCrop())
            glideRequest.into(itemView.ivMyPostImage)
        })

        viewModel.launchPostDetail.observe(this, Observer {
            it.getIfNotHandled()?.let { postId: String ->
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    itemView.context as Activity,
                    itemView.ivMyPostImage,
                    itemView.context.getString(R.string.shared_element_post)
                )
                itemView.context.startActivity(
                    Intent(
                        itemView.context,
                        PostDetailActivity::class.java
                    ).apply {
                        putExtra(PostDetailActivity.POST_ID, postId)
                    }, options.toBundle()
                )
            }
        })
    }

    override fun setupView(view: View) {
        itemView.ivMyPostImage.setOnClickListener {
            viewModel.onMyPostClick()
        }
    }


}