package com.ldnprod.timer.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.Interfaces.IExerciseRepository
import com.ldnprod.timer.Interfaces.ITrainingRepository
import com.ldnprod.timer.Utils.TrainingEvent
import com.ldnprod.timer.Utils.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val exerciseRepository: IExerciseRepository,
    private val trainingRepository: ITrainingRepository,
): ViewModel(){
    fun getTraining(id: Int) = trainingRepository.getTrainingWithId(id)
    fun getExercisesInTraining(training: Training) = exerciseRepository.getAllInTraining(training)
    fun insertExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseRepository.insert(exercise)
        }
    }
    private val _UIEvent = Channel<UIEvent> {  }

    fun onEvent(event: TrainingEvent) {
        when(event) {
            is TrainingEvent.OnExerciseClick -> {
            }
            is TrainingEvent.OnAddButtonClick -> {
            }
            is TrainingEvent.OnDoneButtonClick -> {
            }
            is TrainingEvent.OnDeleteExerciseClick -> {
                viewModelScope.launch {
                    exerciseRepository.delete(event.exercise)
                }
            }
        }
    }

    private fun sendUIEvent(event: UIEvent) {
        viewModelScope.launch {
            _UIEvent.send(event)
        }
    }
}