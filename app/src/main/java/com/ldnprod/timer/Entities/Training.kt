package com.ldnprod.timer.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "trainings")
class Training (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "title") var string: String,

)