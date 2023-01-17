package com.ldnprod.mytrainings

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
import com.ldnprod.mytrainings.Adapters.ExercisePreviewAdapter
import com.ldnprod.mytrainings.Services.Constants.*
import com.ldnprod.mytrainings.Services.ServiceHelper
import com.ldnprod.mytrainings.Services.TrainingService
import com.ldnprod.mytrainings.Utils.PlayTrainingEvent
import com.ldnprod.mytrainings.ViewModels.PlayTrainingViewModel.PlayTrainingViewModelEvent
import com.ldnprod.mytrainings.ViewModels.PlayTrainingViewModel.PlayTrainingViewModel
import com.ldnprod.mytrainings.databinding.ActivityPlayTrainingBinding
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
            trainingService.trainingId.observe(this@PlayTrainingActivity as LifecycleOwner) { trainingId ->
                if (trainingId == viewModel.training.value!!.id){
                    trainingService.currentState.observe(this@PlayTrainingActivity as LifecycleOwner) {
                        when(it.name){
                            TrainingService.State.Idle.name -> {
                                setButtons(R.drawable.ic_play, View.GONE)
                                viewModel.onEvent(PlayTrainingEvent.OnTrainingEnded)
                            }
                            TrainingService.State.Playing.name -> {
                                setButtons(R.drawable.ic_pause, View.VISIBLE)
                            }
                            TrainingService.State.Paused.name -> {
                                setButtons(R.drawable.ic_play, View.VISIBLE)
                            }
                            TrainingService.State.Stopped.name -> {
                                setButtons(R.drawable.ic_play, View.VISIBLE)
                                viewModel.onEvent(PlayTrainingEvent.OnTrainingEnded)
                            }
                            TrainingService.State.Forwarded.name -> {
                                setButtons(R.drawable.ic_pause, View.VISIBLE)
                            }
                            TrainingService.State.ChangedTraining.name -> {
                                viewModel.onEvent(PlayTrainingEvent.OnTrainingChanged(trainingId))
                                //setButtons(R.drawable.ic_pause, View.VISIBLE)
                            }
                        }
                    }
                    trainingService.currentExercise.observe(this@PlayTrainingActivity as LifecycleOwner) {
                        it?.let { exercise ->
                            binding.exerciseTitle.text = exercise.description
                            viewModel.onEvent(PlayTrainingEvent.OnExerciseAchieved(exercise))
                        }

                    }
                    trainingService.remainingTime.observe(this@PlayTrainingActivity as LifecycleOwner) {
                        if (trainingService.isPlaying.value!!) {
                            @SuppressLint("SetTextI18n")
                            binding.remainingTimeTextview.text =
                                "${"%02d".format(it / 60)}:${"%02d".format(it % 60)}"
                        }
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
            startPauseButton.setOnClickListener {
                ServiceHelper.triggerForegroundService(
                    this@PlayTrainingActivity,
                    if (trainingService.isPlaying.value!!
                        && trainingService.trainingId.value == viewModel.training.value!!.id)
                        ACTION_SERVICE_PAUSE
                    else ACTION_SERVICE_PLAY,
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
            viewModel.currentExercise.observe(this@PlayTrainingActivity as LifecycleOwner) {
                it?.let { exercise ->
                    @SuppressLint("SetTextI18n")
                    remainingTimeTextview.text =
                        "${"%02d".format(exercise.duration / 60)}:${
                            "%02d".format(exercise.duration % 60)}"
                    exerciseTitle.text = exercise.description
                }
            }
            viewModel.training.observe(this@PlayTrainingActivity as LifecycleOwner) {
                it?.let { training ->
                    this@PlayTrainingActivity.title = training.title
                }
            }
        }
        lifecycleScope.launch {
            viewModel.viewModelEvent.collect { event ->
                when (event) {
                    is PlayTrainingViewModelEvent.ExerciseDeleted -> {
                        exerciseAdapter.notifyItemRemoved(event.position)
                    }
                    is PlayTrainingViewModelEvent.TrainingLoaded -> {
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
            startPauseButton.setImageResource(startButtonResource)
            stopButton.visibility = stopButtonVisibility
        }
    }
}