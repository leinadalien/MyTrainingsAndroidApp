package com.ldnprod.timer.Implementations

import com.ldnprod.timer.Dao.ExerciseDao
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.Interfaces.IExerciseRepository

class ExerciseRepository(
    private val dao:ExerciseDao
) : IExerciseRepository {

    override fun insert(exercise: Exercise) {
        dao.insert(exercise)
    }

    override fun delete(exercise: Exercise) {
        dao.delete(exercise)
    }

    override fun update(exercise: Exercise) {
        dao.update(exercise)
    }

    override fun getAll(): List<Exercise> {
        return dao.getAll()
    }

    override fun getAllInTraining(training: Training): List<Exercise> {
        return dao.getAllInTraining(training.id)
    }
}