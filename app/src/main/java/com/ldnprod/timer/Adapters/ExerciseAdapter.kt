package com.ldnprod.timer.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.R
import com.ldnprod.timer.Utils.TrainingEvent
import com.ldnprod.timer.ViewModels.TrainingViewModel.TrainingViewModel

class ExerciseAdapter(var exercises: List<Exercise>, private val onEvent: (TrainingEvent) -> Unit):RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {
    inner class ExerciseViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val title: TextView = view.findViewById(R.id.exercise_title)
        val deleteButton: Button = view.findViewById(R.id.delete_exercise_button)
        val durationTextView: TextView = view.findViewById(R.id.exercise_duration_textview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exercise_thumbnail_layout, parent, false)
        return ExerciseViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.apply {
            title.text = exercise.description
            durationTextView.text = "${"%02d".format(exercise.duration / 60)}:${"%02d".format(exercise.duration % 60)}"

            view.setOnClickListener { onEvent(TrainingEvent.OnExerciseClick(exercise, position)) }
            deleteButton.setOnClickListener { onEvent(TrainingEvent.OnDeleteExerciseClick(exercise, position)) }
            durationTextView.setOnClickListener { onEvent(TrainingEvent.OnExerciseClick(exercise, position))  }
        }

    }

    override fun getItemCount(): Int {
        return exercises.size
    }
}