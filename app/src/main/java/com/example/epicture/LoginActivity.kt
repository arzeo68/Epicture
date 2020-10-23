package com.example.epicture

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    var needToConnect: Boolean = false

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
        ImgurAuth.alreadyConnected({ accessApp() }, {
            runOnUiThread {
                needToConnect = true
                progressBar.visibility = View.GONE
                button.visibility = View.VISIBLE
            }
        })
    }

    private fun connection() {
        ImgurAuth.printCredentials()
        ImgurAuth.getToken(this)
    }

    private fun accessApp() {
        val button = findViewById<Button>(R.id.connection)
        val progressBar = findViewById<ProgressBar>(R.id.pBar)
        progressBar.visibility = View.VISIBLE
        button.visibility = View.GONE
        needToConnect = false
        Log.d("AUTH", "Access app...")
        ImgurAuth.savePreferences()
        val appIntent = Intent(applicationContext, MainActivity::class.java)
        startActivity(appIntent)
    }

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
                    ImgurAuth.saveToken(intent)
                    accessApp()
                }
            }
        }
    }
}