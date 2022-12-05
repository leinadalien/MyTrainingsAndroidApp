package com.ldnprod.timer.Utils

import com.ldnprod.timer.Entities.Exercise

sealed class TrainingEvent {
    data class OnTitleChanged(val title: String): TrainingEvent()
    data class OnExerciseMoved(val startPosition: Int, val endPosition: Int): TrainingEvent()
    data class OnDeleteExerciseClick(val exercise: Exercise, val position: Int): TrainingEvent()
    data class OnExerciseClick(val exercise: Exercise, val position: Int): TrainingEvent()
    data class OnExerciseChanged(val exercise: Exercise, val position: Int): TrainingEvent()
    object OnAddButtonClick: TrainingEvent()
    object OnDoneButtonClick: TrainingEvent()
}
