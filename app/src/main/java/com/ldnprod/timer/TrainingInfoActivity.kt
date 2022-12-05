package com.ldnprod.timer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ldnprod.timer.Adapters.ExerciseAdapter
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Utils.TrainingEvent
import com.ldnprod.timer.ViewModels.TrainingViewModel.TrainingViewModel
import com.ldnprod.timer.ViewModels.TrainingViewModel.TrainingViewModelEvent
import com.ldnprod.timer.databinding.ActivityTrainingInfoBinding
import com.ldnprod.timer.databinding.ExerciseInfoDialogLayoutBinding
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
                ItemTouchHelper(simpleCallback).attachToRecyclerView(this)
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
                    is TrainingViewModelEvent.ExerciseOpened -> {
                        showExerciseInfoDialog(event.exercise, event.position)
                    }
                    is TrainingViewModelEvent.ExerciseChanged -> {
                        exerciseAdapter.notifyItemChanged(event.position)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showExerciseInfoDialog(exercise: Exercise? = null, position: Int? = null) {
        val dialogBinding = ExerciseInfoDialogLayoutBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this)
        dialogBinding.apply {
            initializeDialog(this, exercise)

        }
        dialog.setView(dialogBinding.root)
        dialog.setPositiveButton("Ok") { dlg, _ ->
            dialogBinding.apply {
                exerciseDescriptionEdittext.text.toString().let { description ->
                    if (description.isNotBlank()) {
                        exercise?.let {
                            it.description = description
                            it.duration = minutePicker.value * 60 + secondPicker.value
                            viewModel.onEvent(TrainingEvent.OnExerciseChanged(it, position!!))
                        } ?: run {
                            viewModel.addExercise(
                                Exercise(
                                    description = description,
                                    duration = minutePicker.value * 60 + secondPicker.value,
                                    trainingId = 0
                                )
                            )
                        }
                        Toast.makeText(this@TrainingInfoActivity, "Successfully", Toast.LENGTH_SHORT).show()
                        dlg.dismiss()
                    } else {
                        Toast.makeText(this@TrainingInfoActivity, "Description can't be empty", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

        }
        dialog.setNegativeButton("Cancel") { dlg, _ ->
            Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
            dlg.dismiss()
        }
        dialog.create()
        dialog.show()
    }
    private fun initializePicker(picker: NumberPicker, max: Int) {
        picker.apply {
            minValue = 0
            maxValue = max
            setFormatter { value -> String.format("%02d", value) }

            value = 0
            getChildAt(value).visibility = View.INVISIBLE
        }
    }
    private fun initializeDialog(dialogLayoutBinding: ExerciseInfoDialogLayoutBinding, exercise: Exercise?) {
        dialogLayoutBinding.apply {
            initializePicker(minutePicker, 9)
            initializePicker(secondPicker, 59)
            secondPicker.value = 1

            minutePicker.setOnValueChangedListener { picker, oldVal, newVal ->
                if (newVal == 0) {
                    secondPicker.minValue = 1
                } else if (oldVal == 0) {
                    secondPicker.minValue = 0
                }
            }
            secondPicker.setOnValueChangedListener { picker, oldVal, newVal ->
                if (newVal == 0) {
                    minutePicker.apply {
                        minValue = 1
                        maxValue = 10
                    }
                } else if (oldVal == 0) {
                    minutePicker.apply {
                        minValue = 0
                        maxValue = 9
                    }
                }
            }
            exercise?.let {
                exerciseDescriptionEdittext.setText(it.description)
                if (it.duration == 600) {
                    minutePicker.minValue = 1
                    minutePicker.maxValue = 10
                    secondPicker.minValue = 0
                    secondPicker.maxValue = 59
                }
                minutePicker.value = it.duration / 60
                secondPicker.value = it.duration % 60
            }
        }
    }
    private val simpleCallback = object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), 0) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val startPosition = viewHolder.adapterPosition
            val endPosition = target.adapterPosition
            viewModel.onEvent(TrainingEvent.OnExerciseMoved(startPosition, endPosition))
            return true
        }
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        }
    }
}