package com.dicoding.courseschedule.ui.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.notification.DailyReminder
import com.dicoding.courseschedule.util.NightMode

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var listPreferenceValue: String

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        //TODO 10 : Update theme based on value in ListPreference
        val listPreference = findPreference<ListPreference>(getString(R.string.pref_key_dark))
        val prefNotification = findPreference<SwitchPreference>(getString(R.string.pref_key_notify))
        val dailyReceiver = DailyReminder()


        listPreference?.onPreferenceChangeListener = object :
            Preference.OnPreferenceChangeListener {
            override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
                if (preference.key == getString(R.string.pref_key_dark)) {
                    when (newValue.toString().uppercase()) {
                        NightMode.AUTO.toString() -> updateTheme(NightMode.AUTO.value)
                        NightMode.ON.toString() -> updateTheme(NightMode.ON.value)
                        NightMode.OFF.toString() -> updateTheme(NightMode.OFF.value)
                    }
                    return true
                }
                return false
            }
        }
        //TODO 11 : Schedule and cancel notification in DailyReminder based on SwitchPreference
        prefNotification?.setOnPreferenceChangeListener { _, newValue ->
            if(newValue as Boolean){
                dailyReceiver.setDailyReminder(requireContext())
            } else {
                dailyReceiver.cancelAlarm(requireContext())
            }

            true
        }
    }

    private fun updateTheme(nightMode: Int): Boolean {
        AppCompatDelegate.setDefaultNightMode(nightMode)
        requireActivity().recreate()
        return true
    }
}