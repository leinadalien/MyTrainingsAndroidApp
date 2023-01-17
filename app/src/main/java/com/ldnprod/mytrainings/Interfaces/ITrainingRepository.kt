package com.ldnprod.mytrainings.Interfaces


import com.ldnprod.mytrainings.Entities.Exercise
import com.ldnprod.mytrainings.Entities.Training

interface ITrainingRepository {

    suspend fun insert(training: Training) : Long

    suspend fun delete(training: Training)

    suspend fun update(training: Training)

    fun getAll(): List<Training>

    fun getAllTrainingsWithExercises(): Map<Training, List<Exercise>>

    fun getTrainingWithId(id: Int): Training?
}