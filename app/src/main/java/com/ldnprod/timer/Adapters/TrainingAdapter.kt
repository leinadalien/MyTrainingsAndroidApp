package com.ldnprod.timer.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ldnprod.timer.TrainingInfoActivity
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.R
import com.ldnprod.timer.Utils.TrainingListEvent

class TrainingAdapter(var trainings: List<Training>, private val onEvent: (TrainingListEvent) -> Unit ): RecyclerView.Adapter<TrainingAdapter.TrainingViewHolder>() {
    inner class TrainingViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val title : TextView = view.findViewById(R.id.training_title)
        val deleteButton: Button = view.findViewById(R.id.delete_training_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.training_thumbnail_layout, parent, false)
        return TrainingViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val training = trainings[position]
        holder.apply {
            title.text = training.title
            view.setOnClickListener { onEvent(TrainingListEvent.OnTrainingClick(training)) }
            deleteButton.setOnClickListener { onEvent(TrainingListEvent.OnDeleteTrainingClick(training, position)) }
        }
    }

    override fun getItemCount(): Int {
        return trainings.size
    }
}