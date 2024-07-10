package com.toyou.toyouandroid.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData

@BindingAdapter("imageResource")
fun setImageResource(imageView: ImageView, resource: LiveData<Int>?) {
    resource?.value?.let {
        imageView.setImageResource(it)
    }
}
