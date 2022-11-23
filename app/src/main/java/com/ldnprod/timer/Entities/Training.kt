package com.ldnprod.timer.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "trainings")
data class Training (
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "title") var string: String,

)