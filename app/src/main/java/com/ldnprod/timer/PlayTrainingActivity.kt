package com.ldnprod.timer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ldnprod.timer.Adapters.ExercisePreviewAdapter
import com.ldnprod.timer.Services.TrainingService
import com.ldnprod.timer.ViewModels.PlayTrainingViewModel.PlayTrainingViewModelEvent
import com.ldnprod.timer.ViewModels.PlayTrainingViewModel.PlayTrainingViewModel
import com.ldnprod.timer.databinding.ActivityPlayTrainingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayTrainingActivity : AppCompatActivity() {
    private var isBound = false
    private val viewModel by viewModels<PlayTrainingViewModel>()
    private lateinit var binding: ActivityPlayTrainingBinding
    private lateinit var exerciseAdapter: ExercisePreviewAdapter
    private lateinit var trainingService: TrainingService
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TrainingService.TrainingBinder
            trainingService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }
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
                            @SuppressLint("SetTextI18n")
                            remainingTimeTextview.text = "${"%02d".format(viewModel.remainingTime / 60)}:${"%02d".format(viewModel.remainingTime % 60)}"
                            exerciseTitle.text = viewModel.currentExercise
                        }
                        exerciseAdapter.exercises = viewModel.exercises
                        exerciseAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, TrainingService::class.java).also { intent ->  
            bindService(intent, connection,Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isBound = false
    }
}