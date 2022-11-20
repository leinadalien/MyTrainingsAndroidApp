package com.ldnprod.timer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ldnprod.timer.Models.ExerciseModel

class ExerciseAdapter(val context: Context, val exercises:List<ExerciseModel>):RecyclerView.Adapter<ExerciseAdapter.exerciseViewHolder>() {
    inner class exerciseViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val title = view.findViewById<TextView>(R.id.task_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): exerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exercise_thumbnail_layout, parent, false)
        return exerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: exerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.title.text = exercise.title
    }

    override fun getItemCount(): Int {
        return exercises.size
    }
}