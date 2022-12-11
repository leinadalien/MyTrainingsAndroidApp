package com.ldnprod.timer.Utils

import com.ldnprod.timer.Entities.Training

sealed class TrainingListEvent {
    data class OnTrainingClick(val training: Training): TrainingListEvent()
    data class OnEditTrainingClick(val training: Training): TrainingListEvent()
    data class OnDeleteTrainingClick(val training: Training, val position: Int): TrainingListEvent()
    object OnRequestUpdatesForList: TrainingListEvent()
}
