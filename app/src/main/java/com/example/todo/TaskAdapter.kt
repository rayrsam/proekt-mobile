package com.example.todo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.databinding.TaskBinding

interface TaskActionsListener{
    fun onDelete(task: Task)
    fun onEditText(task: Task)
    fun onEditTag(task: Task)
    fun onDone(task: Task)
}

class TaskHolder(val binding: TaskBinding) : RecyclerView.ViewHolder(binding.root)

class TaskAdapter(val action: TaskActionsListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var taskList = mutableListOf<Task>()
        set(newTaskList) {
            field = newTaskList
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val holder = TaskHolder(TaskBinding.inflate(inflater, parent, false))

        return holder
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val task = taskList[position]

        (holder as TaskHolder).binding.apply {
            delBtn.setOnClickListener{ action.onDelete(task) }
            edBtn.setOnClickListener{ action.onEditText(task) }
            tagBtn.setOnClickListener{ action.onEditTag(task) }
            doneBtn.setOnClickListener{ action.onDone(task) }
            taskText.setText(task.text)
            taskText.focusable = View.NOT_FOCUSABLE
            taskText.backgroundTintList = when (task.status){
                -1 -> android.content.res.ColorStateList.valueOf(colorDone)
                else -> android.content.res.ColorStateList.valueOf(colorBase)
            }
        }
    }

    companion object{
        val colorBase = Color.argb(255, 255, 235,59)
        val colorDone = Color.argb(255, 136, 136, 136)
    }
}