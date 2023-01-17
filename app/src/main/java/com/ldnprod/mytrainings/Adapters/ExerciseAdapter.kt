package com.ldnprod.mytrainings.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ldnprod.mytrainings.Entities.Exercise
import com.ldnprod.mytrainings.R
import com.ldnprod.mytrainings.Utils.TrainingEvent

class ExerciseAdapter(var exercises: List<Exercise>, private val onEvent: (TrainingEvent) -> Unit):RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {
    inner class ExerciseViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val title: TextView = view.findViewById(R.id.exercise_title)
        val deleteButton: ImageButton = view.findViewById(R.id.delete_exercise_button)
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