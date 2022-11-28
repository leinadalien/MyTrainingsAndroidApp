package com.ldnprod.timer.ViewModels

import androidx.lifecycle.ViewModel
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.Interfaces.IExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExerciseListViewModel @Inject constructor(
    private val repository: IExerciseRepository
): ViewModel(){
    fun getExercisesInTraining(training: Training) = repository.getAllInTraining(training)
}