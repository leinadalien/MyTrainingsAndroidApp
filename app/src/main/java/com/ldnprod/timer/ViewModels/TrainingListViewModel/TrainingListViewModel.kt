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
    private val trainingRepository: ITrainingRepository,
) : ViewModel() {

    var trainings = ArrayList<Training>()
        private set
    private val _viewModelEvent = Channel<TrainingListViewModelEvent> { }
    val viewModelEvent = _viewModelEvent.receiveAsFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            trainings = trainingRepository.getAll() as ArrayList<Training>
            sendEvent(TrainingListViewModelEvent.TrainingSetChanged(trainings))
        }

    }

    fun onEvent(event: TrainingListEvent) {
        when (event) {
            is TrainingListEvent.OnRequestUpdatesForList -> {
                viewModelScope.launch(Dispatchers.IO) {
                    trainings.let {
                        val newTrainings = trainingRepository.getAll()
                        for (order in newTrainings.indices) {
                            if (order > it.size - 1) {
                                it.add(newTrainings[order])
                                sendEvent(TrainingListViewModelEvent.TrainingInserted(order))
                            } else if (it[order] != newTrainings[order]) {
                                if (it[order].id != newTrainings[order].id) {
                                    it.add(newTrainings[order])
                                    sendEvent(TrainingListViewModelEvent.TrainingInserted(order))
                                } else {
                                    it[order] = newTrainings[order]
                                    sendEvent(TrainingListViewModelEvent.TrainingChanged(order))
                                }
                            }
                        }
                    }
                }
            }
            is TrainingListEvent.OnDeleteTrainingClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    trainings.removeAt(event.position)
                    trainingRepository.delete(event.training)
                    sendEvent(TrainingListViewModelEvent.TrainingRemoved(event.position))
                }
            }
            is TrainingListEvent.OnTrainingClick -> {
                sendEvent(TrainingListViewModelEvent.TrainingOpened(event.training))
            }
            is TrainingListEvent.OnEditTrainingClick -> {
                sendEvent(TrainingListViewModelEvent.TrainingInfoOpened(event.training))
            }
            else -> Unit
        }
    }

    private fun sendEvent(event: TrainingListViewModelEvent) {
        viewModelScope.launch {
            _viewModelEvent.send(event)
        }
    }
}