package com.ldnprod.timer.ViewModels

import androidx.lifecycle.ViewModel
import com.ldnprod.timer.Interfaces.ITrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrainingListViewModel @Inject constructor(
    private val repository: ITrainingRepository
): ViewModel() {
    val trainings = repository.getAll()
}