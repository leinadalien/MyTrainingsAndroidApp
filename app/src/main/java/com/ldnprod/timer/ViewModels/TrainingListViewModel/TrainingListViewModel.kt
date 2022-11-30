package com.ldnprod.timer.ViewModels.TrainingListViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.Interfaces.ITrainingRepository
import com.ldnprod.timer.Utils.TrainingListEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingListViewModel @Inject constructor(
    private val repository: ITrainingRepository
): ViewModel() {
    var trainings = ArrayList<Training>()
        private set

    private val _viewModelEvent = Channel<TrainingListViewModelEvent> {  }
    val viewModelEvent = _viewModelEvent.receiveAsFlow()
    init {
        viewModelScope.launch(Dispatchers.IO) {
            trainings = repository.getAll() as ArrayList<Training>
            sendEventToUI(TrainingListViewModelEvent.TrainingSetChanged(trainings))
        }

    }
    fun onEvent(event: TrainingListEvent) {
        when(event){
            is TrainingListEvent.OnTrainingClick -> {
                sendEventToUI(TrainingListViewModelEvent.JumpToDetail)
            }
            is TrainingListEvent.OnRequestUpdatesForList -> {
                viewModelScope.launch(Dispatchers.IO) {
                    trainings.let{
                        val newTrainings = repository.getAll()
                        if (newTrainings.size > it.size) {
                            for (order in newTrainings.indices) {
                                if (order > it.size - 1 || it[order].id != newTrainings[order].id) {
                                    it.add(newTrainings[order])
                                    sendEventToUI(TrainingListViewModelEvent.TrainingInserted(order))
                                }
                            }
                        }
                    }
                }
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