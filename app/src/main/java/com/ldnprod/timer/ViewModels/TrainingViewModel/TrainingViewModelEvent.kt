package com.ldnprod.timer.ViewModels.TrainingViewModel

import com.ldnprod.timer.Entities.Exercise


sealed class TrainingViewModelEvent {
    data class ExerciseInserted(val position: Int, val exercise: Exercise): TrainingViewModelEvent()
    data class ExerciseRemoved(val position: Int): TrainingViewModelEvent()
    data class ExerciseMoved(val fromPosition: Int, val toPosition: Int): TrainingViewModelEvent()
    object ExerciseDataSetChanged: TrainingViewModelEvent()
    object PopBackStack: TrainingViewModelEvent()
    object CreateExercise: TrainingViewModelEvent()
}
