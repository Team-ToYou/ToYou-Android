package com.toyou.toyouandroid.model

data class PreviewChooseModel(
    val message : String,
    val fromWho : String,
    val options : List<String>,
    val type : Int,
    var answer : String
)
