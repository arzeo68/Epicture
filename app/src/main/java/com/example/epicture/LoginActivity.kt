package com.example.epicture

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        ImgurAuth.alreadyConnected({ accessApp() }, { connection() })
        val button = findViewById<Button>(R.id.connection)
        button.setOnClickListener {
            connection()
        }
    }

    private fun connection() {
        ImgurAuth.printCredentials()
        ImgurAuth.getToken(this)
    }

    private fun accessApp() {
        Log.d("AUTH", "Access app...")
        ImgurAuth.savePreferences()
        val appIntent = Intent(applicationContext, MainActivity::class.java)
        startActivity(appIntent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        when (intent?.action) {
            Intent.ACTION_VIEW -> {
                if (intent.data != null) {
                    ImgurAuth.saveToken(intent)
                    accessApp()
                }
            }
        }
    }
}