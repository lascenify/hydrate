package com.example.hydrateyourself.sync

import android.content.Context
import com.example.hydrateyourself.util.NotificationUtils
import com.example.hydrateyourself.util.PreferenceUtilities

object ReminderTasks {

    // Intent actions
    const val ACTION_INCREMENT_WATER_COUNT  = "increment_water_action"
    const val ACTION_DISMISS_NOTIFICATION = "dismiss_notification_action"
    const val ACTION_CHARGING_REMINDER = "issue_charging_reminder_action"

    fun executeTask(mContext: Context, action:String){
        when (action){
            ACTION_INCREMENT_WATER_COUNT -> incrementWaterCount(mContext)
            ACTION_DISMISS_NOTIFICATION -> NotificationUtils.clearAllNotifications(mContext)
            ACTION_CHARGING_REMINDER -> issueChargingReminderNotification(mContext)
        }
    }

    private fun incrementWaterCount(mContext: Context){
        PreferenceUtilities.incrementWaterCount(context = mContext)
        NotificationUtils.clearAllNotifications(context = mContext)
    }


    private fun issueChargingReminderNotification(mContext: Context){
        NotificationUtils.remindUserBecauseCharging(mContext)
        PreferenceUtilities.incrementChargingReminderCount(mContext)
    }

}