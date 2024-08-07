package com.toyou.toyouandroid.utils

import android.graphics.BitmapFactory
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
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

@BindingAdapter("android:setBackground")
fun setBackground(button: AppCompatButton, resource: Int?) {
    resource?.let { button.setBackgroundResource(it) }
}

@BindingAdapter("formattedNickname")
fun setFormattedNickname(textView: TextView, nickname: String?) {
    nickname?.let {
        textView.text = if (it.length > 3) "${it.substring(0, 3)}..." else it
    }
}