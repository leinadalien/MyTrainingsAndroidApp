package com.ldnprod.timer.Interfaces

import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Entities.Training

interface IExerciseRepository {
    fun insert(exercise: Exercise)

    fun delete(exercise: Exercise)

    fun update(exercise: Exercise)

    fun getAll(): List<Exercise>

    fun getAllInTraining(training: Training): List<Exercise>
}