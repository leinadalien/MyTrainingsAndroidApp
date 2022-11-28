package com.ldnprod.timer.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Entities.Training

@Dao
interface TrainingDao {
    @Insert
    suspend fun insert(training: Training)

    @Delete
    suspend fun delete(training: Training)

    @Update
    suspend fun update(training: Training)

    @Query("SELECT * FROM trainings")
    suspend fun getAll(): LiveData<List<Training>>

    @Query(
        "SELECT * FROM trainings JOIN exercises ON trainings.id = training_id"
    )
    suspend fun getTrainingsWithExercises(): LiveData<Map<Training, List<Exercise>>>
}