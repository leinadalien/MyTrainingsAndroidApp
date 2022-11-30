package com.ldnprod.timer.ViewModels.TrainingListViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.Interfaces.ITrainingRepository
import com.ldnprod.timer.Utils.TrainingListEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingListViewModel @Inject constructor(
    private val repository: ITrainingRepository
): ViewModel() {
    lateinit var trainings: ArrayList<Training>
    fun addTraining(training: Training){
        viewModelScope.launch {
            repository.insert(training)
        }
    }

    private val _viewModelEvent = Channel<TrainingListViewModelEvent> {  }
    val viewModelEvent = _viewModelEvent.receiveAsFlow()
    init {
        trainings = ArrayList()
        viewModelScope.launch(Dispatchers.IO) {
            trainings = repository.getAll() as ArrayList<Training>
            sendEventToUI(TrainingListViewModelEvent.TrainingInserted(trainings.size))
        }

    }
    fun onEvent(event: TrainingListEvent) {
        when(event){
            is TrainingListEvent.OnTrainingClick ->{
                sendEventToUI(TrainingListViewModelEvent.JumpToDetail)
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