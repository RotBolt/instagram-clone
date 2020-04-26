package com.mindorks.bootcamp.instagram.ui.home

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.TestComponentRule
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.utils.RVMatcher.atPositionOnView
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

class HomeFragmentTest {

    val component = TestComponentRule(InstrumentationRegistry.getInstrumentation().targetContext)

    @get:Rule
    val chain = RuleChain.outerRule(component)

    @Before
    fun setUp() {
        val userRespository = component.testComponent!!.getUserRepository()
        val user = User(
            "id", "name", "email", "access-token", "profilePicUrl"
        )
        userRespository.saveCurrentUser(user)
    }

    @Test
    fun postsAvailable_shouldDisplay() {
        launchFragmentInContainer<HomeFragment>(Bundle(), R.style.AppTheme)
        onView(withId(R.id.rvPosts)).check(matches(isDisplayed()))

        onView(withId(R.id.rvPosts))
            .check(matches(atPositionOnView(0, withText("name1"), R.id.tvName)))

        onView(withId(R.id.rvPosts))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(1))
            .check(matches(atPositionOnView(1, withText("name2"), R.id.tvName)))

    }
}