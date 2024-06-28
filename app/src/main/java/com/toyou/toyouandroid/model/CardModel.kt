package com.toyou.toyouandroid.model

data class CardModel(
    val message : String,
    val fromWho : String,
    var isButtonSelected : Boolean = false,
)