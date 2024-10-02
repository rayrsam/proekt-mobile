package com.example.todo

import kotlinx.serialization.json.Json


typealias TaskListener = (blocks: List<Task>) -> Unit

class TaskList {
    var tasks = mutableListOf<Task>()
    lateinit var listener: TaskListener
    private var index = 0

    fun addTask(text : String) : Task{
        val newTask = Task(
            index.toString(),
            text,
            mutableListOf<String>(),
            1)
        tasks.add(newTask)
        index++
        notifyChanges()
        return newTask
    }

    fun addTask(task: Task){
        tasks.add(task)
        notifyChanges()
    }

    fun deleteTask(task: Task){
        tasks.remove(task)
        notifyChanges()
    }

    fun doneTask(task: Task){
        task.status *= -1
        notifyChanges()
    }

    fun setNewDataSet(data: List<Task>){
        tasks = data.toMutableList()
        notifyChanges()
    }

    private fun notifyChanges(){
        listener.invoke(tasks)
    }


}