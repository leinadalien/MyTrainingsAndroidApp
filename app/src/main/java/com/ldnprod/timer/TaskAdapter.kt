package com.ldnprod.timer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ldnprod.timer.Models.TaskModel

class TaskAdapter(val context: Context, val tasks:List<TaskModel>):RecyclerView.Adapter<TaskAdapter.taskViewHolder>() {
    inner class taskViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val title = view.findViewById<TextView>(R.id.task_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): taskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_thumbnail_layout, parent, false)
        return taskViewHolder(view)
    }

    override fun onBindViewHolder(holder: taskViewHolder, position: Int) {
        val task = tasks[position]
        holder.title.text = task.title
    }

    override fun getItemCount(): Int {
        return tasks.size
    }
}