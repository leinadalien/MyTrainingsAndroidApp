package com.ldnprod.timer.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.R
import com.ldnprod.timer.Utils.PlayTrainingEvent
import com.ldnprod.timer.Utils.TrainingEvent

class ExercisePreviewAdapter(var exercises: List<Exercise>, private val onEvent: (PlayTrainingEvent) -> Unit): RecyclerView.Adapter<ExercisePreviewAdapter.ExercisePreviewViewHolder>() {
    inner class ExercisePreviewViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val title: TextView = view.findViewById(R.id.exercise_title)
        val durationTextView: TextView = view.findViewById(R.id.exercise_duration_textview)
        val skipButton: Button = view.findViewById(R.id.skip_exercise_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercisePreviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exercise_preview_layout, parent, false)
        return ExercisePreviewViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ExercisePreviewViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.apply {
            title.text = exercise.description
            durationTextView.text = "${"%02d".format(exercise.duration / 60)}:${"%02d".format(exercise.duration % 60)}"

            skipButton.setOnClickListener { onEvent(PlayTrainingEvent.OnSkipClicked(position)) }
        }
    }

    override fun getItemCount(): Int {
        return exercises.size
    }
}