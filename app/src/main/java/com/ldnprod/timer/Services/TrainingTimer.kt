package com.ldnprod.timer.Services

import android.os.CountDownTimer
import com.ldnprod.timer.Entities.Exercise

abstract class TrainingTimer(
    private val exercises: List<Exercise>
) {
    private lateinit var countDownTimer: CountDownTimer
    private var counter = 0
    private var remainingTime = exercises[0].duration * 1000L

    abstract fun onTick(exercise: Exercise, millisUntilFinishedExercise: Long, order: Int)
    abstract fun onExerciseSwitch(prevExercise: Exercise, nextExercise: Exercise)
    abstract fun onFinish()

    fun start() {
        setOnExercise(0)
        countDownTimer.start()
    }

    fun cancel() {
        countDownTimer.cancel()
    }

    private fun goNext() {
        setOnExercise(counter + 1)
        countDownTimer.start()
    }

    fun pause() {
        countDownTimer.cancel()
    }

    fun resume() {
        setOnExercise(counter, remainingTime)
        countDownTimer.start()
    }
    fun setOnExercise(order: Int, timeLeft: Long = exercises[order].duration * 1000L) {
        onExerciseSwitch(exercises[counter], exercises[order])
        counter = order
        remainingTime = timeLeft
        if (this::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
        countDownTimer = setupTimer(timeLeft)
    }
    private fun setupTimer(timeLeft: Long) : CountDownTimer {
        return object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                onTick(exercises[counter], millisUntilFinished, counter)
            }
            override fun onFinish() {
                if (counter < exercises.size - 1) {
                    goNext()
                } else {
                    this@TrainingTimer.onFinish()
                }
            }

        }
    }
}