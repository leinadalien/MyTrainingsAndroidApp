package com.ldnprod.timer.Implementations

import androidx.lifecycle.LiveData
import com.ldnprod.timer.Dao.ExerciseDao
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.Interfaces.IExerciseRepository

class ExerciseRepository(
    private val dao:ExerciseDao
) : IExerciseRepository {

    override suspend fun insert(exercise: Exercise) {
        dao.insert(exercise)
    }

    override suspend fun delete(exercise: Exercise) {
        dao.delete(exercise)
    }

    override suspend fun update(exercise: Exercise) {
        dao.update(exercise)
    }

    override fun getAll(): LiveData<List<Exercise>> {
        return dao.getAll()
    }

    override fun getAllInTraining(training: Training): LiveData<List<Exercise>> {
        return dao.getAllInTraining(training.id)
    }
}