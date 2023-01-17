package com.ldnprod.mytrainings.ViewModels.TrainingViewModel

import com.ldnprod.mytrainings.Entities.Exercise
import com.ldnprod.mytrainings.Entities.Training


sealed class TrainingViewModelEvent {
    data class ExerciseInserted(val position: Int, val exercise: Exercise): TrainingViewModelEvent()
    data class ExerciseChanged(val position: Int): TrainingViewModelEvent()
    data class ExerciseRemoved(val position: Int): TrainingViewModelEvent()
    data class ExerciseMoved(val fromPosition: Int, val toPosition: Int): TrainingViewModelEvent()
    data class TrainingSaved(val training: Training): TrainingViewModelEvent()
    object ExerciseCreated: TrainingViewModelEvent()
    object TrainingLoaded: TrainingViewModelEvent()
    data class TrainingStateChanged(val likePrevious: Boolean): TrainingViewModelEvent()
    data class ExerciseOpened(val exercise: Exercise, val position: Int): TrainingViewModelEvent()

}
