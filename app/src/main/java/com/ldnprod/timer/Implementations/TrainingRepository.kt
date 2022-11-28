package com.ldnprod.timer.Implementations

import androidx.lifecycle.LiveData
import com.ldnprod.timer.Dao.TrainingDao
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.Interfaces.ITrainingRepository

class TrainingRepository(private val dao:TrainingDao): ITrainingRepository {
    override suspend fun insert(training: Training) {
        dao.insert(training)
    }

    override suspend fun delete(training: Training) {
        dao.delete(training)
    }

    override suspend fun update(training: Training) {
        dao.update(training)
    }

    override fun getAll(): LiveData<List<Training>> {
        return dao.getAll()
    }

    override fun getAllTrainingsWithExercises(): LiveData<Map<Training, List<Exercise>>> {
        return dao.getTrainingsWithExercises()
    }

}