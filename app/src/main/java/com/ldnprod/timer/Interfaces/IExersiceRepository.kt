package com.ldnprod.timer.Interfaces

import com.ldnprod.timer.Dao.TrainingWithExercise
import com.ldnprod.timer.Entities.Exercise

interface IExerciseRepository {
    fun insert(exercise: Exercise)

    fun delete(exercise: Exercise)

    fun update(exercise: Exercise)

    fun getAll(): List<Exercise>

    fun getAllExercisesInTraining(trainingId: Int): List<Exercise>

    fun getExerciseWithTraining(): List<TrainingWithExercise>
}