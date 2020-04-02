package com.example.hydrateyourself.ui

import android.content.*
import android.content.Context.BATTERY_SERVICE
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.hydrateyourself.R
import com.example.hydrateyourself.databinding.HydrationFragmentBinding
import com.example.hydrateyourself.sync.ReminderTasks
import com.example.hydrateyourself.sync.WaterReminderIntentService
import com.example.hydrateyourself.util.NotificationUtils
import com.example.hydrateyourself.util.PreferenceUtilities


class HydrationFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener{

    private lateinit var binding : HydrationFragmentBinding
    private var mToast:Toast? = null

    private val chargingBroadcastReceiver = ChargingBroadcastReceiver()
    private val intentFilter = IntentFilter()

        companion object{
        lateinit var powerIncrementIv: ImageView
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.hydration_fragment, container, false)
        powerIncrementIv = binding.ivPowerIncrement
        binding.ibWaterIncrement.setOnClickListener { incrementWater() }
        binding.bTestNotifications.setOnClickListener { testNotification() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /** Set the original values in the UI  **/
        updateWaterCount()
        updateChargingReminderCount()

        /** Setup the shared preference listener  **/
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.registerOnSharedPreferenceChangeListener(this)

        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED)
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)

    }

    /**
     * It is a dynamic broadcast receiver, so we register it within the resume method
     * and unregister it in the onPause method
     */
    override fun onResume() {
        super.onResume()
        /** Determine current charging status */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val batteryManager = activity!!.getSystemService(BATTERY_SERVICE) as BatteryManager?
            showCharging(batteryManager!!.isCharging)
        } else {
            val newIntentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val currentBatteryStatusIntent = activity?.registerReceiver(null, newIntentFilter)
            val batteryStatus = currentBatteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging = batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING || batteryStatus == BatteryManager.BATTERY_STATUS_FULL
            showCharging(isCharging)
        }
        /** Register the receiver*/
        activity!!.registerReceiver(chargingBroadcastReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        activity!!.unregisterReceiver(chargingBroadcastReceiver)
    }

    /**
     * Updates the UI to show the water counter
     */
    private fun updateWaterCount(){
        binding.tvWaterCount.text = PreferenceUtilities.getWaterCount(context).toString()
    }

    /**
     * Updates the UI to show the reminder counter
     */
    private fun updateChargingReminderCount(){
        val chargingReminders = PreferenceUtilities.getChargingReminderCount(context)
        val formattedChargingReminders = resources.getQuantityString(
            R.plurals.charge_notification_count, chargingReminders, chargingReminders
        )
        binding.tvChargingReminderCount.text = formattedChargingReminders

    }


    /**
     * Called when the user clicks the water icon inside the application.
     * Launches the Intent Service with the action of incrementing water count
     */
    private fun incrementWater() {
        if (mToast != null)
            mToast?.cancel()
        mToast = Toast.makeText(context, R.string.water_chug_toast, Toast.LENGTH_SHORT)
        mToast?.show()
        val intent = Intent(context, WaterReminderIntentService::class.java)
        intent.action = ReminderTasks.ACTION_INCREMENT_WATER_COUNT
        context?.startService(intent)
    }


    /**
     * Method used to test the notification system
     */
    private fun testNotification (){
        NotificationUtils.remindUserBecauseCharging(context!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    /**
     * Called when shared preferences change in order to update the ui
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (PreferenceUtilities.KEY_WATER_COUNT == key)
            updateWaterCount()
        else if (PreferenceUtilities.KEY_CHARGING_REMINDER_COUNT == key)
            updateChargingReminderCount()

    }

    /**
     * Updates the UI if the device starts charging or has just been disconnected
     */
    fun showCharging(isCharging:Boolean){
        if (isCharging)
            powerIncrementIv.setImageResource(R.drawable.ic_power_pink_80px)
        else
            powerIncrementIv.setImageResource(R.drawable.ic_power_grey_80px)
    }

    /**
     * Broadcast receiver registered to the actions
     * ACTION_POWER_CONNECTED and ACTION_POWER_DISCONNECTED
     */
    private inner class ChargingBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent!!.action
            showCharging(isCharging = action == Intent.ACTION_POWER_CONNECTED)
        }
    }

}


