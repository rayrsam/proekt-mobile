package com.example.todo
import kotlinx.serialization.Serializable

@Serializable
data class Task(val id:Int,
           var text:String = "Пустая заметка",
           var tag:String = "",
           var status: Int = 1)