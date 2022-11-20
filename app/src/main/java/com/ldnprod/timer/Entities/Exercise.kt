package com.ldnprod.timer.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
class Exercise (
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "duration") var duration: Int,
        @ColumnInfo(name = "description") var description: String,
        @ColumnInfo(name = "training_id") val trainingId: Int,
        @ColumnInfo(name = "previous_exercise_id") var previousExerciseId: Int?,
        @ColumnInfo(name = "next_exercise_id") var nextExerciseId: Int?,
)