package com.ldnprod.timer.ViewModels.TrainingViewModel

import androidx.lifecycle.*
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.Interfaces.IExerciseRepository
import com.ldnprod.timer.Interfaces.ITrainingRepository
import com.ldnprod.timer.Utils.TrainingEvent
import com.ldnprod.timer.Utils.TrainingState
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
    var title = ""
        private set
    var exercises = ArrayList<Exercise>()
        private set
    private var prevState: TrainingState? = null
    private val lazyDeletedExercises = ArrayList<Exercise>()
    fun addExercise(exercise: Exercise) {
        exercises.add(exercise)
        sendEvent(TrainingViewModelEvent.ExerciseInserted(exercises.size, exercise))
        updateState()
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
                    exercises = exerciseRepository.getAllInTraining(it) as ArrayList<Exercise>
                    prevState = TrainingState(title, exercises)
                    sendEvent(TrainingViewModelEvent.TrainingLoaded)
                }
            }
        }
    }

    fun onEvent(event: TrainingEvent) {
        when (event) {
            is TrainingEvent.OnTitleChanged -> {
                title = event.title
                updateState()
            }
            is TrainingEvent.OnExerciseClick -> {
                sendEvent(TrainingViewModelEvent.ExerciseOpened(event.exercise, event.position))
            }
            is TrainingEvent.OnAddButtonClick -> {
                sendEvent(TrainingViewModelEvent.ExerciseCreated)
            }
            is TrainingEvent.OnDoneButtonClick -> {
                viewModelScope.launch(Dispatchers.IO) {
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
                                lazyDeletedExercises.forEach {
                                    ex -> exerciseRepository.delete(ex)
                                }
                                lazyDeletedExercises.clear()
                                sendEvent(TrainingViewModelEvent.TrainingClosed(it))
                            }
                        }
                    }
                }
            }
            is TrainingEvent.OnDeleteExerciseClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    exercises.removeAt(event.position)
                    if (!lazyDeletedExercises.contains(event.exercise)) {
                        lazyDeletedExercises.add(event.exercise)
                    }
                    sendEvent(TrainingViewModelEvent.ExerciseRemoved(event.position))
                    updateState()
                }
            }
            is TrainingEvent.OnExerciseChanged -> {
                exercises[event.position] = event.exercise
                sendEvent(TrainingViewModelEvent.ExerciseChanged(event.position))
                updateState()
            }
            else -> Unit
        }
    }

    private fun updateState() {
        if (exercises.isNotEmpty()) {
            sendEvent(TrainingViewModelEvent.TrainingStateChanged(
                prevState?.let {
                    it == TrainingState(title, exercises)
                } ?: true)
            )
        }
    }

    private fun sendEvent(event: TrainingViewModelEvent) {
        viewModelScope.launch {
            _viewModelEvent.send(event)
        }
    }
}