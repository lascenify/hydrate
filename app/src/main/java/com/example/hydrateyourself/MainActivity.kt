package com.example.hydrateyourself

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hydrateyourself.util.ReminderUtilities

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scheduleChargingReminder()
    }

    private fun scheduleChargingReminder(){
        ReminderUtilities.scheduleChargingReminder(applicationContext)
    }



}
