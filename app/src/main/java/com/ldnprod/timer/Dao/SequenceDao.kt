package com.ldnprod.timer.Dao

import androidx.room.*
import com.ldnprod.timer.Entities.SequenceEntity

@Dao
interface SequenceDao {
    @Insert
    fun insert(sequence: SequenceEntity)

    @Delete
    fun delete(sequence: SequenceEntity)

    @Update
    fun update(sequence: SequenceEntity)

    @Query("SELECT * FROM sequences")
    fun getAll(): List<SequenceEntity>
}