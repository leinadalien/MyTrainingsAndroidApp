package com.ldnprod.timer.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise (
        @PrimaryKey(autoGenerate = true) var id: Int = 0,
        @ColumnInfo(name = "duration") var duration: Int,
        @ColumnInfo(name = "description") var description: String,
        @ColumnInfo(name = "repeats") var repeats: Int = 1,
        @ColumnInfo(name = "training_id") val trainingId: Int,
        @ColumnInfo(name = "previous_exercise_id") var previousExerciseId: Int? = null,
        @ColumnInfo(name = "next_exercise_id") var nextExerciseId: Int? = null,
)