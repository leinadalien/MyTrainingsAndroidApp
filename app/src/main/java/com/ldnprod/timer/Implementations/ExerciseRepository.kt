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

    override fun getAllInTrainingByOrder(trainingId: Int): List<Exercise> {
        val receivedExercises = dao.getAllInTraining(trainingId) as ArrayList<Exercise>
        val result = ArrayList<Exercise>()
        var prevId: Int? = null
        while(receivedExercises.isNotEmpty()) {
            val ex = receivedExercises.find { ex -> ex.previousExerciseId == prevId }
            prevId = ex?.id
            result.add(ex!!)
            receivedExercises.remove(ex)
        }
        return result
    }

    override fun getById(id: Int): Exercise {
        return dao.getById(id)
    }
}