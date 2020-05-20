package com.mindorks.bootcamp.instagram.ui.photo

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.TestComponentRule
import com.mindorks.bootcamp.instagram.data.model.User
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

class PhotoFragmentTest {

    val componentRule = TestComponentRule(InstrumentationRegistry.getInstrumentation().targetContext)

    @get:Rule
    val chain = RuleChain.outerRule(componentRule)


    @Before
    fun setUp() {
        val userRespository = componentRule.testComponent!!.getUserRepository()
        val user = User(
            "id", "name", "test@pui.com", "access-token", "profilePicUrl"
        )
        userRespository.saveCurrentUser(user)
    }

    @Test
    fun testCheckViewsDisplay(){
        launchFragmentInContainer<PhotoFragment>(Bundle(),R.style.AppTheme)
        onView(withId(R.id.viewCamera)).check(matches(isDisplayed()))
        onView(withId(R.id.viewGallery)).check(matches(isDisplayed()))
    }
}