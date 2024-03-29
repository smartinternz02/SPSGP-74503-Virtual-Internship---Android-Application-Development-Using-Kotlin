package com.himanshu.android.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.himanshu.android.R
import com.himanshu.android.utils.Constants.CONFIRMED
import com.himanshu.android.utils.Constants.TO_RECEIVE
import com.himanshu.android.view.ui.home.HomeActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class NotificationManager @Inject constructor(
    private val context: Context,
    private val builder: NotificationCompat.Builder
    ) {

    fun createNotification(status: String, activity: Class<HomeActivity>) {
        val notificationId = (0..1000).random()
        when (status) {
            CONFIRMED -> {
                setupNotification(
                    CONFIRMED,
                    context.getString(R.string.confirmed_notification_content),
                    notificationId,
                    activity
                )
            }
            TO_RECEIVE -> {
                setupNotification(
                    TO_RECEIVE,
                    context.getString(R.string.to_receive_notification_content),
                    notificationId,
                    activity
                )
            }
        }

    }

    private fun setupNotification(title: String,
                                  content: String,
                                  id: Int,
                                  activity: Class<HomeActivity>) {
        val bundle = bundleOf("status" to title)
        val notification = builder
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(initPendingIntent(bundle, activity))
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(context).notify(id, notification)
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID,
                Constants.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                lightColor = Color.GREEN
                enableLights(true)
            }
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun initPendingIntent(bundle: Bundle, activity: Class<HomeActivity>): PendingIntent {
        return NavDeepLinkBuilder(context)
            .setGraph(R.navigation.nav_main)
            .setDestination(R.id.ordersFragment)
            .setComponentName(activity)
            .setArguments(bundle)
            .createPendingIntent()
    }

}