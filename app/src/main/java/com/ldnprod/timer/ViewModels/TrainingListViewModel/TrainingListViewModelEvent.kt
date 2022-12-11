package com.ldnprod.timer.ViewModels.TrainingListViewModel

import com.ldnprod.timer.Entities.Training

sealed class TrainingListViewModelEvent {
    data class TrainingInserted(val position: Int): TrainingListViewModelEvent()
    data class TrainingChanged(val position: Int): TrainingListViewModelEvent()
    data class TrainingSetChanged(val trainings: List<Training>): TrainingListViewModelEvent()
    data class TrainingRemoved(val position: Int): TrainingListViewModelEvent()
    data class TrainingOpened(val training: Training): TrainingListViewModelEvent()
    data class TrainingInfoOpened(val training: Training): TrainingListViewModelEvent()
}
