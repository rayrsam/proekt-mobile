package com.example.todo

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo.databinding.MainActivityBinding
import com.example.todo.databinding.TaskBinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class MainActivity : ComponentActivity() {
    private lateinit var binding: MainActivityBinding
    private lateinit var adapter: TaskAdapter
    private val taskList = TaskList()
    private lateinit var taskAPI : TaskAPI

    private val taskListener: TaskListener = {
        adapter.taskList = it.toMutableList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/api/Note/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        taskAPI = retrofit.create(TaskAPI::class.java)

        taskList.listener = taskListener

        adapter = TaskAdapter(object : TaskActionsListener {

            override fun onDelete(task: Task) {

                taskAPI.deleteTask(task.id).enqueue(object : Callback<Task>{
                    override fun onResponse(call: Call<Task>, response: Response<Task>) {
                        taskList.deleteTask(task)
                    }

                    override fun onFailure(call: Call<Task>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                })
            }

            override fun onEditText(task: Task) {
                editTaskText(task)
            }

            override fun onEditTag(task: Task) {
                editTaskTag(task)
            }

            override fun onDone(task: Task) {
                taskList.doneTask(task)
                onUpdate(task)
            }
        })
        val layoutManager = LinearLayoutManager(this)

        binding.apply {
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter

            addBtn.setOnClickListener{ addTask() }
            searchBtn.setOnClickListener{

                taskAPI.getList(tagString.text.toString()).enqueue(object : Callback<List<Task>> {
                    override fun onResponse(
                        call: Call<List<Task>>,
                        response: Response<List<Task>>
                    ) {
                        taskList.setNewDataSet(response.body()!!)
                    }
                    override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                        TODO("Not yet implemented")
                    }
                })

            }
        }

        binding.searchBtn.performClick()
    }

    private fun addTask() {
        val task = Task(
            "",
            "Пустая заметка",
            mutableListOf<String>(""),
            1
        )

        Log.d("query", Json.encodeToString(task.text))

        taskAPI.createTask(task).enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                val id = response.body()
                if (id != null){
                    task.id = id
                    taskList.addTask(task)
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun onUpdate(task: Task){
        taskAPI.updateTask(task).enqueue( object : Callback<Task> {
            override fun onResponse(call: Call<Task>, response: Response<Task>) {
            }

            override fun onFailure(call: Call<Task>, t: Throwable) {
            }

        })
    }


    private fun editTaskText(task: Task){
        val taskBinding = TaskBinding.inflate(layoutInflater)
        taskBinding.buttons.visibility = View.GONE
        taskBinding.taskText.setText(task.text)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Изменить текст")
            .setView(taskBinding.root)
            .setPositiveButton("OK") { _, _ ->
                task.text = taskBinding.taskText.text.toString()
                val id = taskList.tasks.indexOfFirst { it.id == task.id }
                adapter.notifyItemChanged(id)

                onUpdate(task)
            }
            .setNegativeButton("Отмена") { _, _ -> run {} }
            .setNeutralButton("Удалить") { _, _ ->
                adapter.action.onDelete(task)
            }
            .create()
        dialog.show()
    }

    private fun editTaskTag(task: Task){
        val taskBinding = TaskBinding.inflate(layoutInflater)
        taskBinding.buttons.visibility = View.GONE
        taskBinding.taskText.setText(task.tag.joinToString("_"))

        val dialog = AlertDialog.Builder(this)
            .setTitle("Изменить тэги (через \"_\")")
            .setView(taskBinding.root)
            .setPositiveButton("OK") { _, _ ->
                task.tag = taskBinding.taskText.text
                    .toString().split("_").toMutableList()

                onUpdate(task)
            }
            .setNegativeButton("Отмена") { _, _ -> run {} }
            .create()
        dialog.show()
    }
}


