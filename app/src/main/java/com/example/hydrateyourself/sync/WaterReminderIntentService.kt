package com.example.hydrateyourself.sync

import android.app.IntentService
import android.content.Intent

/**
 * This is an intent service used to execute task on a background thread
 */
class WaterReminderIntentService  : IntentService("WaterReminderIntentService"){

    /**
     * very similar to doInBackground of AsyncTask. Calls the ReminderTasks method to
     * execute either the drink water task or the dismiss notification task
     * */

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null)
            ReminderTasks.executeTask(applicationContext, intent.action!!)
    }
}