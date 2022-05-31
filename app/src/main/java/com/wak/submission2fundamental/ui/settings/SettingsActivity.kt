package com.wak.submission2fundamental.ui.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.wak.submission2fundamental.R
import com.wak.submission2fundamental.broadcastreceiver.ReminderReceiver
import com.wak.submission2fundamental.databinding.ActivitySettingsBinding


class SettingsActivity : AppCompatActivity(), View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {

    companion object {
        private const val REMINDER_PREFS = "reminder_prefs"
        private const val STATE = "state"
    }


    private lateinit var settingsBinding: ActivitySettingsBinding
    private lateinit var reminderReceiver: ReminderReceiver
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(settingsBinding.root)
        checkTheme()

        reminderReceiver = ReminderReceiver()
        sharedPreferences = getSharedPreferences(REMINDER_PREFS, Context.MODE_PRIVATE)

        checkStateSwicth()
        settingsBinding.localizationSettings.setOnClickListener(this)
        settingsBinding.switchReminder.setOnCheckedChangeListener(this)

        settingsBinding.btnChangeTheme.setOnClickListener{chooseThemeDialog()}

        supportActionBar?.title = "Settings"
    }

    private fun chooseThemeDialog() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.choose_theme_text))
        val styles = arrayOf("Light", "Dark", "System default")
        val checkedItem = MyPreferences(this).darkMode

        builder.setSingleChoiceItems(styles, checkedItem) { dialog, which ->

            when (which) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    MyPreferences(this).darkMode = 0
                    delegate.applyDayNight()
                    dialog.dismiss()
                }
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    MyPreferences(this).darkMode = 1
                    delegate.applyDayNight()
                    dialog.dismiss()
                }
                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    MyPreferences(this).darkMode = 2
                    delegate.applyDayNight()
                    dialog.dismiss()
                }

            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun checkTheme() {
        when (MyPreferences(this).darkMode) {
            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                delegate.applyDayNight()
            }
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                delegate.applyDayNight()
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                delegate.applyDayNight()
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.localization_settings -> {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
            }

        }
    }
    private fun checkStateSwicth() {
        settingsBinding.switchReminder.isChecked = sharedPreferences.getBoolean(STATE, false)
    }

    override fun onCheckedChanged(cb: CompoundButton?, bool: Boolean) {
        if (bool) {
            reminderReceiver.setRepeatingReminder(this)
        } else {
            reminderReceiver.cancelReminder(this)
        }
        saveReminderState(bool)
    }

    private fun saveReminderState(state: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(STATE, state)
        editor.apply()
    }


}

class MyPreferences(context: Context?) {

    companion object {
        private const val DARK_STATUS = "io.github.manuelernesto.DARK_STATUS"
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var darkMode = preferences.getInt(DARK_STATUS, 0)
    set(value) = preferences.edit().putInt(DARK_STATUS, value).apply()

}



