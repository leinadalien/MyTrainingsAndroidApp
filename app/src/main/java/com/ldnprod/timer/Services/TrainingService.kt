package com.ldnprod.timer.Services

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat.Builder
import com.ldnprod.timer.Services.Constants.ACTION_SERVICE_CANCEL
import com.ldnprod.timer.Services.Constants.ACTION_SERVICE_START
import com.ldnprod.timer.Services.Constants.ACTION_SERVICE_STOP
import com.ldnprod.timer.Services.Constants.TRAINING_STATE
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer

import javax.inject.Inject
import kotlin.time.Duration


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

    private var duration: Duration = Duration.ZERO
    private lateinit var timer: Timer

    var seconds = "00"
        private set
    var minutes = "00"
        private set
    var exercise = "Exercise"
        private set
    var currentState = State.Idle
        private set

    override fun onBind(intent: Intent?) = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.getStringExtra(TRAINING_STATE)) {
            State.Started.name -> {

            }
            State.Stopped.name -> {

            }
            State.Canceled.name -> {

            }
        }
        intent?.let {
            when(it.action) {
                ACTION_SERVICE_START -> {

                }
                ACTION_SERVICE_STOP -> {

                }
                ACTION_SERVICE_CANCEL -> {

                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}