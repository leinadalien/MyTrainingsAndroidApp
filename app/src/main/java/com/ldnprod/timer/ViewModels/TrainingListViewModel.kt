package com.ldnprod.timer.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.Interfaces.ITrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
}