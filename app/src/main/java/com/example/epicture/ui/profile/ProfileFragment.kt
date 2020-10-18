package com.example.epicture.ui.profile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.epicture.R
import com.example.epicture.SettingsActivity


class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        val returnValue : View? = inflater.inflate(R.layout.fragment_profile, container, false)
        val button: ImageButton = returnValue?.findViewById(R.id.buttonSetting)!!
        button.setOnClickListener {
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
        }
        val buttonMyPicture: ImageButton = returnValue?.findViewById(R.id.myPictureButton)!!
        val buttonLikedPicture: ImageButton = returnValue?.findViewById(R.id.likedPicture)!!
        buttonLikedPicture.setColorFilter(Color.GRAY)
        buttonMyPicture.setColorFilter(Color.WHITE)
        buttonLikedPicture.setOnClickListener {
            buttonMyPicture.setColorFilter(Color.GRAY)
            buttonLikedPicture.setColorFilter(Color.WHITE)
        }
        buttonMyPicture.setOnClickListener {
            buttonLikedPicture.setColorFilter(Color.GRAY)
            buttonMyPicture.setColorFilter(Color.WHITE)

        }

        return returnValue
    }
}