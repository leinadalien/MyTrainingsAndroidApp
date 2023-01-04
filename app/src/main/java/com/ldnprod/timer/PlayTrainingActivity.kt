package com.ldnprod.timer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ldnprod.timer.Adapters.ExercisePreviewAdapter
import com.ldnprod.timer.Services.Constants.ACTION_SERVICE_STOP
import com.ldnprod.timer.Services.Constants.ACTION_SERVICE_START
import com.ldnprod.timer.Services.Constants.ACTION_SERVICE_PAUSE
import com.ldnprod.timer.Services.ServiceHelper
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
            trainingService.remainingTime.observe(this@PlayTrainingActivity as LifecycleOwner) {
                if (trainingService.currentState.value == TrainingService.State.Started) {
                    binding.apply {
                        @SuppressLint("SetTextI18n")
                        remainingTimeTextview.text =
                            "${"%02d".format(it / 60)}:${"%02d".format(it % 60)}"
                    }
                }
            }
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
            startButton.setOnClickListener {
                if (trainingService.currentState.value == TrainingService.State.Idle) {
                    cancelButton.visibility = View.VISIBLE
                }
                ServiceHelper.triggerForegroundService(
                    this@PlayTrainingActivity,
                    if (trainingService.currentState.value == TrainingService.State.Started) ACTION_SERVICE_PAUSE
                    else ACTION_SERVICE_START,
                    viewModel.currentExercise.value!!.id
                )

            }
            cancelButton.setOnClickListener {
                ServiceHelper.triggerForegroundService(
                    this@PlayTrainingActivity, ACTION_SERVICE_STOP,
                    viewModel.currentExercise.value!!.id
                )
                cancelButton.visibility = View.GONE
            }
            cancelButton.visibility = View.GONE
        }
        lifecycleScope.launch {
            viewModel.viewModelEvent.collect { event ->
                when (event) {
                    is PlayTrainingViewModelEvent.ExerciseDeleted -> {
                        exerciseAdapter.notifyItemRemoved(event.position)
                    }
                    is PlayTrainingViewModelEvent.TrainingLoaded -> {
                        binding.apply {
                            trainingTitle.text = viewModel.title
                            @SuppressLint("SetTextI18n")
                            remainingTimeTextview.text =
                                "${"%02d".format(viewModel.remainingTime / 60)}:${
                                    "%02d".format(viewModel.remainingTime % 60)
                                }"
                            exerciseTitle.text = viewModel.currentExercise.value!!.description
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
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isBound = false
    }
}