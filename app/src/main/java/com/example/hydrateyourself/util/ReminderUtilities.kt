package com.example.hydrateyourself.util

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.hydrateyourself.sync.WaterReminderWorker
import java.util.concurrent.TimeUnit

object ReminderUtilities {
    val REMINDER_INTERVAL_SECONDS = 900L
    val SYNC_FLEXTIME_SECONDS = 900L
    val REMINDER_JOB_TAG = "hydration_reminder_tag"

    // will store whether the job has been activated or not
    private var sInitialized = false

    @Synchronized
    fun scheduleChargingReminder(mContext:Context){
        if (sInitialized)
            return
        val workManager = WorkManager.getInstance(mContext)
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .build()
        val workRequest = PeriodicWorkRequest.Builder(WaterReminderWorker::class.java, REMINDER_INTERVAL_SECONDS, TimeUnit.SECONDS)
            .setConstraints(constraints)
            .build()
        workManager.enqueueUniquePeriodicWork(REMINDER_JOB_TAG, ExistingPeriodicWorkPolicy.REPLACE, workRequest)
        sInitialized = true

    }
}