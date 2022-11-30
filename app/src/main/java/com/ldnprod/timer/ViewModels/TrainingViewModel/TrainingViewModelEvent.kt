package com.ldnprod.timer.ViewModels.TrainingViewModel

import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Entities.Training


sealed class TrainingViewModelEvent {
    data class ExerciseInserted(val position: Int, val exercise: Exercise): TrainingViewModelEvent()
    data class ExerciseRemoved(val position: Int): TrainingViewModelEvent()
    data class ExerciseMoved(val fromPosition: Int, val toPosition: Int): TrainingViewModelEvent()
    object ExerciseDataSetChanged: TrainingViewModelEvent()
    data class CloseDetailed(val training: Training): TrainingViewModelEvent()
    object CreateExercise: TrainingViewModelEvent()
}
