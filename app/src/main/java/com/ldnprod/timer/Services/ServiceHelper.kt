package com.ldnprod.timer.Services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.ldnprod.timer.PlayTrainingActivity
import com.ldnprod.timer.Services.Constants.CLICK_REQUEST_CODE
import com.ldnprod.timer.Services.Constants.CANCEL_REQUEST_CODE
import com.ldnprod.timer.Services.Constants.RESUME_REQUEST_CODE
import com.ldnprod.timer.Services.Constants.STOP_REQUEST_CODE
import com.ldnprod.timer.Services.Constants.TRAINING_STATE

object ServiceHelper {
    private val flag =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PendingIntent.FLAG_IMMUTABLE
        else
            0
    fun clickPendingIntent(context: Context): PendingIntent {
        val clickIntent = Intent(context, PlayTrainingActivity::class.java)
        return PendingIntent.getActivity(
            context, CLICK_REQUEST_CODE, clickIntent, flag
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
    fun resumePendingIntent(context: Context): PendingIntent {
        val resumeIntent = Intent(context, TrainingService::class.java).apply {
            putExtra(TRAINING_STATE, TrainingService.State.Started.name)
        }
        return PendingIntent.getService(
            context, RESUME_REQUEST_CODE, resumeIntent, flag
        )
    }
    fun cancelPendingIntent(context: Context): PendingIntent {
        val cancelIntent = Intent(context, TrainingService::class.java).apply {
            putExtra(TRAINING_STATE, TrainingService.State.Canceled.name)
        }
        return PendingIntent.getService(
            context, CANCEL_REQUEST_CODE, cancelIntent, flag
        )
    }
    fun triggerForegroundService(context: Context, action: String) {
        Intent(context, TrainingService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }
}