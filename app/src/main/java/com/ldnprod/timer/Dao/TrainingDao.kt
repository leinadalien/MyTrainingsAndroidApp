package com.ldnprod.timer.Dao

import androidx.room.*
import com.ldnprod.timer.Entities.Training

@Dao
interface TrainingDao {
    @Insert
    fun insert(training: Training)

    @Delete
    fun delete(training: Training)

    @Update
    fun update(training: Training)

    @Query("SELECT * FROM trainings")
    fun getAll(): List<Training>
}