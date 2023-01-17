package com.ldnprod.mytrainings.Utils

import com.ldnprod.mytrainings.Entities.Exercise

sealed class PlayTrainingEvent{
    data class OnSkipClicked(val position: Int): PlayTrainingEvent()
    data class OnExerciseAchieved(val exercise: Exercise): PlayTrainingEvent()
    object OnTrainingEnded: PlayTrainingEvent()
    data class OnTrainingChanged(val trainingId: Int): PlayTrainingEvent()
}
