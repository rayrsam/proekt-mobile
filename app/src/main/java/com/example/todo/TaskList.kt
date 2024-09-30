package com.example.todo

import kotlinx.serialization.json.Json


typealias TaskListener = (blocks: List<Task>) -> Unit

class TaskList {
    private var taskList = mutableListOf<Task>()
    lateinit var listener: TaskListener
    private var index = 0

    fun getTasks() : MutableList<Task>{ return taskList }

    fun addTask(){
        val newTask = Task(index, "Пустая Заметка", "", 1)
        taskList.add(newTask)
        index++
        notifyChanges()
    }

    fun deleteTask(task: Task){
        taskList.remove(task)
        notifyChanges()
    }

    fun doneTask(task: Task){
        task.status *= -1
        notifyChanges()
    }

    private fun notifyChanges(){
        listener.invoke(taskList)
    }

    fun readFromFile(data: String){
        taskList.clear()
        index = 0

        taskList = Json.decodeFromString<MutableList<Task>>(data)

        notifyChanges()
    }


}