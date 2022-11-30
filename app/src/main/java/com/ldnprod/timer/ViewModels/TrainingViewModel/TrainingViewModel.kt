package com.ldnprod.timer.ViewModels.TrainingViewModel

import androidx.lifecycle.*
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.Interfaces.IExerciseRepository
import com.ldnprod.timer.Interfaces.ITrainingRepository
import com.ldnprod.timer.Utils.TrainingEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val exerciseRepository: IExerciseRepository,
    private val trainingRepository: ITrainingRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var training: Training? = null
        private set
    var title: String = ""
        private set
    var exercises = ArrayList<Exercise>()
        private set

    fun addExercise(exercise: Exercise) {
        exercises.add(exercise)
        sendEventToUI(TrainingViewModelEvent.ExerciseInserted(exercises.size, exercise))
    }

    private val _viewModelEvent = Channel<TrainingViewModelEvent> { }
    val viewModelEvent = _viewModelEvent.receiveAsFlow()

    init {
        val trainingId = savedStateHandle.get<Int>("trainingId") ?: -1
        if (trainingId != -1) {
            viewModelScope.launch(Dispatchers.IO) {
                trainingRepository.getTrainingWithId(trainingId)?.let {
                    title = it.title
                    training = it
                }
                exercises = exerciseRepository.getAllInTraining(training!!) as ArrayList<Exercise>
                sendEventToUI(TrainingViewModelEvent.ExerciseSetChanged)
            }
        }
    }

    fun onEvent(event: TrainingEvent) {
        when (event) {
            is TrainingEvent.OnTitleChanged -> {
                title = event.title
            }
            is TrainingEvent.OnExerciseClick -> {
            }
            is TrainingEvent.OnAddButtonClick -> {
                sendEventToUI(TrainingViewModelEvent.CreateExercise)
            }
            is TrainingEvent.OnDoneButtonClick -> {
                viewModelScope.launch {
                    title.let { trainingTitle ->
                        if (trainingTitle.isNotBlank()) {
                            if (training == null) training = Training(title = trainingTitle)
                            training?.let {
                                it.title = trainingTitle
                                val trainingId = trainingRepository.insert(
                                    it
                                ).toInt()
                                for (order in exercises.indices) {
                                    if (order > 0) {
                                        exercises[order].previousExerciseId =
                                            exercises[order - 1].id
                                    }
                                    if (order < exercises.size - 1) {
                                        exercises[order].nextExerciseId = exercises[order + 1].id
                                    }
                                    exercises[order].trainingId = trainingId
                                    exerciseRepository.insert(exercises[order])
                                }
                                sendEventToUI(TrainingViewModelEvent.CloseDetailed(it))
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
            is TrainingEvent.OnTrainingRequested -> {
                savedStateHandle["trainingId"] = event.id
                viewModelScope.launch(Dispatchers.IO) {
                    trainingRepository.getTrainingWithId(event.id)?.let {
                        title = it.title
                        training = it

                    }
                    exercises = exerciseRepository.getAllInTraining(training!!) as ArrayList<Exercise>
                    sendEventToUI(TrainingViewModelEvent.ExerciseSetChanged)
                }
            }
            else -> Unit
        }
    }

    private fun sendEventToUI(event: TrainingViewModelEvent) {
        viewModelScope.launch {
            _viewModelEvent.send(event)
        }
    }
}