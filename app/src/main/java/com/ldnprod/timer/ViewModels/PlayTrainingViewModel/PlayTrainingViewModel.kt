package com.ldnprod.timer.ViewModels.PlayTrainingViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.Interfaces.IExerciseRepository
import com.ldnprod.timer.Interfaces.ITrainingRepository
import com.ldnprod.timer.Utils.PlayTrainingEvent
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
    var currentExercise = MutableLiveData<Exercise>(null)
        private set
    private var exercises = ArrayList<Exercise>()
    var remainingExercises = ArrayList<Exercise>()
        private set
    var training = MutableLiveData<Training>(null)
        private set
    private val _viewModelEvent = Channel<PlayTrainingViewModelEvent> { }
    val viewModelEvent = _viewModelEvent.receiveAsFlow()

    init {
        val trainingId = savedStateHandle.get<Int>("trainingId") ?: -1
        if (trainingId != -1) {
            viewModelScope.launch(Dispatchers.IO) {
                trainingRepository.getTrainingWithId(trainingId)?.let { it ->
                    training.postValue(it)
                    exercises = exerciseRepository.getAllInTrainingByOrder(it.id) as ArrayList<Exercise>
                    remainingExercises.clear()
                    exercises.forEach {
                        remainingExercises.add(it)
                    }
                    currentExercise.postValue(remainingExercises.removeAt(0))
                    sendEvent(PlayTrainingViewModelEvent.TrainingLoaded)
                }
            }
        }
    }

    fun onEvent(event: PlayTrainingEvent) {
        when(event){
            is PlayTrainingEvent.OnSkipClicked -> {
                remainingExercises.removeAt(event.position)
                sendEvent(PlayTrainingViewModelEvent.ExerciseDeleted(event.position))
            }
            is PlayTrainingEvent.GoToExercise -> {
                if (currentExercise.value != exercises[event.position]) {
                    while (remainingExercises[0].id != exercises[event.position].id) {
                        remainingExercises.removeAt(0)
                        sendEvent(PlayTrainingViewModelEvent.ExerciseDeleted(0))
                    }
                    currentExercise.postValue(remainingExercises.removeAt(0))
                    sendEvent(PlayTrainingViewModelEvent.ExerciseDeleted(0))
                }
            }
            is PlayTrainingEvent.OnTrainingEnded -> {
                remainingExercises.clear()
                exercises.forEach {
                    remainingExercises.add(it)
                }
                currentExercise.postValue(remainingExercises.removeAt(0))
                sendEvent(PlayTrainingViewModelEvent.TrainingLoaded)
            }
        }
    }
    private fun sendEvent(event: PlayTrainingViewModelEvent) {
        viewModelScope.launch {
            _viewModelEvent.send(event)
        }
    }
}