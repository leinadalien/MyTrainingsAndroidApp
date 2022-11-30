package com.ldnprod.timer.Interfaces


import androidx.lifecycle.LiveData
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Entities.Training
import kotlinx.coroutines.flow.Flow

interface ITrainingRepository {

    suspend fun insert(training: Training) : Long

    suspend fun delete(training: Training)

    suspend fun update(training: Training)

    fun getAll(): List<Training>

    fun getAllTrainingsWithExercises(): Map<Training, List<Exercise>>

    fun getTrainingWithId(id: Int): Training?
}