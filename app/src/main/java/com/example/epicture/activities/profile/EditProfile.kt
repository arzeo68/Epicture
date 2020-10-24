package com.example.epicture.activities.profile

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.example.epicture.App
import com.example.epicture.ImgurAuth
import com.example.epicture.R

/**
 * This Activity is called when the user want to edit is profile
 * It allows the user to change his username or/and his bio
 */
class EditProfile : AppCompatActivity() {
    private var name: String = ""
    private var avatar: String = ""
    private var bio: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        val intent = intent.extras
        avatar = intent?.getString("avatar").toString()
        bio = intent?.getString("bio").toString()
        name = intent?.getString("username").toString()
        var nameInput =
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.name_input)
        var bioInput =
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.bio_input)
        val userImage = findViewById<ImageView>(R.id.userProfileImageEdit)

        nameInput.setText(name)
        bioInput.setText(bio)
        Glide.with(this).load(avatar).into(userImage);
        val cancel: TextView = findViewById(R.id.cancel_button)
        cancel.setOnClickListener {
            finish()
        }

        val done: TextView = findViewById(R.id.done_button)
        done.setOnClickListener {
            nameInput = findViewById(R.id.name_input)
            bioInput = findViewById(R.id.bio_input)
            ImgurAuth.changeAccountSettings({
                val prefs = PreferenceManager.getDefaultSharedPreferences(App.context).edit()
                prefs.putString("account_username", nameInput.text.toString())
                prefs.apply()
                ImgurAuth.setUsername(nameInput.text.toString())
                finish()
            }, {}, name, mapOf(
                "bio" to bioInput.text.toString(),
                "username" to nameInput.text.toString()
            )
            )
        }
    }
}