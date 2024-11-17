package com.toyou.toyouandroid.utils

import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter

@BindingAdapter("android:setBackground")
fun setBackground(button: AppCompatButton, resource: Int?) {
    resource?.let { button.setBackgroundResource(it) }
}