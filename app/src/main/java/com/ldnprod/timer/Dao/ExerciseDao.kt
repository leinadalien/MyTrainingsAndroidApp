package com.ldnprod.timer.Dao
import androidx.room.*
import com.ldnprod.timer.Entities.Exercise

@Dao
interface ExerciseDao {
    @Insert
    fun insert(exercise: Exercise)

    @Delete
    fun delete(exercise: Exercise)

    @Update
    fun update(exercise: Exercise)

    @Query("SELECT * FROM exercises")
    fun getAll(): List<Exercise>

    @Query("SELECT * FROM exercises WHERE training_id =:trainingId")
    fun getAllExercisesInTraining(trainingId: Int): List<Exercise>

    @Transaction
    @Query("SELECT * FROM trainings")
    fun getTrainingWithExercises(): List<TrainingWithExercise>
}