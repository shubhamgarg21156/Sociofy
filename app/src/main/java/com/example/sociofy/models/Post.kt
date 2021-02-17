package com.example.sociofy.models

class Post(
    val text : String = "",
    val createdBy : Users = Users(),
    val createdAt : Long = 0,
    val likedBy : ArrayList<String> = ArrayList()
)