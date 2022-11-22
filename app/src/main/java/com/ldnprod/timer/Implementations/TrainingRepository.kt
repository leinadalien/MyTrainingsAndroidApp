package com.ldnprod.timer.Implementations

import com.ldnprod.timer.Dao.TrainingDao
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.Interfaces.ITrainingRepository

class TrainingRepository(private val dao:TrainingDao): ITrainingRepository {
    override fun insert(training: Training) {
        dao.insert(training)
    }

    override fun delete(training: Training) {
        dao.delete(training)
    }

    override fun update(training: Training) {
        dao.update(training)
    }

    override fun getAll(): List<Training> {
        return dao.getAll()
    }

}