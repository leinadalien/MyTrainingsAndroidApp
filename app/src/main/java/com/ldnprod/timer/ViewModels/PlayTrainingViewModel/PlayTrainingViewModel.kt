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
    var currentExercise = MutableLiveData<Exercise>(null)
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
                    remainingExercises = exerciseRepository.getAllInTrainingByOrder(it.id) as ArrayList<Exercise>
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
            is PlayTrainingEvent.OnGoNextExercise -> {
                remainingExercises.removeAt(0)
                sendEvent(PlayTrainingViewModelEvent.ExerciseDeleted(0))
            }
            is PlayTrainingEvent.OnTrainingEnded -> {
                viewModelScope.launch(Dispatchers.IO) {
                    remainingExercises = exerciseRepository.getAllInTrainingByOrder(training.value!!.id) as ArrayList<Exercise>
                    currentExercise.postValue(remainingExercises.removeAt(0))
                    sendEvent(PlayTrainingViewModelEvent.TrainingLoaded)
                }
            }
        }
    }
    private fun sendEvent(event: PlayTrainingViewModelEvent) {
        viewModelScope.launch {
            _viewModelEvent.send(event)
        }
    }
}