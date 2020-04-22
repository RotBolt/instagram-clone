package com.mindorks.bootcamp.instagram.data.remote.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UpdateInfoRequest(
    @SerializedName("name")
    @Expose
    val name: String,

    @SerializedName("profilePicUrl")
    @Expose
    val profilePicUrl: String?,

    @SerializedName("tagline")
    @Expose
    val tagline: String
)