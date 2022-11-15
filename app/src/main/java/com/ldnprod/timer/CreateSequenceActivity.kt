package com.ldnprod.timer

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.ldnprod.timer.Models.TaskModel
import com.ldnprod.timer.databinding.ActivityCreateSequenceBinding

class CreateSequenceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateSequenceBinding
    private lateinit var taskAdapter: TaskAdapter
    private var tasks = ArrayList<TaskModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateSequenceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        taskAdapter = TaskAdapter(this, tasks)
        binding.createButton.setOnClickListener { showTaskInfo() }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = taskAdapter
    }
    private fun showTaskInfo() {
        val view = LayoutInflater.from(this).inflate(R.layout.add_task_dialog_layout, null)
        val dialog = AlertDialog.Builder(this)
        dialog.setView(view)
        dialog.setPositiveButton("Ok") {
                dialog, _ ->
            val title = view.findViewById<EditText>(R.id.title_edittext).text.toString()
            tasks.add(TaskModel(title))
            taskAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Successfully", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.setNegativeButton("Cancel"){
                dialog, _ ->
            Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
            dialog.dismiss()

        }
        dialog.create()
        dialog.show()
    }

}