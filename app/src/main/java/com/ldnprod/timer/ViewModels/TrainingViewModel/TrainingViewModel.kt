package com.ldnprod.timer.ViewModels.TrainingViewModel

import androidx.lifecycle.*
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Entities.Training
import com.ldnprod.timer.Interfaces.IExerciseRepository
import com.ldnprod.timer.Interfaces.ITrainingRepository
import com.ldnprod.timer.Utils.TrainingEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val exerciseRepository: IExerciseRepository,
    private val trainingRepository: ITrainingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var training = MutableLiveData<Training?>(null)
        private set
    var title = MutableLiveData("")
        private set
    lateinit var exercises: ArrayList<Exercise>

    fun addExercise(exercise: Exercise) {
        exercises.add(exercise)
        sendEventToUI(TrainingViewModelEvent.ExerciseInserted(exercises.size, exercise))
    }

    private val _viewModelEvent = Channel<TrainingViewModelEvent> { }
    val viewModelEvent = _viewModelEvent.receiveAsFlow()

    init {
        val trainingId = savedStateHandle.get<Int>("trainingId") ?: -1
        if (trainingId != -1) {
            viewModelScope.launch(Dispatchers.IO) {
                trainingRepository.getTrainingWithId(trainingId)?.let {
                    title.value = it.title
                    training.value = it
                    exercises =
                        trainingRepository.getAllTrainingsWithExercises()[it] as ArrayList<Exercise>
                }

            }
        } else {
            exercises = ArrayList()
        }
    }

    fun onEvent(event: TrainingEvent) {
        when (event) {
            is TrainingEvent.OnTitleChanged -> {
                title.value = event.title
            }
            is TrainingEvent.OnExerciseClick -> {
            }
            is TrainingEvent.OnAddButtonClick -> {
                sendEventToUI(TrainingViewModelEvent.CreateExercise)
            }
            is TrainingEvent.OnDoneButtonClick -> {
                viewModelScope.launch {
                    title.value?.let { trainingTitle ->
                        if (trainingTitle.isNotBlank()) {
                            if (training.value == null) training.value = Training(title = trainingTitle)
                            training.value?.let {
                                it.title = trainingTitle
                                trainingRepository.insert(
                                    it
                                )
                                for (order in exercises.indices) {
                                    if (order > 0) {
                                        exercises[order].previousExerciseId =
                                            exercises[order - 1].id
                                    }
                                    if (order < exercises.size - 1) {
                                        exercises[order].nextExerciseId = exercises[order + 1].id
                                    }
                                    exercises[order].trainingId = it.id
                                    exerciseRepository.insert(exercises[order])
                                }
                                sendEventToUI(TrainingViewModelEvent.CloseDetailed(it))
                            }
                        }
                    }
                }
            }
            is TrainingEvent.OnDeleteExerciseClick -> {
                viewModelScope.launch {
                    exerciseRepository.delete(event.exercise)
                }
            }
        }
    }

    private fun sendEventToUI(event: TrainingViewModelEvent) {
        viewModelScope.launch {
            _viewModelEvent.send(event)
        }
    }
}