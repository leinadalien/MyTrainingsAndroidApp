package com.ldnprod.timer.Utils

import com.ldnprod.timer.Entities.Exercise

sealed class TrainingEvent {
    data class OnTitleChanged(val title: String):TrainingEvent()
    data class OnDeleteExerciseClick(val exercise: Exercise): TrainingEvent()
    data class OnExerciseClick(val exercise: Exercise): TrainingEvent()
    object OnAddButtonClick: TrainingEvent()
    object OnDoneButtonClick: TrainingEvent()

}
