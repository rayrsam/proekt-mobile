package com.example.todo

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo.databinding.MainActivityBinding
import com.example.todo.databinding.TaskBinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    private lateinit var binding: MainActivityBinding
    private lateinit var adapter: TaskAdapter
    private val taskList = TaskList()

    private val taskListener: TaskListener = {
        adapter.taskList = it.toMutableList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskList.listener = taskListener

        adapter = TaskAdapter(object : TaskActionsListener {
            override fun onDelete(task: Task) {
                taskList.deleteTask(task)
            }

            override fun onEditText(task: Task) {
                editTaskText(task)
            }

            override fun onEditTag(task: Task) {
                editTaskTag(task)
            }

            override fun onDone(task: Task) {
                taskList.doneTask(task)
            }
        })
        val layoutManager = LinearLayoutManager(this)

        binding.apply {
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter

            addBtn.setOnClickListener{ addTask() }
            saveBtn.setOnClickListener { saveLauncher.launch("TaskList")}
            loadBtn.setOnClickListener { loadLauncher.launch(arrayOf("text/plain"))}

        }

    }
    private fun addTask() {
        taskList.addTask()
    }

    private fun editTaskText(task: Task){
        val taskBinding = TaskBinding.inflate(layoutInflater)
        taskBinding.buttons.visibility = View.GONE
        taskBinding.taskText.setText(task.text)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Изменить ТЕКСТ")
            .setView(taskBinding.root)
            .setPositiveButton("OK") { _, _ ->
                task.text = taskBinding.taskText.text.toString()

                val id = taskList.getTasks().indexOfFirst { it.id == task.id }
                adapter.notifyItemChanged(id)
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
        taskBinding.taskText.setText(task.tag)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Изменить ТЕГИ")
            .setView(taskBinding.root)
            .setPositiveButton("OK") { _, _ ->
                task.tag = taskBinding.taskText.text.toString()
            }
            .setNegativeButton("Отмена") { _, _ -> run {} }
            .create()
        dialog.show()
    }


    private val loadLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            try {
                uri?.let { openFile(it) }
            } catch (e: Exception) {
                showError(e)
            }
        }
    private val saveLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { result ->
            try {
                result?.let { saveFile(it) }
            } catch (e: Exception) {
                showError(e)
            }
        }

    private fun openFile(uri: Uri) {
        val data = contentResolver.openInputStream(uri)?.use {
            String(it.readBytes())
        } ?: throw IllegalStateException()
        taskList.readFromFile(data)
    }

    private fun saveFile(uri: Uri) {
        contentResolver.openOutputStream(uri)?.use {
            val jsonResult = Json
            val bytes = jsonResult.encodeToString(taskList.getTasks()).toByteArray()
            it.write(bytes)
        } ?: throw IllegalStateException()
    }

    private fun showError(e: Exception) {
        Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
    }
}


