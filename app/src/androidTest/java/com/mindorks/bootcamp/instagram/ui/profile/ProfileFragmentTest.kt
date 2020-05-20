package com.mindorks.bootcamp.instagram.ui.profile

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.TestComponentRule
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.utils.RVMatcher.atPositionOnView
import org.hamcrest.Matchers.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain


class ProfileFragmentTest {
    val componentRule =
        TestComponentRule(InstrumentationRegistry.getInstrumentation().targetContext)

    @get:Rule
    val chain = RuleChain.outerRule(componentRule)

    @Before
    fun setUp() {
        val userRespository = componentRule.testComponent!!.getUserRepository()
        val user = User(
            "id", "name", "email", "access-token", "profilePicUrl"
        )
        userRespository.saveCurrentUser(user)
    }

    @Test
    fun userInfoAvailable_shouldDisplayUserInfoDetails() {
        launchFragmentInContainer<ProfileFragment>(Bundle(), R.style.AppTheme)
        onView(withId(R.id.tvUserName)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(containsString("haruka"))))
        }

        onView(withId(R.id.tvBio)).apply {
            check(matches(isDisplayed()))
            check(matches(withText(containsString("Pui pui"))))
        }
    }

    @Test
    fun myPostsAvailable_shouldDisplayMyPosts(){
        launchFragmentInContainer<ProfileFragment>(Bundle(), R.style.AppTheme)

        onView(withId(R.id.rvMyPosts)).apply {
            check(matches(atPositionOnView(0, isDisplayed(),R.id.ivMyPostImage)))
        }
    }


}