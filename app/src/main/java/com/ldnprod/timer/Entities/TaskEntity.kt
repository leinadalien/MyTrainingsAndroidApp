package com.ldnprod.timer.Entities

import android.content.ClipDescription
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Duration

@Entity(tableName = "tasks")
class TaskEntity (
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "duration") var duration: Int,
        @ColumnInfo(name = "description") var description: String,
        @ColumnInfo(name = "sequence_id") val sequenceId: Int,
        @ColumnInfo(name = "previous_task_id") var previousTaskId: Int?,
        @ColumnInfo(name = "next_task_id") var nextTaskId: Int?,
)