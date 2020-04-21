package com.mindorks.bootcamp.instagram.data.remote.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

data class MyPostListResponse(
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
    val data: List<MyPost>
) {

    data class MyPost(
        @SerializedName("id")
        @Expose
        val id: String,
        @SerializedName("imgUrl")
        @Expose
        val imgUrl: String,
        @SerializedName("imgWidth")
        @Expose
        val imgWidth: Int,
        @SerializedName("imgHeight")
        @Expose
        val imgHeight: Int,
        @SerializedName("createdAt")
        @Expose
        val createdAt: Date
    )
}