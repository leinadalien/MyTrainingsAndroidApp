package com.ldnprod.mytrainings.Services

import android.os.CountDownTimer
import com.ldnprod.mytrainings.Entities.Exercise

abstract class TrainingTimer(
    private val exercises: List<Exercise>
) {
    private lateinit var countDownTimer: CountDownTimer
    var exerciseIndex = 0
        private set
    var remainingTimeOfCurrentExercise = exercises[0].duration * 1000L
        private set
    abstract fun onTick(exercise: Exercise, millisUntilFinishedExercise: Long, order: Int)
    abstract fun onExerciseSwitch(prevExercise: Exercise, nextExercise: Exercise)
    abstract fun onGoForward()
    abstract fun onFinish()



    fun start() {
        setOnExercise(0)
        countDownTimer.start()
    }

    fun cancel() {
        exerciseIndex = 0
        remainingTimeOfCurrentExercise = exercises[0].duration * 1000L
        countDownTimer.cancel()
    }

    fun goForward() {
        if (exerciseIndex < exercises.size - 1) {
            onGoForward()
            setOnExercise(exerciseIndex + 1)
            countDownTimer.start()
        } else {
            this@TrainingTimer.onFinish()

        }
    }

    fun pause() {
        countDownTimer.cancel()
    }

    fun resume() {
        setOnExercise(exerciseIndex, remainingTimeOfCurrentExercise)
        countDownTimer.start()
    }
    fun setOnExercise(order: Int, timeLeft: Long = exercises[order].duration * 1000L) {
        onExerciseSwitch(exercises[exerciseIndex], exercises[order])
        exerciseIndex = order
        remainingTimeOfCurrentExercise = timeLeft
        if (this::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
        countDownTimer = setupTimer(timeLeft)
    }
    private fun setupTimer(timeLeft: Long) : CountDownTimer {
        return object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeOfCurrentExercise = millisUntilFinished
                onTick(exercises[exerciseIndex], millisUntilFinished, exerciseIndex)
            }
            override fun onFinish() {
                goForward()
            }

        }
    }
}