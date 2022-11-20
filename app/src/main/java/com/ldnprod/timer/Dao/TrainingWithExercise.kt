package com.ldnprod.timer.Dao

import androidx.room.Embedded
import androidx.room.Relation
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.Entities.Exercise

data class TrainingWithExercise (
    @Embedded val sequence: Training,
    @Relation(
        parentColumn = "id",
        entityColumn = "sequence_id"
    )
    val tasks: List<Exercise>
)