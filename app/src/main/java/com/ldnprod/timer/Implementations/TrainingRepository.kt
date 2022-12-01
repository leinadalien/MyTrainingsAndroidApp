package com.ldnprod.timer.Implementations

import com.ldnprod.timer.Dao.TrainingDao
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.Interfaces.ITrainingRepository
import kotlinx.coroutines.flow.Flow

class TrainingRepository(private val dao:TrainingDao): ITrainingRepository {
    override suspend fun insert(training: Training): Long {
        return dao.insert(training)
    }

    override suspend fun delete(training: Training) {
        dao.deleteAndClean(training)
    }

    override suspend fun update(training: Training) {
        dao.update(training)
    }

    override fun getAll(): List<Training> {
        return dao.getAll()
    }

    override fun getAllTrainingsWithExercises(): Map<Training, List<Exercise>> {
        return dao.getTrainingsWithExercises()
    }

    override fun getTrainingWithId(id: Int): Training? {
        return dao.getTrainingWithId(id)
    }

}