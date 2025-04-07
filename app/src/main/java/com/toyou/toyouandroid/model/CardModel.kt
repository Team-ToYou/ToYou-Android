package com.toyou.toyouandroid.model

data class CardModel(
    val message : String,
    val fromWho : String,
    val questionType : Int,
    var isButtonSelected : Boolean = false,
    val id : Long
    )