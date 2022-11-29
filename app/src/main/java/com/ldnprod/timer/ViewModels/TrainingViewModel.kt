package com.ldnprod.timer.ViewModels

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val exerciseRepository: IExerciseRepository,
    private val trainingRepository: ITrainingRepository,
    savedStateHandle: SavedStateHandle
): ViewModel(){
    var training = MutableLiveData<Training?>(null)
        private set
    var title = MutableLiveData<String>("")
        private set
    var exercises = training.value?.let { exerciseRepository.getAllInTraining(it) } ?: ArrayList()
    private val _uiEvent = Channel<UIEvent> {  }
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        val trainingId = savedStateHandle.get<Int>("trainingId")!!
        if (trainingId != -1) {
            viewModelScope.launch {
                trainingRepository.getTrainingWithId(trainingId)?.let {
                    title.value = it.title
                    training.value = it
                    exercises = trainingRepository.getAllTrainingsWithExercises()[it]!!
                }

            }
        }
    }
    fun onEvent(event: TrainingEvent) {
        when(event) {
            is TrainingEvent.OnTitleChanged -> {
                title.value = event.title
            }
            is TrainingEvent.OnExerciseClick -> {
            }
            is TrainingEvent.OnAddButtonClick -> {
            }
            is TrainingEvent.OnDoneButtonClick -> {
                viewModelScope.launch {
                    if(!title.value.isNullOrBlank()) {
                        training.value?.let {
                            trainingRepository.insert(
                                Training(
                                    title = title.value!!,
                                    id = it.id
                                )
                            )
                            for (order in exercises.indices) {
                                if (order > 0) {
                                    exercises[order].previousExerciseId = exercises[order - 1].id
                                }
                                if (order < exercises.size - 1) {
                                    exercises[order].nextExerciseId = exercises[order + 1].id
                                }
                                exercises[order].trainingId = it.id
                                exerciseRepository.insert(exercises[order])
                            }
                        }
                    }
                }
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
            _uiEvent.send(event)
        }
    }
}