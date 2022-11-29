package com.ldnprod.timer.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.Interfaces.ITrainingRepository
import com.ldnprod.timer.Utils.TrainingListEvent
import com.ldnprod.timer.Utils.UIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import javax.inject.Inject

@HiltViewModel
class TrainingListViewModel @Inject constructor(
    private val repository: ITrainingRepository
): ViewModel() {
    val trainings = repository.getAll()
    fun addTraining(training: Training){
        viewModelScope.launch {
            repository.insert(training)
        }
    }

    private val _uiEvent = Channel<UIEvent> {  }
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: TrainingListEvent) {
        when(event){
            is TrainingListEvent.OnTrainingClick ->{

            }
            else -> Unit
        }
    }
    private fun sendUIEvent(event: UIEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}