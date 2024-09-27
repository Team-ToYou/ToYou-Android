package com.toyou.toyouandroid.model

data class ChooseModel(
    val message : String,
    val fromWho : String,
    val options : List<String>,
    val type : Int,
    var isButtonSelected : Boolean = false,
    val id : Long,
    )
