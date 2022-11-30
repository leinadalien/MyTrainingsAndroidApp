package com.ldnprod.timer.ViewModels.TrainingListViewModel

sealed class TrainingListViewModelEvent {
    data class TrainingInserted(val position: Int): TrainingListViewModelEvent()
    object JumpToDetail: TrainingListViewModelEvent()
}
