package com.ldnprod.timer.ViewModels.PlayTrainingViewModel

sealed class PlayTrainingViewModelEvent{
    data class ExerciseDeleted(val position: Int): PlayTrainingViewModelEvent()
    object TrainingLoaded: PlayTrainingViewModelEvent()
}
