package com.mindorks.bootcamp.instagram.ui.profile.myPosts

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.mindorks.bootcamp.instagram.data.remote.response.MyPostListResponse
import com.mindorks.bootcamp.instagram.ui.base.BaseAdapter


class MyPostsAdapter(
    parentLifeCycle: Lifecycle,
    myPosts: ArrayList<MyPostListResponse.MyPost>
) : BaseAdapter<MyPostListResponse.MyPost, MyPostItemViewHolder>(parentLifeCycle, myPosts){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostItemViewHolder = MyPostItemViewHolder(parent)

}