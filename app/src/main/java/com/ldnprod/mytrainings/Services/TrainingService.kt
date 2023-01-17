package com.ldnprod.mytrainings.Services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import androidx.lifecycle.MutableLiveData
import com.ldnprod.mytrainings.Entities.Exercise
import com.ldnprod.mytrainings.Interfaces.IExerciseRepository
import com.ldnprod.mytrainings.Interfaces.ITrainingRepository
import com.ldnprod.mytrainings.R
import com.ldnprod.mytrainings.Services.Constants.*
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

    private lateinit var doneSoundPlayer: MediaPlayer
    private lateinit var prepareSoundPlayer: MediaPlayer
    inner class TrainingBinder : Binder() {
        fun getService(): TrainingService = this@TrainingService
    }
    enum class State {
        Idle, Playing, Paused, Stopped, Forwarded, ChangedTraining
    }
    private val binder = TrainingBinder()
    private lateinit var timer: TrainingTimer
    override fun onCreate() {
        super.onCreate()
        doneSoundPlayer = MediaPlayer.create(this@TrainingService, R.raw.done_sound)
        prepareSoundPlayer = MediaPlayer.create(this@TrainingService, R.raw.prepare_sound)
    }
    var remainingTime = MutableLiveData(0)
        private set
    var currentState = MutableLiveData(State.Idle)
        private set
    var isPlaying = MutableLiveData(false)
        private set
    var trainingId = MutableLiveData(0)
        private set
    lateinit var exercises: List<Exercise>
        private set
    var currentExercise = MutableLiveData<Exercise>(null)

    override fun onBind(intent: Intent?) = binder

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val trainingId = it.getIntExtra(TRAINING_ID, -1)
            when(it.getStringExtra(TRAINING_STATE)) {
                State.Playing.name -> playTraining(trainingId)
                State.Paused.name -> pauseTraining()
                State.Stopped.name -> {
                    stopTraining()
                    currentState.postValue(State.Idle)
                    stopForegroundService()
                }
                State.Forwarded.name -> goNextExerciseInTraining(trainingId)
            }
            when(it.action) {
                ACTION_SERVICE_START -> startTraining(trainingId)
                ACTION_SERVICE_PAUSE -> pauseTraining()
                ACTION_SERVICE_PLAY -> playTraining(trainingId)
                ACTION_SERVICE_STOP ->  {
                    stopTraining()
                    currentState.postValue(State.Idle)
                    stopForegroundService()
                }
                ACTION_SERVICE_NEXT -> goNextExerciseInTraining(trainingId)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    private fun startTraining(trainingId: Int) {
        isPlaying.postValue(true)
        this.trainingId.postValue(trainingId)
        CoroutineScope(Dispatchers.IO).launch {
            exercises = exerciseRepository.getAllInTrainingByOrder(trainingId)
            val training = trainingRepository.getTrainingWithId(trainingId)
            training?.let {
                notificationBuilder.setContentTitle(it.title).build()
            }
            currentExercise.postValue(exercises[0])
        }

        setNotificationButton(0, "Pause",ServiceHelper.pausePendingIntent(this, trainingId))
        startForegroundService()

        notificationBuilder.setContentIntent(ServiceHelper.clickPendingIntent(applicationContext, trainingId)).build()
        currentState.postValue(State.Playing)
        timer = object : TrainingTimer(exercises){
            override fun onTick(exercise: Exercise, millisUntilFinishedExercise: Long, order: Int) {
                updateNotification(exercise, (millisUntilFinishedExercise / 1000).toInt())
                remainingTime.postValue((millisUntilFinishedExercise / 1000).toInt())
                if (millisUntilFinishedExercise in 0..2999) {
                    prepareSoundPlayer.start()
                }
            }

            override fun onExerciseSwitch(prevExercise: Exercise, nextExercise: Exercise) {
                this@TrainingService.currentExercise.postValue(nextExercise)
            }

            override fun onGoForward() {
                doneSoundPlayer.start()
                this@TrainingService.currentExercise.postValue(exercises[exerciseIndex + 1])
                currentState.postValue(State.Forwarded)
            }

            override fun onFinish() {
                stopTraining()
                doneSoundPlayer.start()
                timer.setOnExercise(0)
                currentExercise.postValue(exercises[0])
                setNotificationButton(0, "Start", ServiceHelper.playPendingIntent(this@TrainingService, trainingId))
                remainingTime.postValue(exercises[0].duration)
                updateNotification(exercises[0], exercises[0].duration)
            }

        }
        timer.start()
    }
    private fun pauseTraining() {
        timer.pause()
        currentState.postValue(State.Paused)
        isPlaying.postValue(false)
        setNotificationButton(0, "Resume",ServiceHelper.playPendingIntent(this, trainingId.value!!))
    }
    private fun playTraining(trainingId: Int) {
        if (this::timer.isInitialized) {
            if (trainingId == this.trainingId.value) {
                isPlaying.postValue(true)
                timer.resume()
                currentState.postValue(State.Playing)
                setNotificationButton(0, "Pause",ServiceHelper.pausePendingIntent(this, trainingId))
            } else {
                stopTraining()
                startTraining(trainingId)
                currentState.postValue(State.ChangedTraining)
            }
        } else {
            startTraining(trainingId)
        }

    }
    private fun stopTraining() {
        isPlaying.postValue(false)
        timer.cancel()
        currentState.postValue(State.Stopped)
    }
    private fun goNextExerciseInTraining(trainingId: Int){
        timer.goForward()
        currentState.postValue(State.Forwarded)
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