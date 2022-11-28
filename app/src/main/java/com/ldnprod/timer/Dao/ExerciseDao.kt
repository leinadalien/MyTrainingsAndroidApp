package com.ldnprod.timer.Dao
import androidx.lifecycle.LiveData
import androidx.room.*
import com.ldnprod.timer.Entities.Exercise

@Dao
interface ExerciseDao {
    @Insert
    suspend fun insert(exercise: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)

    @Update
    suspend fun update(exercise: Exercise)

    @Query("SELECT * FROM exercises")
    suspend fun getAll(): LiveData<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE training_id =:trainingId")
    suspend fun getAllInTraining(trainingId: Int): LiveData<List<Exercise>>
}