package com.ldnprod.timer.Dao
import androidx.room.*
import com.ldnprod.timer.Entities.TaskEntity

@Dao
interface TaskDao {
    @Insert
    fun insert(sequence: TaskEntity)

    @Delete
    fun delete(sequence: TaskEntity)

    @Update
    fun update(sequence: TaskEntity)

    @Query("SELECT * FROM tasks")
    fun getAll(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE sequence_id =:sequenceId")
    fun getAllTasksInSequence(sequenceId: Int): List<TaskEntity>

    @Transaction
    @Query("SELECT * FROM sequences")
    fun getSequenceWithTasks(): List<SequenceWithTask>
}