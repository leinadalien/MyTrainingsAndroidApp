package com.ldnprod.timer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ldnprod.timer.Adapters.ExerciseAdapter
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Utils.TrainingEvent
import com.ldnprod.timer.ViewModels.TrainingViewModel.TrainingViewModel
import com.ldnprod.timer.ViewModels.TrainingViewModel.TrainingViewModelEvent
import com.ldnprod.timer.databinding.ActivityTrainingInfoBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TrainingInfoActivity : AppCompatActivity() {
    private val viewModel by viewModels<TrainingViewModel>()
    private lateinit var binding: ActivityTrainingInfoBinding
    private lateinit var exerciseAdapter: ExerciseAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        exerciseAdapter = ExerciseAdapter(viewModel.exercises) { viewModel.onEvent(it) }
        binding.apply {
            createButton
                .setOnClickListener {
                    viewModel.onEvent(TrainingEvent.OnAddButtonClick)
            }
            doneButton
                .setOnClickListener {
                viewModel.onEvent(TrainingEvent.OnDoneButtonClick)
            }
            trainingTitleEdittext.apply {
                doOnTextChanged { _, _, _, _ ->
                    viewModel.onEvent(TrainingEvent.OnTitleChanged(trainingTitleEdittext.text.toString()))
                }
            }
            recyclerView.apply {
                layoutManager = LinearLayoutManager(this@TrainingInfoActivity)
                adapter = exerciseAdapter
            }
        }

        lifecycleScope.launch {
            viewModel.viewModelEvent.collect { event ->
                when (event) {
                    is TrainingViewModelEvent.ExerciseInserted -> {
                        exerciseAdapter.notifyItemInserted(event.position)
                    }
                    is TrainingViewModelEvent.ExerciseMoved -> {
                        exerciseAdapter.notifyItemMoved(event.fromPosition, event.toPosition)
                    }
                    is TrainingViewModelEvent.ExerciseRemoved -> {
                        exerciseAdapter.notifyItemRemoved(event.position)
                    }
                    is TrainingViewModelEvent.TrainingClosed -> {
                        finish()
                    }
                    is TrainingViewModelEvent.ExerciseCreated -> {
                        showExerciseInfoDialog()
                    }
                    is TrainingViewModelEvent.TrainingLoaded -> {
                        binding.apply {
                            trainingTitleEdittext.setText(viewModel.title)
                        }
                        exerciseAdapter.exercises = viewModel.exercises
                        exerciseAdapter.notifyDataSetChanged()
                    }
                    is TrainingViewModelEvent.TrainingStateChanged -> {
                        binding.doneButton.visibility =
                            if (event.likePrevious) View.GONE else View.VISIBLE
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
        dialog.setPositiveButton("Ok") { dialog, _ ->
            val title = view.findViewById<EditText>(R.id.title_edittext).text.toString()
            val exercise = Exercise(description = title, duration = 10, trainingId = 0)
            viewModel.addExercise(exercise)
            Toast.makeText(this, "Successfully", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.setNegativeButton("Cancel") { dialog, _ ->
            Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
            dialog.dismiss()

        }
        dialog.create()
        dialog.show()
    }
}