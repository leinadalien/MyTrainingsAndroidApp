package com.ldnprod.timer.Interfaces


import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Entities.Training

interface ITrainingRepository {

    fun insert(training: Training)

    fun delete(training: Training)

    fun update(training: Training)

    fun getAll(): List<Training>

    fun getAllTrainingsWithExercises(): Map<Training, List<Exercise>>
}