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
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.Utils.TrainingEvent
import com.ldnprod.timer.Utils.UIEvent
import com.ldnprod.timer.ViewModels.TrainingViewModel
import com.ldnprod.timer.ViewModels.TrainingListViewModel
import com.ldnprod.timer.databinding.ActivityCreateTrainingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateTrainingActivity : AppCompatActivity() {
    private val trainingViewModel by viewModels<TrainingViewModel>()
    private lateinit var binding: ActivityCreateTrainingBinding
    private lateinit var exerciseAdapter: ExerciseAdapter
    private var exercises = ArrayList<Exercise>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        exerciseAdapter = ExerciseAdapter(exercises)
        binding.createButton.setOnClickListener {
            exerciseInfoDialog()
            trainingViewModel.onEvent(TrainingEvent.OnAddButtonClick)
        }
        binding.doneButton.setOnClickListener {
            trainingViewModel.onEvent(TrainingEvent.OnDoneButtonClick)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = exerciseAdapter
        lifecycleScope.launch {
            trainingViewModel.uiEvent.collect { event ->
                when(event) {
                    is UIEvent.ItemInserted -> {
                        exerciseAdapter.notifyItemInserted(event.position)
                    }
                    is UIEvent.CloseActivity -> {
                        finish()
                    }
                    else -> Unit
                }
            }
        }
    }
    private fun exerciseInfoDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.add_exercise_dialog_layout, null)
        val dialog = AlertDialog.Builder(this)
        dialog.setView(view)
        dialog.setPositiveButton("Ok") {
                dialog, _ ->
            val title = view.findViewById<EditText>(R.id.title_edittext).text.toString()
            val exercise = Exercise(description = title, duration = 10, trainingId = 0)
            exercises.add(exercise)
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
        binding.doneButton.visibility = if (exercises.size > 0) View.VISIBLE else View.GONE
    }
}