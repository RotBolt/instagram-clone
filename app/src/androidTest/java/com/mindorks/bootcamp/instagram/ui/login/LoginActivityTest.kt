package com.mindorks.bootcamp.instagram.ui.login

import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.TestComponentRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

class LoginActivityTest {
    private val component =
        TestComponentRule(InstrumentationRegistry.getInstrumentation().targetContext)
//    private val main = IntentsTestRule(LoginActivity::class.java, false, false)

//    @get:Rule
//    val chain = RuleChain.outerRule(component).around(main)

    @get:Rule
    val chain = RuleChain.outerRule(component)

    @Before
    fun setUp() {

    }

    @Test
    fun testCheckViewsDisplay() {
//        main.launchActivity(Intent(component.getContext(), LoginActivity::class.java))

        launch(LoginActivity::class.java)
        onView(withId(R.id.layoutEmail))
            .check(matches(isDisplayed()))

        onView(withId(R.id.layoutPassword))
            .check(matches(isDisplayed()))

        onView(withId(R.id.btnLogin))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenValidEmailAndValidPassword_whenLogin_shouldLaunchMainActivity(){
        launch(LoginActivity::class.java)

        onView(withId(R.id.etEmail)).perform(
            typeText("test@pui.com"),
            closeSoftKeyboard()
        )

        onView(withId(R.id.etPassword)).perform(
            typeText("puipuipui"),
            closeSoftKeyboard()
        )

        onView(withId(R.id.btnLogin)).perform(click())

        onView(withId(R.id.bottomNavView)).check(matches(isDisplayed()))
    }
}