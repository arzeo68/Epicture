package com.example.epicture

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        // TODO: call pour voir si les credentials sont bonnes et passer au home si success

        ImgurAuth.alreadyConnected ({ accessApp() }, { connection() })

        val button = findViewById<Button>(R.id.connection)
        button.setOnClickListener {
            connection()
        }
    }

    private fun connection() {
        ImgurAuth.getToken(this)
    }

    private fun accessApp() {
        Log.d("AUTH", "Access app...")
        val appIntent = Intent(applicationContext, MainActivity::class.java)
        startActivity(appIntent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        when (intent?.action) {
            Intent.ACTION_VIEW -> {
                if (intent.data != null) {
                    ImgurAuth.saveToken(intent)
                    ImgurAuth.printCredentials()
                    accessApp()
                }
            }
        }
    }
}