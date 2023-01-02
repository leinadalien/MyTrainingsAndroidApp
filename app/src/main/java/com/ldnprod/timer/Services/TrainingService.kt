package com.ldnprod.timer.Services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ldnprod.timer.Entities.Exercise
import com.ldnprod.timer.Services.Constants.ACTION_SERVICE_CANCEL
import com.ldnprod.timer.Services.Constants.ACTION_SERVICE_START
import com.ldnprod.timer.Services.Constants.ACTION_SERVICE_STOP
import com.ldnprod.timer.Services.Constants.NOTIFICATION_CHANNEL_ID
import com.ldnprod.timer.Services.Constants.NOTIFICATION_CHANNEL_NAME
import com.ldnprod.timer.Services.Constants.NOTIFICATION_ID
import com.ldnprod.timer.Services.Constants.TRAINING_STATE
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer

import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


@AndroidEntryPoint
class TrainingService() : Service() {
    @Inject
    lateinit var notificationManager: NotificationManager
    @Inject
    lateinit var notificationBuilder: Builder

    inner class TrainingBinder : Binder() {
        fun getService(): TrainingService = this@TrainingService
    }
    enum class State {
        Idle,
        Started,
        Stopped,
        Canceled
    }
    private val binder = TrainingBinder()

    private var counter = 0;
    private lateinit var timer: Timer

    var timeOfExercise = 600
        private set
    var seconds = MutableLiveData("00")
        private set
    var minutes = MutableLiveData("00")
        private set
    var exercise = MutableLiveData("Exercise")
        private set
    var currentState = MutableLiveData(State.Idle)
        private set

    override fun onBind(intent: Intent?) = binder

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.getStringExtra(TRAINING_STATE)) {
            State.Started.name -> {
                setStopButton()
                startForegroundService()
                startTraining { minutes, seconds ->
                    updateNotification(minutes,seconds)
                }
            }
            State.Stopped.name -> {
                stopTraining()
                setResumeButton()
            }
            State.Canceled.name -> {
                stopTraining()
                cancelTraining()
                stopForegroundService()
            }
        }
        intent?.let {
            when(it.action) {
                ACTION_SERVICE_START -> {
                    setStopButton()
                    startForegroundService()
                    startTraining { minutes, seconds ->
                        updateNotification(minutes, seconds)
                    }

                }
                ACTION_SERVICE_STOP -> {
                    setResumeButton()
                    stopTraining()
                }
                ACTION_SERVICE_CANCEL -> {
                    stopTraining()
                    cancelTraining()
                    stopForegroundService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    private fun startTraining(onTick: (minutes: String, seconds: String) -> Unit) {
        currentState.value = State.Started
        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            counter += 1
            updateTimerUnits()
            onTick(minutes.value!!, seconds.value!!)
        }
    }
    private fun stopTraining() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        currentState.value = State.Stopped
    }
    private fun cancelTraining() {
        counter = 0
        currentState.value = State.Idle
        updateTimerUnits()
    }
    private fun updateTimerUnits() {
        val timeLeft = timeOfExercise - counter
        this.minutes.postValue("%02d".format(timeLeft / 60))
        this.seconds.postValue("%02d".format(timeLeft % 60))
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
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun updateNotification(minutes: String, seconds: String) {
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentText(
                "$exercise: $minutes:$seconds"
            ).build()
        )
    }
    @SuppressLint("RestrictedApi")
    private fun setStopButton(){
        notificationBuilder.mActions.removeAt(0)
        notificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0, "Stop", ServiceHelper.stopPendingIntent(this)
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