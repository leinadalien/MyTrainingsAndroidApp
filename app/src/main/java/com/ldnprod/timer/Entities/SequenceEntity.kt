package com.ldnprod.timer.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "sequences")
class SequenceEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "title") var string: String,

)