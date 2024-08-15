package com.toyou.toyouandroid.model

data class PreviewCardModel(
    val question : String,
    var answer : String,
    val type : Int,
    val fromWho : String,
    val options : List<String>?,

    )
