package com.ldnprod.mytrainings.Dao

import androidx.room.*
import com.ldnprod.mytrainings.Entities.Exercise
import com.ldnprod.mytrainings.Entities.Training

@Dao
interface TrainingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(training: Training) : Long

    @Delete
    suspend fun delete(training: Training)

    @Query("DELETE FROM exercises WHERE training_id = :trainingId")
    suspend fun clean(trainingId: Int)

    @Transaction
    suspend fun deleteAndClean(training: Training) {
        clean(training.id)
        delete(training)
    }

    @Update
    suspend fun update(training: Training)

    @Query("SELECT * FROM trainings")
    fun getAll(): List<Training>

    @Query("SELECT * FROM trainings JOIN exercises ON trainings.id = training_id")
    fun getTrainingsWithExercises(): Map<Training, List<Exercise>>

    @Query("SELECT * FROM trainings WHERE id =:id")
    fun getTrainingWithId(id: Int): Training?
}