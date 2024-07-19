package com.toyou.toyouandroid.utils

import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import java.io.File

@BindingAdapter("imageResource")
fun setImageResource(imageView: ImageView, resource: LiveData<Int>?) {
    resource?.value?.let {
        imageView.setImageResource(it)
    }
}

@BindingAdapter("imagePath")
fun loadImage(view: ImageView, imagePath: String) {
    val imgFile = File(imagePath)
    if (imgFile.exists()) {
        val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
        view.setImageBitmap(bitmap)
    }
}