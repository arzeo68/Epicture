package com.example.epicture.activities.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.example.epicture.App
import com.example.epicture.ImgurAuth
import com.example.epicture.R
import com.example.epicture.activities.login.LoginActivity

/**
 * The settings activity deal with the settings page where the user can logout
 */
class SettingsActivity : AppCompatActivity() {

    /**
     * onCreate override and called when the activity is starting
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val button = findViewById<Button>(R.id.logout)
        button.setOnClickListener {
            ImgurAuth.logout()
            val intent = Intent(App.context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    /**
     * Basic settings fragment
     */
    class SettingsFragment : PreferenceFragmentCompat() {
        /**
         * onCreatePreferences override called when the settings activity is starting
         */
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}