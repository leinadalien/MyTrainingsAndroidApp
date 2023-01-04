package com.ldnprod.timer.Implementations

import androidx.lifecycle.LiveData
import com.ldnprod.timer.Dao.ExerciseDao
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.Interfaces.IExerciseRepository

class ExerciseRepository(
    private val dao:ExerciseDao
) : IExerciseRepository {

    override suspend fun insert(exercise: Exercise): Long {
        return dao.insert(exercise)
    }

    override suspend fun delete(exercise: Exercise) {
        dao.delete(exercise)
    }

    override suspend fun update(exercise: Exercise) {
        dao.update(exercise)
    }

    override fun getAll(): List<Exercise> {
        return dao.getAll()
    }

    override fun getAllInTraining(training: Training): List<Exercise> {
        return dao.getAllInTraining(training.id)
    }

    override fun getById(id: Int): Exercise {
        return dao.getById(id)
    }
}