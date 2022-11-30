package com.ldnprod.timer.Dao

import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Entities.Training
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(training: Training)

    @Delete
    suspend fun delete(training: Training)

    @Update
    suspend fun update(training: Training)

    @Query("SELECT * FROM trainings")
    fun getAll(): List<Training>

    @Query("SELECT * FROM trainings JOIN exercises ON trainings.id = training_id")
    fun getTrainingsWithExercises(): Map<Training, List<Exercise>>

    @Query("SELECT * FROM trainings WHERE id =:id")
    fun getTrainingWithId(id: Int): Training?
}