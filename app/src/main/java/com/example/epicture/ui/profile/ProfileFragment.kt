package com.example.epicture.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.example.epicture.*
import com.example.epicture.http.AccountBase
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private var account = AccountBase(0,"","","",0,"", 0, false)
    private lateinit var username: String

    fun callBackGetUserDataResolve(data: AccountBase)
    {
        activity?.runOnUiThread{
            account = data
            textView.text = account.bio
            pointsText.text = account.reputation.toString()
            reputationText.text = account.reputation_name
            usernameDisplay.text = username
            Glide.with(this) .load(data.avatar).into(imageView3);
        }
    }

    fun callBackGetUserDataReject()
    {

    }

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



        /////////////////////////////// GET USER DATA ///////////////////
        if (account.id == 0) {
            val pseudo: String? = PreferenceManager.getDefaultSharedPreferences(App.context)
                .getString("account_username", "")
            if (pseudo != null) {
                username = pseudo
                ImgurAuth.getAccountBase({ res ->
                    callBackGetUserDataResolve(res)
                }, { callBackGetUserDataReject() }, pseudo)
            }
        }
        else {
            callBackGetUserDataResolve(account)
        }




        ////////////////////////////   BUTTON SWITCH BETWEEN MY PICTURE AND LIKED ///////////////////////
        val buttonMyPicture: ImageButton = returnValue.findViewById(R.id.myPictureButton)!!
        val buttonLikedPicture: ImageButton = returnValue.findViewById(R.id.likedPicture)!!
        val backMyPicture: ImageButton = returnValue.findViewById(R.id.backgroundButtonMyPicture)!!
        val backLikedPicture: ImageButton = returnValue.findViewById(R.id.backgroundButtonLikedPicture)!!

        buttonLikedPicture.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.buttonreleaseColor))
        buttonMyPicture.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.buttonSelectedColor))

        buttonLikedPicture.setOnClickListener {
            buttonMyPicture.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.buttonreleaseColor))
            buttonLikedPicture.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.buttonSelectedColor))
        }
        buttonMyPicture.setOnClickListener {
            buttonLikedPicture.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.buttonreleaseColor))
            buttonMyPicture.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.buttonSelectedColor))
        }
        backMyPicture.setOnClickListener {
            buttonLikedPicture.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.buttonreleaseColor))
            buttonMyPicture.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.buttonSelectedColor))
        }

        backLikedPicture.setOnClickListener {
            buttonLikedPicture.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.buttonSelectedColor))
            buttonMyPicture.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.buttonreleaseColor))
        }
        return returnValue
    }
}