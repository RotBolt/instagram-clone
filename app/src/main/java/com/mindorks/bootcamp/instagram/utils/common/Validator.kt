package com.mindorks.bootcamp.instagram.utils.common

import com.mindorks.bootcamp.instagram.R
import java.util.regex.Pattern
import com.mindorks.bootcamp.instagram.utils.common.Validator.Validation.Field

object Validator{

    private val EMAIL_ADDRESS = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    private const val MIN_PASSWORD_LENGTH = 6

    data class Validation(val field:Field,val resource: Resource<Int>){
        enum class Field{
            EMAIL,
            PASSWORD
        }
    }

    data class ValidationResult(
        val email:Validation,
        val password:Validation
    )

    fun validateLoginFields(email:String?, password:String?):ValidationResult{
        val emailValidation = when{
            email.isNullOrBlank() -> Validation(Field.EMAIL, Resource.error(R.string.email_field_empty))
            !EMAIL_ADDRESS.matcher(email).matches() -> Validation(Field.EMAIL, Resource.error(R.string.email_field_invalid))
            else -> Validation(Field.EMAIL, Resource.success())
        }

        val passValidation = when{
            password.isNullOrBlank() -> Validation(Field.PASSWORD, Resource.error(R.string.password_field_empty))
            password.length < MIN_PASSWORD_LENGTH -> Validation(Field.PASSWORD, Resource.error(R.string.password_field_small_length))
            else -> Validation(Field.PASSWORD, Resource.success())
        }

        return ValidationResult(emailValidation,passValidation)
    }

}