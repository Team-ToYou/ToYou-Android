package com.toyou.toyouandroid.model

data class CardShortModel(
    val message : String,
    val fromWho : String,
    val questionType : Int,
    var isButtonSelected : Boolean = false,
)
