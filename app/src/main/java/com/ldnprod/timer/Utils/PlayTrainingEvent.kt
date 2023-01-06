package com.ldnprod.timer.Utils

import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Entities.Training

sealed class PlayTrainingEvent{
    data class OnSkipClicked(val position: Int): PlayTrainingEvent()
    data class OnExerciseAchieved(val exercise: Exercise): PlayTrainingEvent()
    object OnTrainingEnded: PlayTrainingEvent()
    data class OnTrainingChanged(val trainingId: Int): PlayTrainingEvent()
}
