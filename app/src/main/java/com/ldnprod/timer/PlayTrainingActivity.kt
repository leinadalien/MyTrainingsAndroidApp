package com.ldnprod.timer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ldnprod.timer.Adapters.ExercisePreviewAdapter
import com.ldnprod.timer.ViewModels.PlayTrainingViewModel.PlayTrainingViewModelEvent
import com.ldnprod.timer.ViewModels.PlayTrainingViewModel.PlayTrainingViewModel
import com.ldnprod.timer.databinding.ActivityPlayTrainingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayTrainingActivity : AppCompatActivity() {
    private val viewModel by viewModels<PlayTrainingViewModel>()
    private lateinit var binding: ActivityPlayTrainingBinding
    private lateinit var exerciseAdapter: ExercisePreviewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        exerciseAdapter = ExercisePreviewAdapter(viewModel.exercises) { viewModel.onEvent(it) }
        binding.apply {
            upcomingExercisesRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@PlayTrainingActivity)
                adapter = exerciseAdapter
            }
        }
        lifecycleScope.launch {
            viewModel.viewModelEvent.collect { event ->
                when(event) {
                    is PlayTrainingViewModelEvent.ExerciseDeleted -> {
                        exerciseAdapter.notifyItemRemoved(event.position)
                    }
                    is PlayTrainingViewModelEvent.TrainingLoaded -> {
                        binding.apply {
                            trainingTitle.text = viewModel.title
                        }
                        exerciseAdapter.exercises = viewModel.exercises
                        exerciseAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}