package com.ldnprod.timer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.Drawable
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ldnprod.timer.Adapters.ExercisePreviewAdapter
import com.ldnprod.timer.Services.Constants.*
import com.ldnprod.timer.Services.ServiceHelper
import com.ldnprod.timer.Services.TrainingService
import com.ldnprod.timer.Utils.PlayTrainingEvent
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
            trainingService.currentState.observe(this@PlayTrainingActivity as LifecycleOwner) {
                when(it.name){
                    TrainingService.State.Idle.name -> {
                        setButtons(R.drawable.ic_play, View.GONE)
                        viewModel.onEvent(PlayTrainingEvent.OnTrainingEnded)
                    }
                    TrainingService.State.Started.name -> setButtons(R.drawable.ic_pause, View.VISIBLE)
                    TrainingService.State.Paused.name -> setButtons(R.drawable.ic_play, View.VISIBLE)
                    TrainingService.State.Resumed.name -> setButtons(R.drawable.ic_pause, View.VISIBLE)
                    TrainingService.State.Stopped.name -> {
                        setButtons(R.drawable.ic_play, View.VISIBLE)
                        viewModel.onEvent(PlayTrainingEvent.OnTrainingEnded)
                    }
                    TrainingService.State.GoNext.name -> onGoNextExercise()
                }
            }
            binding.apply {
                trainingService.remainingTime.observe(this@PlayTrainingActivity as LifecycleOwner) {
                    if (trainingService.currentState.value == TrainingService.State.Started) {
                        @SuppressLint("SetTextI18n")
                        remainingTimeTextview.text =
                            "${"%02d".format(it / 60)}:${"%02d".format(it % 60)}"
                    }
                }
                trainingService.exerciseDescription.observe(this@PlayTrainingActivity as LifecycleOwner) {
                    if (trainingService.currentState.value == TrainingService.State.Started) {
                        exerciseTitle.text = it
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
        exerciseAdapter = ExercisePreviewAdapter(viewModel.remainingExercises) { viewModel.onEvent(it) }
        binding.apply {
            upcomingExercisesRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@PlayTrainingActivity)
                adapter = exerciseAdapter
            }
            startButton.setOnClickListener {
                ServiceHelper.triggerForegroundService(
                    this@PlayTrainingActivity,
                    if (trainingService.currentState.value == TrainingService.State.Started) ACTION_SERVICE_PAUSE
                    else ACTION_SERVICE_RESUME,
                    viewModel.training.value!!.id,
                )

            }
            stopButton.setOnClickListener {
                ServiceHelper.triggerForegroundService(
                    this@PlayTrainingActivity, ACTION_SERVICE_STOP,
                    viewModel.training.value!!.id,
                )
                stopButton.visibility = View.GONE
            }
            stopButton.visibility = View.GONE
        }
        lifecycleScope.launch {
            viewModel.viewModelEvent.collect { event ->
                when (event) {
                    is PlayTrainingViewModelEvent.ExerciseDeleted -> {
                        exerciseAdapter.notifyItemRemoved(event.position)
                    }
                    is PlayTrainingViewModelEvent.TrainingLoaded -> {
                        binding.apply {
                            trainingTitle.text = viewModel.training.value!!.title
                            @SuppressLint("SetTextI18n")
                            remainingTimeTextview.text =
                                "${"%02d".format(viewModel.currentExercise.value!!.duration / 60)}:${
                                    "%02d".format(viewModel.currentExercise.value!!.duration % 60)
                                }"
                            exerciseTitle.text = viewModel.currentExercise.value!!.description
                        }
                        exerciseAdapter.exercises = viewModel.remainingExercises
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
    private fun setButtons(startButtonResource: Int, stopButtonVisibility: Int) {
        binding.apply {
            startButton.setImageResource(startButtonResource)
            stopButton.visibility = stopButtonVisibility
        }
    }
    private fun onGoNextExercise(){
        viewModel.onEvent(PlayTrainingEvent.OnGoNextExercise)
    }
}