# hydrate
Hydration reminder app written in Kotlin in order to learn good practices in background tasks in Android Development.

## Background tasks

Background tasks have been implemented using [WorkManager](https://developer.android.com/jetpack/androidx/releases/work?hl=en) and a BroadcastReceiver.

- **Drink water reminder**: reminder each 15 minutes while the device is charging to drink water.
  1. [WaterReminderWorker](app/src/main/java/com/example/hydrateyourself/sync/WaterReminderWorker.kt) calls the reminder task on the background thread.
  2. [WaterReminderIntentService](app/src/main/java/com/example/hydrateyourself/sync/WaterReminderIntentService.kt) handles the action of incrementing the water count on the background, triggered by both notification actions and button clicks.
  3. [ReminderTasks](app/src/main/java/com/example/hydrateyourself/sync/ReminderTasks.kt) is a Kotlin object which has both incrementWaterCount and issueChargingReminderNotification tasks
  4. [NotificationUtils](app/src/main/java/com/example/hydrateyourself/util/NotificationUtils.kt) contains util function to set up notifications
  
- [**Charging Broadcast Receiver**](app/src/main/java/com/example/hydrateyourself/ui/HydrationFragment.kt): dynamic Broadcast Receiver (gets registered within the onResume method and unregistered in the onPause method). Waits to be triggered when the device gets connected or disconnected to the power. It updates an imageView on the UI changing its color.
  
