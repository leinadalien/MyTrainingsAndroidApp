package com.ldnprod.timer.Services

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.ldnprod.timer.PlayTrainingActivity
import com.ldnprod.timer.Services.Constants.*

object ServiceHelper {
    private val flag =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PendingIntent.FLAG_IMMUTABLE
        else
            0
    fun clickPendingIntent(context: Context, trainingId: Int): PendingIntent {
        val resultIntent = Intent(context, PlayTrainingActivity::class.java).apply { putExtra("trainingId", trainingId) }
        val resultPendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(
                CLICK_REQUEST_CODE,
                PendingIntent.FLAG_UPDATE_CURRENT)
        }
        return resultPendingIntent
    }

    fun pausePendingIntent(context: Context): PendingIntent {
        val pauseIntent = Intent(context, TrainingService::class.java).apply {
            putExtra(TRAINING_STATE, TrainingService.State.Paused.name)
        }
        return PendingIntent.getService(
            context, PAUSE_REQUEST_CODE, pauseIntent, flag
        )
    }

    fun resumePendingIntent(context: Context): PendingIntent {
        val resumeIntent = Intent(context, TrainingService::class.java).apply {
            putExtra(TRAINING_STATE, TrainingService.State.Started.name)
        }
        return PendingIntent.getService(
            context, RESUME_REQUEST_CODE, resumeIntent, flag
        )
    }

    fun stopPendingIntent(context: Context): PendingIntent {
        val stopIntent = Intent(context, TrainingService::class.java).apply {
            putExtra(TRAINING_STATE, TrainingService.State.Stopped.name)
        }
        return PendingIntent.getService(
            context, STOP_REQUEST_CODE, stopIntent, flag
        )
    }

    fun nextExercisePendingIntent(context: Context): PendingIntent {
        val nextIntent = Intent(context, TrainingService::class.java).apply {
            putExtra(TRAINING_STATE, TrainingService.State.Started.name)
        }
        return PendingIntent.getService(
            context, NEXT_REQUEST_CODE, nextIntent, flag
        )
    }

    fun triggerForegroundService(context: Context, action: String, trainingId: Int, exerciseId: Int) {
        Intent(context, TrainingService::class.java).apply {
            this.action = action
            this.putExtra(EXERCISE_ID, exerciseId)
            this.putExtra(TRAINING_ID, trainingId)
            context.startService(this)
        }
    }
}