package com.ldnprod.timer.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ldnprod.timer.TrainingInfoActivity
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.R

class TrainingAdapter(val context: Context, var trainings: List<Training>): RecyclerView.Adapter<TrainingAdapter.TrainingViewHolder>() {
    inner class TrainingViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val title = view.findViewById<TextView>(R.id.training_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.training_thumbnail_layout, parent, false)
        return TrainingViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val training = trainings[position]
        holder.title.text = training.title
        holder.view.setOnClickListener {
            val intent = Intent(context, TrainingInfoActivity::class.java).putExtra("trainingId", training.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return trainings.size
    }
}