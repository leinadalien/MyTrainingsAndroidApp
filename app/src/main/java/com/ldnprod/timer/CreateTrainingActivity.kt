package com.ldnprod.timer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ldnprod.timer.Adapters.ExerciseAdapter
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Utils.TrainingEvent
import com.ldnprod.timer.ViewModels.TrainingViewModel.TrainingViewModel
import com.ldnprod.timer.ViewModels.TrainingViewModel.TrainingViewModelEvent
import com.ldnprod.timer.databinding.ActivityCreateTrainingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateTrainingActivity : AppCompatActivity() {
    private val trainingViewModel by viewModels<TrainingViewModel>()
    private lateinit var binding: ActivityCreateTrainingBinding
    private lateinit var exerciseAdapter: ExerciseAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        exerciseAdapter = ExerciseAdapter(trainingViewModel.exercises)
        binding.createButton.setOnClickListener {
            trainingViewModel.onEvent(TrainingEvent.OnAddButtonClick)
        }
        binding.doneButton.setOnClickListener {
            trainingViewModel.onEvent(TrainingEvent.OnDoneButtonClick)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = exerciseAdapter
        lifecycleScope.launch {
            trainingViewModel.viewModelEvent.collect { event ->
                when(event) {
                    is TrainingViewModelEvent.ExerciseInserted -> {
                        exerciseAdapter.notifyItemInserted(event.position)
                    }
                    is TrainingViewModelEvent.ExerciseMoved -> {
                        exerciseAdapter.notifyItemMoved(event.fromPosition, event.toPosition)
                    }
                    is TrainingViewModelEvent.ExerciseRemoved -> {
                        exerciseAdapter.notifyItemRemoved(event.position)
                    }
                    is TrainingViewModelEvent.ExerciseDataSetChanged -> {
                        exerciseAdapter = ExerciseAdapter(trainingViewModel.exercises)
                        exerciseAdapter.notifyDataSetChanged()
                    }
                    is TrainingViewModelEvent.PopBackStack -> {
                        finish()
                    }
                    is TrainingViewModelEvent.CreateExercise -> {
                        showExerciseInfoDialog()
                    }
                    else -> Unit
                }
            }
        }
    }
    private fun showExerciseInfoDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.add_exercise_dialog_layout, null)
        val dialog = AlertDialog.Builder(this)
        dialog.setView(view)
        dialog.setPositiveButton("Ok") {
                dialog, _ ->
            val title = view.findViewById<EditText>(R.id.title_edittext).text.toString()
            val exercise = Exercise(description = title, duration = 10, trainingId = 0)
            trainingViewModel.addExercise(exercise)
            checkVisibilityDoneButton()
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
    private fun checkVisibilityDoneButton(){
        binding.doneButton.visibility = if (trainingViewModel.exercises.isNotEmpty()) View.VISIBLE else View.GONE
    }
}