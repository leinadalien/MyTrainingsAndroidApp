package com.ldnprod.timer.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.R

class ExerciseAdapter(val exercises:List<Exercise>):RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {
    inner class ExerciseViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val title = view.findViewById<TextView>(R.id.exercise_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exercise_thumbnail_layout, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.title.text = exercise.description
        holder.view.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return exercises.size
    }
}