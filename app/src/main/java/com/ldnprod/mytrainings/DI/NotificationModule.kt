package com.ldnprod.mytrainings.DI

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.ldnprod.mytrainings.R
import com.ldnprod.mytrainings.Services.Constants.NOTIFICATION_CHANNEL_ID
import com.ldnprod.mytrainings.Services.ServiceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object NotificationModule {

    @ServiceScoped
    @Provides
    fun provideNotificationBuilder(
        @ApplicationContext context: Context
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Training")
            .setContentText("Exercise 00:00")
            .setSmallIcon(R.drawable.ic_app_icon)
            .setOngoing(true)
            .addAction(0, "Pause", ServiceHelper.pausePendingIntent(context, -1))
            .addAction(0, "Stop", ServiceHelper.stopPendingIntent(context, -1))
            .setContentIntent(ServiceHelper.clickPendingIntent(context, -1))
    }
    @ServiceScoped
    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}