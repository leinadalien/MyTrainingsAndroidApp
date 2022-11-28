package com.ldnprod.timer.Interfaces

import androidx.lifecycle.LiveData
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Entities.Training

interface IExerciseRepository {
    suspend fun insert(exercise: Exercise)

    suspend fun delete(exercise: Exercise)

    suspend fun update(exercise: Exercise)

    suspend fun getAll(): LiveData<List<Exercise>>

    suspend fun getAllInTraining(training: Training): LiveData<List<Exercise>>
}