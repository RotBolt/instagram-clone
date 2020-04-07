package com.mindorks.bootcamp.instagram.utils.common

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.EditText


fun EditText.handleClearIcon(text: String, drawable: Drawable?) {
    if (this.isFocused) {
        this.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            if (text.isNotEmpty()) drawable else null,
            null
        )
    }
}

fun EditText.setupRightDrawable( rightDrawable: Drawable?,onClickAction: () -> Unit) {
    this.setOnTouchListener { view, motionEvent ->
        var isConsumed = false
        if (view is EditText) {
            if (motionEvent.x >= view.width - view.totalPaddingEnd) {
                onClickAction()
                isConsumed = true
            }
        }
        isConsumed
    }
    this.onFocusChangeListener = View.OnFocusChangeListener { _, isFocused: Boolean ->
        if (!isFocused) {
            this.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                null,
                null
            )
        } else if (this.text.toString().isNotEmpty()) {
            this.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                rightDrawable,
                null
            )
        }
    }
}