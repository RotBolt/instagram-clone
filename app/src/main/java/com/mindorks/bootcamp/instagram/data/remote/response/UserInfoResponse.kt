package com.mindorks.bootcamp.instagram.data.remote.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserInfoResponse(
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
    val data: UserInfo

) {
    data class UserInfo(
        @SerializedName("id")
        @Expose
        val id: String,

        @SerializedName("name")
        @Expose
        val name: String,

        @SerializedName("profilePicUrl")
        @Expose
        val profilePicUrl: String? = null,

        @SerializedName("tagline")
        @Expose
        val tagline: String? = null
    )

}