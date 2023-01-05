package com.ldnprod.timer.Services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import androidx.lifecycle.MutableLiveData
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Interfaces.IExerciseRepository
import com.ldnprod.timer.Interfaces.ITrainingRepository
import com.ldnprod.timer.Services.Constants.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import javax.inject.Inject


@AndroidEntryPoint
class TrainingService: Service() {
    @Inject
    lateinit var notificationManager: NotificationManager
    @Inject
    lateinit var notificationBuilder: Builder
    @Inject
    lateinit var exerciseRepository: IExerciseRepository
    @Inject
    lateinit var trainingRepository: ITrainingRepository

    inner class TrainingBinder : Binder() {
        fun getService(): TrainingService = this@TrainingService
    }
    enum class State {
        Idle, Started, Paused, Resumed, Stopped
    }
    private val binder = TrainingBinder()

    private lateinit var timer: TrainingTimer

    var remainingTime = MutableLiveData(0)
        private set
    var exerciseDescription = MutableLiveData("Example exercise")
        private set
    var currentState = MutableLiveData(State.Idle)
        private set
    lateinit var exercises: List<Exercise>
        private set

    override fun onBind(intent: Intent?) = binder

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val trainingId = intent.getIntExtra(TRAINING_ID, -1)!!
            when(intent.getStringExtra(TRAINING_STATE)) {
                State.Started.name -> startTraining(trainingId)
                State.Paused.name -> pauseTraining(trainingId)
                State.Resumed.name -> resumeTraining(trainingId)
                State.Stopped.name -> {
                    stopTraining(trainingId)
                    stopForegroundService()
                }
            }
            when(it.action) {
                ACTION_SERVICE_START -> startTraining(trainingId)
                ACTION_SERVICE_PAUSE -> pauseTraining(trainingId)
                ACTION_SERVICE_RESUME -> resumeTraining(trainingId)
                ACTION_SERVICE_STOP ->  {
                    stopTraining(trainingId)
                    stopForegroundService()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }
    private fun startTraining(trainingId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            exercises = exerciseRepository.getAllInTrainingByOrder(trainingId)
            val training = trainingRepository.getTrainingWithId(trainingId)
            training?.let {
                notificationBuilder.setContentTitle(it.title).build()
            }
        }

        setNotificationButton(0, "Pause",ServiceHelper.pausePendingIntent(this, trainingId))
        startForegroundService()

        notificationBuilder.setContentIntent(ServiceHelper.clickPendingIntent(applicationContext, trainingId)).build()
        currentState.value = State.Started
        timer = object : TrainingTimer(exercises){
            override fun onTick(exercise: Exercise, millisUntilFinishedExercise: Long, order: Int) {
                updateNotification(exercise, (millisUntilFinishedExercise / 1000).toInt())
                remainingTime.postValue((millisUntilFinishedExercise / 1000).toInt())
            }

            override fun onExerciseSwitch(prevExercise: Exercise, nextExercise: Exercise) {
                exerciseDescription.postValue(nextExercise.description)
            }

            override fun onFinish() {
                stopTraining(trainingId)
                timer.setOnExercise(0)
                setNotificationButton(0, "Start", ServiceHelper.resumePendingIntent(this@TrainingService, trainingId))
            }

        }
        timer.start()
    }
    private fun pauseTraining(trainingId: Int) {
        timer.pause()
        currentState.value = State.Paused
        setNotificationButton(0, "Resume",ServiceHelper.resumePendingIntent(this, trainingId))
    }
    private fun resumeTraining(trainingId: Int) {
        if (this::timer.isInitialized) {
            timer.resume()
            currentState.value = State.Started
            setNotificationButton(0, "Pause",ServiceHelper.pausePendingIntent(this, trainingId))
        } else {
            startTraining(trainingId)
        }

    }
    private fun stopTraining(trainingId: Int) {
        timer.cancel()
        timer.setOnExercise(0)
        currentState.value = State.Idle

    }
    private fun startForegroundService() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun stopForegroundService() {
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun updateNotification(exercise: Exercise, remainingTime: Int) {
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentText(
                "${exercise.description} - ${
                    "%02d".format(remainingTime / 60)}:${
                    "%02d".format(remainingTime % 60)}"
            ).build()
        )
    }
    @SuppressLint("RestrictedApi")
    private fun setNotificationButton(index: Int, text: String, pendingIntent: PendingIntent){
        notificationBuilder.mActions.removeAt(index)
        notificationBuilder.mActions.add(index, NotificationCompat.Action(0, text, pendingIntent))
        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build())
    }
}