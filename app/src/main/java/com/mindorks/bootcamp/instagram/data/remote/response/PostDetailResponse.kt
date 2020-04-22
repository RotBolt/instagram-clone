package com.mindorks.bootcamp.instagram.data.remote.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mindorks.bootcamp.instagram.data.model.Post

data class PostDetailResponse(

    @SerializedName("statusCode")
    @Expose
    val statusCode: String,

    @SerializedName("status")
    @Expose
    val status: Int,

    @SerializedName("message")
    @Expose
    val message: String,

    @SerializedName("data")
    @Expose
    val data: Post
)