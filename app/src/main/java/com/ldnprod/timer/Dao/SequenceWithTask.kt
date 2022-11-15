package com.ldnprod.timer.Dao

import androidx.room.Embedded
import androidx.room.Relation
import com.ldnprod.timer.Entities.SequenceEntity
import com.ldnprod.timer.Entities.TaskEntity

data class SequenceWithTask (
    @Embedded val sequence: SequenceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "sequence_id"
    )
    val tasks: List<TaskEntity>
)