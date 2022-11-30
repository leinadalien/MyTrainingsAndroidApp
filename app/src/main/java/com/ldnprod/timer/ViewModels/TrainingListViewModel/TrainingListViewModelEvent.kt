package com.ldnprod.timer.ViewModels.TrainingListViewModel

import com.ldnprod.timer.Entities.Training

sealed class TrainingListViewModelEvent {
    data class TrainingInserted(val position: Int): TrainingListViewModelEvent()
    data class TrainingSetChanged(val trainings: List<Training>): TrainingListViewModelEvent()
    object JumpToDetail: TrainingListViewModelEvent()
}
