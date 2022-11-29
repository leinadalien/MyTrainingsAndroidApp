package com.ldnprod.timer.ViewModels.TrainingListViewModel

sealed class TrainingListViewModelEvent {
    data class ItemInserted(val position: Int): TrainingListViewModelEvent()
}
