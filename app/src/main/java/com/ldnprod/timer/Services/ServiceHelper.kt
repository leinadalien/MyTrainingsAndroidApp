package com.ldnprod.timer.Services

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import com.ldnprod.timer.PlayTrainingActivity
import com.ldnprod.timer.Services.Constants.*

object ServiceHelper {
    private val flag = PendingIntent.FLAG_UPDATE_CURRENT

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

    fun pausePendingIntent(context: Context, trainingId: Int): PendingIntent {
        val pauseIntent = Intent(context, TrainingService::class.java).apply {
            putExtra(TRAINING_STATE, TrainingService.State.Paused.name)
            putExtra(TRAINING_ID, trainingId)
        }
        return PendingIntent.getService(
            context, PAUSE_REQUEST_CODE, pauseIntent, flag
        )
    }

    fun playPendingIntent(context: Context, trainingId: Int): PendingIntent {
        val resumeIntent = Intent(context, TrainingService::class.java).apply {
            putExtra(TRAINING_STATE, TrainingService.State.Playing.name)
            putExtra(TRAINING_ID, trainingId)
        }
        return PendingIntent.getService(
            context, RESUME_REQUEST_CODE, resumeIntent, flag
        )
    }

    fun stopPendingIntent(context: Context, trainingId: Int): PendingIntent {
        val stopIntent = Intent(context, TrainingService::class.java).apply {
            putExtra(TRAINING_STATE, TrainingService.State.Stopped.name)
            putExtra(TRAINING_ID, trainingId)
        }
        return PendingIntent.getService(
            context, STOP_REQUEST_CODE, stopIntent, flag
        )
    }

    fun nextExercisePendingIntent(context: Context, trainingId: Int): PendingIntent {
        val nextIntent = Intent(context, TrainingService::class.java).apply {
            putExtra(TRAINING_STATE, TrainingService.State.Playing.name)
            putExtra(TRAINING_ID, trainingId)
        }
        return PendingIntent.getService(
            context, NEXT_REQUEST_CODE, nextIntent, flag
        )
    }

    fun triggerForegroundService(context: Context, action: String, trainingId: Int) {
        Intent(context, TrainingService::class.java).apply {
            this.action = action
            putExtra(TRAINING_ID, trainingId)
            context.startService(this)
        }
    }
}