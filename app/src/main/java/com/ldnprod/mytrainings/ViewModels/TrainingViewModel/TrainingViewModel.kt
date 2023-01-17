package com.ldnprod.mytrainings.ViewModels.TrainingViewModel

import androidx.lifecycle.*
import com.ldnprod.mytrainings.Entities.Exercise
import com.ldnprod.mytrainings.Entities.Training
import com.ldnprod.mytrainings.Interfaces.IExerciseRepository
import com.ldnprod.mytrainings.Interfaces.ITrainingRepository
import com.ldnprod.mytrainings.Utils.TrainingEvent
import com.ldnprod.mytrainings.Utils.TrainingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Collections
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
                trainingRepository.getTrainingWithId(trainingId)?.let { it ->
                    title = it.title
                    training = it
                    val receivedExercises = exerciseRepository.getAllInTraining(it) as ArrayList<Exercise>
                    var prevId: Int? = null
                    while(receivedExercises.isNotEmpty()) {
                        val ex = receivedExercises.find { ex -> ex.previousExerciseId == prevId }
                        prevId = ex?.id
                        exercises.add(ex!!)
                        receivedExercises.remove(ex)
                    }
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
                                var prevId: Int? = null
                                exercises.forEach { ex ->
                                    ex.previousExerciseId = prevId
                                    ex.trainingId = trainingId
                                    prevId = exerciseRepository.insert(ex).toInt()
                                }
                                lazyDeletedExercises.forEach {
                                    ex -> exerciseRepository.delete(ex)
                                }
                                lazyDeletedExercises.clear()
                                sendEvent(TrainingViewModelEvent.TrainingSaved(it))
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
            is TrainingEvent.OnExerciseMoved -> {
                Collections.swap(exercises, event.startPosition, event.endPosition)
                sendEvent(TrainingViewModelEvent.ExerciseMoved(event.startPosition, event.endPosition))
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
                } ?: false)
            )
        }
    }

    private fun sendEvent(event: TrainingViewModelEvent) {
        viewModelScope.launch {
            _viewModelEvent.send(event)
        }
    }
}