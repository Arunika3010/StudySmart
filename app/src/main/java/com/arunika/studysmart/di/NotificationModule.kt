package com.arunika.studysmart.di

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.arunika.studysmart.R
import com.arunika.studysmart.presentation.session.ServiceHelper
import com.arunika.studysmart.util.Constants.NOTIFICATION_CHANNEL_ID
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
	fun provideNotificationService(
		@ApplicationContext context: Context
	): NotificationManager {
		return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
	}

	@ServiceScoped
	@Provides
	fun provideNotificationBuilder(
		@ApplicationContext context: Context
	): NotificationCompat.Builder {
		return NotificationCompat
			.Builder(context, NOTIFICATION_CHANNEL_ID)
			.setContentTitle("Study Session")
			.setContentText("00:00:00")
			.setSmallIcon(R.drawable.ic_launcher_foreground)
			.setOngoing(true)
			.setContentIntent(ServiceHelper.clickPendingIntent(context))

	}
}