package com.mindorks.bootcamp.instagram.ui.profile

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.TestComponentRule
import com.mindorks.bootcamp.instagram.data.model.User
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain


class EditProfileActivityTest {

    val componentRule = TestComponentRule(InstrumentationRegistry.getInstrumentation().targetContext)

    private val main = IntentsTestRule(EditProfileActivity::class.java, false, false)

    @get:Rule
    val chain = RuleChain.outerRule(componentRule).around(main)

    @Before
    fun setUp() {
        val userRespository = componentRule.testComponent!!.getUserRepository()
        val user = User(
            "id", "name", "test@pui.com", "access-token", "profilePicUrl"
        )
        userRespository.saveCurrentUser(user)
    }

    @Test
    fun userInfoAvailable_shouldDisplayUserInfoInFields(){
        main.launchActivity(Intent(componentRule.getContext(),EditProfileActivity::class.java).apply {
            putExtra(EditProfileActivity.NAME_FIELD, "haruka")
            putExtra(EditProfileActivity.BIO_FIELD,"pui pui")
            putExtra(EditProfileActivity.PROFILE_PIC_URL,"https://cloudstorage.api.com/fhej/pui.jpeg")
        })

        onView(withId(R.id.etName)).check(matches(withText("haruka")))
        onView(withId(R.id.etBio)).check(matches(withText("pui pui")))
        onView(withId(R.id.tvEmail)).check(matches(withText("test@pui.com")))
    }


}