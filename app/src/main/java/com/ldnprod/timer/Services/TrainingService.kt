package com.ldnprod.timer.Services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
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
import com.ldnprod.timer.Services.Constants.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Timer

import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer


@AndroidEntryPoint
class TrainingService: Service() {
    @Inject
    lateinit var notificationManager: NotificationManager
    @Inject
    lateinit var notificationBuilder: Builder
    @Inject
    lateinit var exerciseRepository: IExerciseRepository
    inner class TrainingBinder : Binder() {
        fun getService(): TrainingService = this@TrainingService
    }
    enum class State {
        Idle,
        Started,
        Paused,
        Stopped
    }
    private val binder = TrainingBinder()

    private var counter = 0;
    private lateinit var timer: Timer

    var remainingTime = MutableLiveData(0)
        private set
    var exercise = MutableLiveData<Exercise>(null)
        private set
    var currentState = MutableLiveData(State.Idle)
        private set

    override fun onBind(intent: Intent?) = binder

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.getStringExtra(TRAINING_STATE)) {
            State.Started.name -> {
                setPauseButton()
                startForegroundService()
                CoroutineScope(Dispatchers.IO).launch {
                    exercise.postValue(exerciseRepository.getById(intent.getIntExtra(EXERCISE_ID, 1)))
                }
                notificationBuilder.setContentIntent(ServiceHelper.clickPendingIntent(applicationContext, exercise.value!!.trainingId))
                startTraining()
            }
            State.Paused.name -> {
                stopTraining()
                setResumeButton()
            }
            State.Stopped.name -> {
                stopTraining()
                cancelTraining()
                stopForegroundService()
            }
        }
        intent?.let {
            when(it.action) {
                ACTION_SERVICE_START -> {
                    setPauseButton()
                    startForegroundService()
                    CoroutineScope(Dispatchers.IO).launch {
                        exercise.postValue(exerciseRepository.getById(intent.getIntExtra(EXERCISE_ID, 1)))
                    }
                    startTraining()
                }
                ACTION_SERVICE_PAUSE -> {
                    setResumeButton()
                    stopTraining()
                }
                ACTION_SERVICE_STOP -> {
                    stopTraining()
                    cancelTraining()
                    stopForegroundService()
                }
                ACTION_SERVICE_NEXT -> {

                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    private fun startTraining() {
        currentState.value = State.Started
        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            counter += 1
            updateTimerUnits()
            updateNotification()
        }
    }
    private fun stopTraining() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        currentState.value = State.Paused
    }
    private fun cancelTraining() {
        counter = 0
        currentState.value = State.Idle
        updateTimerUnits()
    }
    private fun updateTimerUnits() {
        this.remainingTime.postValue(exercise.value!!.duration - counter)
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
    private fun updateNotification() {
        exercise.value!!.let { exercise ->
//            if (remainingTime == 0) {
//                if(exercise.)
//            }
            notificationManager.notify(
                NOTIFICATION_ID,
                notificationBuilder.setContentText(
                    remainingTime.value!!.let { remainingTime ->
                        "${exercise.description} - ${
                            "%02d".format(remainingTime / 60)}:${
                            "%02d".format(remainingTime % 60)}"
                    }

                ).build()
            )
        }
    }
    @SuppressLint("RestrictedApi")
    private fun setPauseButton(){
        notificationBuilder.mActions.removeAt(0)
        notificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0, "Pause", ServiceHelper.pausePendingIntent(this)
            )
        )
        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build())
    }
    @SuppressLint("RestrictedApi")
    private fun setResumeButton() {
        notificationBuilder.mActions.removeAt(0)
        notificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0, "Resume", ServiceHelper.resumePendingIntent(this)
            )
        )
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
}