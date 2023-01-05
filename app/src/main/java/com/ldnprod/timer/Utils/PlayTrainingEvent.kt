package com.ldnprod.timer.Utils

sealed class PlayTrainingEvent{
    data class OnSkipClicked(val position: Int): PlayTrainingEvent()
    object OnGoNextExercise: PlayTrainingEvent()
    object OnTrainingEnded: PlayTrainingEvent()
}
