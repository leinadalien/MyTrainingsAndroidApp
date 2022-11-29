package com.ldnprod.timer.ViewModels.TrainingListViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.Interfaces.ITrainingRepository
import com.ldnprod.timer.Utils.TrainingListEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingListViewModel @Inject constructor(
    private val repository: ITrainingRepository
): ViewModel() {
    val trainings = viewModelScope.launch { repository.getAll() }
    fun addTraining(training: Training){
        viewModelScope.launch {
            repository.insert(training)
        }
    }

    private val _viewModelEvent = Channel<TrainingListViewModelEvent> {  }
    val viewModelEvent = _viewModelEvent.receiveAsFlow()

    fun onEvent(event: TrainingListEvent) {
        when(event){
            is TrainingListEvent.OnTrainingClick ->{

            }
            else -> Unit
        }
    }
    private fun sendEventToUI(event: TrainingListViewModelEvent) {
        viewModelScope.launch {
            _viewModelEvent.send(event)
        }
    }
}