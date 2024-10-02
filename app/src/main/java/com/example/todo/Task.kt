package com.example.todo
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    var id:String,
    var text:String,
    var tag:MutableList<String>,
    var status: Int
)