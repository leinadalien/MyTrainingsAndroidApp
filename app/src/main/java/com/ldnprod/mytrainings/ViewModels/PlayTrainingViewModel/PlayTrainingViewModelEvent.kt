package com.ldnprod.mytrainings.ViewModels.PlayTrainingViewModel

sealed class PlayTrainingViewModelEvent{
    data class ExerciseDeleted(val position: Int): PlayTrainingViewModelEvent()
    object TrainingLoaded: PlayTrainingViewModelEvent()
}
