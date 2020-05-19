package com.mindorks.bootcamp.instagram.utils

import com.mindorks.bootcamp.instagram.data.model.User

object TestHelper {

    fun getTestUser() = User(
        "id",
        "Haruka",
        "haruka@pui.com",
        "access-token",
        "https://cloudStorage.api.com/hadagdhjh/pui.jpeg"
    )

}