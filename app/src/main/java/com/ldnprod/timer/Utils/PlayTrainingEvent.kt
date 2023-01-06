package com.ldnprod.timer.Utils

sealed class PlayTrainingEvent{
    data class OnSkipClicked(val position: Int): PlayTrainingEvent()
    data class GoToExercise(val position: Int): PlayTrainingEvent()
    object OnTrainingEnded: PlayTrainingEvent()
}
