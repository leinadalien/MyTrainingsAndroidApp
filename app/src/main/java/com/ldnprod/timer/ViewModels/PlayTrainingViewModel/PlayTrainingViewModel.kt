package com.ldnprod.timer.ViewModels.PlayTrainingViewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Interfaces.IExerciseRepository
import com.ldnprod.timer.Interfaces.ITrainingRepository
import com.ldnprod.timer.Utils.PlayTrainingEvent
import com.ldnprod.timer.ViewModels.TrainingViewModel.TrainingViewModelEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayTrainingViewModel @Inject constructor(
    private val exerciseRepository: IExerciseRepository,
    private val trainingRepository: ITrainingRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    var exercises = ArrayList<Exercise>()
        private set
    var title = ""
        private set
    var remainingTime = 600
        private set
    var currentExercise = ""
        private set


    private val _viewModelEvent = Channel<PlayTrainingViewModelEvent> { }
    val viewModelEvent = _viewModelEvent.receiveAsFlow()

    init {
        val trainingId = savedStateHandle.get<Int>("trainingId") ?: -1
        if (trainingId != -1) {
            viewModelScope.launch(Dispatchers.IO) {
                trainingRepository.getTrainingWithId(trainingId)?.let { it ->
                    title = it.title
                    val receivedExercises = exerciseRepository.getAllInTraining(it) as ArrayList<Exercise>
                    var prevId: Int? = null
                    while(receivedExercises.isNotEmpty()) {
                        val ex = receivedExercises.find { ex -> ex.previousExerciseId == prevId }
                        ex?.let { exercise ->
                            prevId?.let {
                                exercises.add(exercise)
                            }?: run {
                                currentExercise = exercise.description
                                remainingTime = exercise.duration
                            }
                            prevId = exercise.id
                            receivedExercises.remove(ex)
                        }

                    }

                    sendEvent(PlayTrainingViewModelEvent.TrainingLoaded)
                }
            }
        }
    }

    fun onEvent(event: PlayTrainingEvent) {
        when(event){
            is PlayTrainingEvent.OnSkipClicked -> {
                exercises.removeAt(event.position)
                sendEvent(PlayTrainingViewModelEvent.ExerciseDeleted(event.position))
            }
        }
    }
    private fun sendEvent(event: PlayTrainingViewModelEvent) {
        viewModelScope.launch {
            _viewModelEvent.send(event)
        }
    }
}