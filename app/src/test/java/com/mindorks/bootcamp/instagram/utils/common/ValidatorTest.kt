package com.mindorks.bootcamp.instagram.utils.common

import com.mindorks.bootcamp.instagram.R
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.junit.Test

class ValidatorTest {


    @Test
    fun simpleValidationLogin() {
        val email = "rmaity@pui.com"
        val password = "puipuipui"
        val validationResult = Validator.validateLoginFields(email, password)
        validationResult.run {
            assertThat("Valid Email", this.email.resource.status == Status.SUCCESS)
            assertThat("Valid Password", this.password.resource.status == Status.SUCCESS)
        }
    }

    @Test
    fun givenValidEmailAndValidPasswd_whenValidate_shouldReturnSuccess() {
        val email = "rmaity@pui.com"
        val password = "puipuipui"
        val validationResult = Validator.validateLoginFields(email, password)
        assertThat(
            validationResult, Matchers.allOf(
                Matchers.hasProperty(
                    "email",
                    equalTo(
                        Validator.Validation(
                            Validator.Validation.Field.EMAIL,
                            Resource.success()
                        )
                    )
                ),
                Matchers.hasProperty(
                    "password",
                    equalTo(
                        Validator.Validation(
                            Validator.Validation.Field.PASSWORD,
                            Resource.success()
                        )
                    )
                )
            )
        )
    }

    @Test
    fun givenInvalidEmailAndValidPasswd_whenValidate_shouldReturnError() {
        val email = "rmaity"
        val password = "puipuipui"
        val validationResult = Validator.validateLoginFields(email, password)
        assertThat(
            validationResult, Matchers.allOf(
                Matchers.hasProperty(
                    "email",
                    equalTo(
                        Validator.Validation(
                            Validator.Validation.Field.EMAIL,
                            Resource.error(R.string.email_field_invalid)
                        )
                    )
                ),
                Matchers.hasProperty(
                    "password",
                    equalTo(
                        Validator.Validation(
                            Validator.Validation.Field.PASSWORD,
                            Resource.success()
                        )
                    )
                )
            )
        )
    }

    @Test
    fun givenValidEmailAndInvalidPasswd_whenValidate_shouldReturnError() {
        val email = "rmaity@pui.com"
        val password = "puipu"
        val validationResult = Validator.validateLoginFields(email, password)
        assertThat(
            validationResult, Matchers.allOf(
                Matchers.hasProperty(
                    "email",
                    equalTo(
                        Validator.Validation(
                            Validator.Validation.Field.EMAIL,
                            Resource.success()
                        )
                    )
                ),
                Matchers.hasProperty(
                    "password",
                    equalTo(
                        Validator.Validation(
                            Validator.Validation.Field.PASSWORD,
                            Resource.error(R.string.password_field_small_length)
                        )
                    )
                )
            )
        )
    }
}