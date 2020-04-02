package com.example.hydrateyourself.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Build.VERSION_CODES.JELLY_BEAN
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.content.ContextCompat
import com.example.hydrateyourself.MainActivity
import com.example.hydrateyourself.R
import com.example.hydrateyourself.sync.ReminderTasks.ACTION_DISMISS_NOTIFICATION
import com.example.hydrateyourself.sync.ReminderTasks.ACTION_INCREMENT_WATER_COUNT
import com.example.hydrateyourself.sync.WaterReminderIntentService

object NotificationUtils {
    /**
     * This notification ID can be used to access our notification after we've displayed it. This
     * can be handy when we need to cancel the notification, or perhaps update it. This number is
     * arbitrary and can be set to whatever you like. 1138 is in no way significant.
     */
    private const val WATER_REMINDER_NOTIFICATION_ID = 1138
    /**
     * This pending intent id is used to uniquely reference the pending intent
     */
    private const val WATER_REMINDER_PENDING_INTENT_ID = 3417
    /**
     * This notification channel id is used to link notifications to this channel
     */
    private const val WATER_REMINDER_NOTIFICATION_CHANNEL_ID =
        "reminder_notification_channel"
    private const val ACTION_DRINK_PENDING_INTENT_ID = 1
    private const val ACTION_IGNORE_PENDING_INTENT_ID = 14


    /**
     * This function is called when the notification must be launched.
     * It has two actions: ok, increment water count or dismiss.
     */
    fun remindUserBecauseCharging(context: Context){
        // getting the notification manager
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // creating the notification channel using the not. manager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(WATER_REMINDER_NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.main_notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // creation of the notification
        val notificationBuilder = NotificationCompat.Builder(context, WATER_REMINDER_NOTIFICATION_CHANNEL_ID)
        notificationBuilder.color = ContextCompat.getColor(context, R.color.colorPrimary)
        notificationBuilder.setSmallIcon(R.drawable.ic_drink_notification)
        notificationBuilder.setLargeIcon(largeIcon(context))
        notificationBuilder.setContentTitle(context.resources.getString(R.string.charging_reminder_notification_title))
        notificationBuilder.setContentText(context.resources.getString(R.string.charging_reminder_notification_body))
        notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.charging_reminder_notification_body)))
        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE)
        notificationBuilder.setContentIntent(contentIntent(context))
        // we add the two actions to the notification
        notificationBuilder.addAction(drinkWaterAction(mContext = context))
        notificationBuilder.addAction(ignoreReminderAction(mContext = context))
        notificationBuilder.setAutoCancel(true)
        if (Build.VERSION.SDK_INT >= JELLY_BEAN &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            notificationBuilder.priority = PRIORITY_HIGH
        }

        // notify the notification manager and pass the notification built
        notificationManager.notify(WATER_REMINDER_NOTIFICATION_ID, notificationBuilder.build())
    }

    /**
     * Creates a pending intent for the notification created in remindUserBecauseCharging
     * @param context
     * @return the pending intent
     */
    private fun contentIntent (context: Context) : PendingIntent{
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
            context,
            WATER_REMINDER_PENDING_INTENT_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun largeIcon(context: Context):Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_drink_notification)


    /**
     * Clear all active notifications from this app
     */
    fun clearAllNotifications(context: Context){
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }


    /**
     * Action to ignore the notification
     */
    private fun ignoreReminderAction(mContext: Context) : NotificationCompat.Action{
        val intent = Intent(mContext, WaterReminderIntentService::class.java)
        intent.action = ACTION_DISMISS_NOTIFICATION
        val pendingIntent = PendingIntent.getService(
            mContext,
            ACTION_DRINK_PENDING_INTENT_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Action(
            R.drawable.ic_cancel_black_24px,
            "No, thanks",
            pendingIntent
        )
    }


    /**
     * Action to drink water
     */
    private fun drinkWaterAction(mContext: Context): NotificationCompat.Action{
        val intent = Intent(mContext, WaterReminderIntentService::class.java)
        intent.action = ACTION_INCREMENT_WATER_COUNT
        val pendingIntent = PendingIntent.getService(
            mContext,
            ACTION_IGNORE_PENDING_INTENT_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Action(
            R.drawable.ic_drink_notification,
            "Sure!",
            pendingIntent
        )
    }

}