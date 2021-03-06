package com.example.epicture.activities.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.epicture.ImgurAuth
import com.example.epicture.R
import com.example.epicture.activities.MainActivity

/**
 * This activity is the one called when the user run the application or when the user logged out
 * It allows the user to connect or if he was already connected verify if his credentials are still good
 */
class LoginActivity : AppCompatActivity() {
    var needToConnect: Boolean = false

    /**
     * onCreate is override and called when the activity is starting
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val button = findViewById<Button>(R.id.connection)
        button.setOnClickListener {
            connection()
        }

        val progressBar = findViewById<ProgressBar>(R.id.pBar)
        progressBar.visibility = View.VISIBLE
        button.visibility = View.GONE
        try { ImgurAuth.alreadyConnected({ try {accessApp()
        } catch (e:Exception) {
            e.printStackTrace()
        }}, {
            try {
            runOnUiThread {
                needToConnect = true
                progressBar.visibility = View.GONE
                button.visibility = View.VISIBLE
            }
            } catch (e:Exception) {
                e.printStackTrace()
            }
        })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Allow the user to connect to the app and enter his credentials on Imgur thanks to his navigator
     */
    private fun connection() {
        try { ImgurAuth.printCredentials()
        } catch (e:Exception) {
            e.printStackTrace()
        }
        try { ImgurAuth.getToken(this)
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Allow the user to access the rest of the App if he logged in well
     */
    private fun accessApp() {
        val button = findViewById<Button>(R.id.connection)
        val progressBar = findViewById<ProgressBar>(R.id.pBar)
        progressBar.visibility = View.VISIBLE
        button.visibility = View.GONE
        needToConnect = false
        try { ImgurAuth.savePreferences()
        } catch (e:Exception) {
            e.printStackTrace()
        }
        val appIntent = Intent(applicationContext, MainActivity::class.java)
        startActivity(appIntent)
    }

    /**
     * onNewIntent is override and called when the user finished to enter is credentials on Imgur
     * It's calling the accessApp method if he's well connected
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        runOnUiThread {
            val progressBar = findViewById<ProgressBar>(R.id.pBar)
            val button = findViewById<Button>(R.id.connection)
            button.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
        when (intent?.action) {
            Intent.ACTION_VIEW -> {
                if (intent.data != null) {
                    try {
                        ImgurAuth.saveToken(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    accessApp()
                }
            }
        }
    }
}