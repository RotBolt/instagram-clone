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
import com.mindorks.bootcamp.instagram.ui.login.signUp.SignUpActivity
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

class SignUpActivityTest {

    private val componentRule = TestComponentRule(InstrumentationRegistry.getInstrumentation().targetContext)

    @get:Rule
    val chain = RuleChain.outerRule(componentRule)

    @Test
    fun testCheckViewsDisplay(){
        launch(SignUpActivity::class.java)
        onView(withId(R.id.layoutEmail))
            .check(matches(isDisplayed()))

        onView(withId(R.id.layoutPassword))
            .check(matches(isDisplayed()))

        onView(withId(R.id.layoutName))
            .check(matches(isDisplayed()))

        onView(withId(R.id.btnSignUp))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenValidEmailPasswordAndName_whenSignUp_shouldLaunchMainActivity(){
        launch(SignUpActivity::class.java)

        onView(withId(R.id.etEmail)).perform(
            typeText("test@pui.com"),
            closeSoftKeyboard()
        )

        onView(withId(R.id.etPassword)).perform(
            typeText("puipuipui"),
            closeSoftKeyboard()
        )

        onView(withId(R.id.etName)).perform(
            typeText("hola meen"),
            closeSoftKeyboard()
        )

        onView(withId(R.id.btnSignUp)).perform(click())

        onView(withId(R.id.bottomNavView)).check(matches(isDisplayed()))
    }
}