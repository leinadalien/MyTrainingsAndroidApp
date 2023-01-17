package com.ldnprod.mytrainings.Dao
import androidx.room.*
import com.ldnprod.mytrainings.Entities.Exercise

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: Exercise) : Long

    @Delete
    suspend fun delete(exercise: Exercise)

    @Update
    suspend fun update(exercise: Exercise)

    @Query("SELECT * FROM exercises")
    fun getAll(): List<Exercise>

    @Query("SELECT * FROM exercises WHERE training_id =:trainingId")
    fun getAllInTraining(trainingId: Int): List<Exercise>

    @Query("SELECT * FROM exercises WHERE id =:id")
    fun getById(id: Int): Exercise
}