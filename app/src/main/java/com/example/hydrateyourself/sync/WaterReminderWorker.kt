package com.example.hydrateyourself.sync

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture

class WaterReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result = try {
        ReminderTasks.executeTask(
            this.applicationContext,
            ReminderTasks.ACTION_CHARGING_REMINDER
        )
        Result.success()
    } catch (throwable:Throwable) {
        Log.e("ERROR_REMINDER", "Error executing reminder", throwable)
        Result.failure()
    }
}