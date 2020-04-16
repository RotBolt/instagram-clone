package com.mindorks.bootcamp.instagram.ui.home.posts

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.ui.base.BaseAdapter

class PostAdapter(
    parentLifecycle: Lifecycle,
    posts: ArrayList<Post>
) : BaseAdapter<Post, PostItemViewHolder>(parentLifecycle, posts){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostItemViewHolder = PostItemViewHolder(parent)

}