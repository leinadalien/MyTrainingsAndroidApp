package com.ldnprod.mytrainings.Interfaces

import com.ldnprod.mytrainings.Entities.Exercise
import com.ldnprod.mytrainings.Entities.Training

interface IExerciseRepository {
    suspend fun insert(exercise: Exercise) : Long

    suspend fun delete(exercise: Exercise)

    suspend fun update(exercise: Exercise)

    fun getAll(): List<Exercise>

    fun getAllInTraining(training: Training): List<Exercise>

    fun getAllInTrainingByOrder(trainingId: Int): List<Exercise>

    fun getById(id: Int): Exercise
}