package com.ldnprod.mytrainings.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ldnprod.mytrainings.Entities.Training
import com.ldnprod.mytrainings.R
import com.ldnprod.mytrainings.Utils.TrainingListEvent

class TrainingAdapter(var trainings: List<Training>, private val onEvent: (TrainingListEvent) -> Unit ): RecyclerView.Adapter<TrainingAdapter.TrainingViewHolder>() {
    inner class TrainingViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val title : TextView = view.findViewById(R.id.training_title)
        val deleteButton: ImageButton = view.findViewById(R.id.delete_training_button)
        val editButton: ImageButton = view.findViewById(R.id.edit_training_button)
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
            editButton.setOnClickListener { onEvent(TrainingListEvent.OnEditTrainingClick(training)) }
        }
    }

    override fun getItemCount(): Int {
        return trainings.size
    }
}