package com.ldnprod.timer.Utils

import com.ldnprod.timer.Entities.Training

sealed class TrainingListEvent {
    data class OnTrainingClick(val training: Training): TrainingListEvent()
    object OnRequestUpdatesForList: TrainingListEvent()
}
