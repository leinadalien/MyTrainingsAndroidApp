package com.ldnprod.mytrainings.Implementations

import com.ldnprod.mytrainings.Dao.TrainingDao
import com.ldnprod.mytrainings.Entities.Exercise
import com.ldnprod.mytrainings.Entities.Training
import com.ldnprod.mytrainings.Interfaces.ITrainingRepository

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