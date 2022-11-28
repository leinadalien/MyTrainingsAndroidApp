package com.ldnprod.timer.Interfaces


import androidx.lifecycle.LiveData
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Entities.Training

interface ITrainingRepository {

    suspend fun insert(training: Training)

    suspend fun delete(training: Training)

    suspend fun update(training: Training)

    suspend fun getAll():LiveData<List<Training>>

    suspend fun getAllTrainingsWithExercises(): LiveData<Map<Training, List<Exercise>>>
}