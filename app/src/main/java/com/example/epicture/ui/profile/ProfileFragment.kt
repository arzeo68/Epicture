package com.example.epicture.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.epicture.*
import com.example.epicture.http.AccountBase
import com.example.epicture.http.AlbumImage
import com.example.epicture.http.Gallery
import com.example.epicture.http.Image
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {

    private var isOnMyPicture: Boolean = true
    private var _viewList: RecyclerView? = null
    private lateinit var adapterUser: MyAdapterMyImage
    private lateinit var adapterFavorite: MyAdapterFavorite
    private lateinit var adapterInAlbum: MyAdapterInAlbum
    private lateinit var profileViewModel: ProfileViewModel
    private var account = AccountBase(0,"","","",0,"", 0, false)
    private var username: String = ""
    private lateinit var buttonArray: ArrayList<ImageButton>

    private fun getFavorite()
    {
        val pseudo: String? = PreferenceManager.getDefaultSharedPreferences(App.context)
            .getString("account_username", "")
        if (pseudo != null) {
            ImgurAuth.getFavorites({ res ->
                GetUserFavoriteResolve(res)
            }, { }, pseudo)
        }
    }

    private fun getUserImage()
    {
        val pseudo: String? = PreferenceManager.getDefaultSharedPreferences(App.context)
            .getString("account_username", "")
        if (pseudo != null) {
            ImgurAuth.getImagesByAccountAuth({ res ->
                getUserImagesResolve(res)
            }, { }, pseudo)
        }
    }

    private fun getAlbumImage(data: String?) {
        if (data != null) {
            Log.d("JHGFDDF", data)
        }
        val pseudo: String? = PreferenceManager.getDefaultSharedPreferences(App.context)
            .getString("account_username", "")
        if (pseudo != null) {
            if (data != null) {
                ImgurAuth.getAlbumImages({ res ->
                    getImagesInAlbumResolve(res)
                },
                    {Log.d("JHGFDDF", "call back failed")
                    }, data)
            }
        }
    }

    private fun switchMode()
    {
        _viewList?.removeAllViews()
        if (isOnMyPicture)
        {
            getUserImage()
            buttonArray[2].setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.buttonreleaseColor))
            buttonArray[0].setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.buttonSelectedColor))
        }
        else
        {
            getFavorite()
            buttonArray[0].setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.buttonreleaseColor))
            buttonArray[2].setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.buttonSelectedColor))
        }
    }

    private fun likeCallback(id: String?, type: String)
    {
        if (id != null) {
            ImgurAuth.putFavorite({putFavoriteResolve()}, {}, id, type)
        }
    }

    private fun getUserImagesResolve(data: List<Image>) {
        activity?.runOnUiThread {
            adapterUser = MyAdapterMyImage(requireContext(), data, {res, res2 ->likeCallback(res, res2)})
            _viewList?.adapter = adapterUser
        }
    }

    private fun putFavoriteResolve() {
    }

    private fun getImagesInAlbumResolve(data: List<AlbumImage>) {
        activity?.runOnUiThread {
            Log.d("JHGFDDF", "call back resolve")
            adapterInAlbum = MyAdapterInAlbum(requireContext(), data)
            _viewList?.adapter = adapterInAlbum
        }
    }

    private fun GetUserFavoriteResolve(data: List<Gallery>) {
        activity?.runOnUiThread {
            adapterFavorite = MyAdapterFavorite(requireContext(), data, {res ->getAlbumImage(res)})
            _viewList?.adapter = adapterFavorite
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun callBackGetUserDataResolve(data: AccountBase)
    {
        activity?.runOnUiThread {
            account = data
            descriptionText.text = account.bio
            pointsText.text = account.reputation.toString()
            reputationText.text = account.reputation_name
            usernameDisplay.text = username
            Glide.with(this).load(data.avatar).into(userProfileImage);
            userProfileImage.background = requireContext().resources.getDrawable(R.drawable.circle)
        }
    }

    private fun callBackGetUserDataReject()
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
        val viewManager = LinearLayoutManager(context)

        _viewList = returnValue?.findViewById<RecyclerView>(R.id.imageList)?.apply {
            setHasFixedSize(false)
            adapter = MyAdapterMyImage(requireContext(), null, {res, res2 ->likeCallback(res, res2)})
            layoutManager = viewManager
        }

        val button: ImageButton = returnValue?.findViewById(R.id.buttonSetting)!!
        button.setOnClickListener {
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
        }
        //////////////////////////// STYLE //////////////////////////////




        ////////////////////////////   BUTTON SWITCH BETWEEN MY PICTURE AND LIKED ///////////////////////
        isOnMyPicture = true
        val buttonMyPicture: ImageButton = returnValue.findViewById(R.id.myPictureButton)!!
        val buttonLikedPicture: ImageButton = returnValue.findViewById(R.id.likedPicture)!!
        val backMyPicture: ImageButton = returnValue.findViewById(R.id.backgroundButtonMyPicture)!!
        val backLikedPicture: ImageButton = returnValue.findViewById(R.id.backgroundButtonLikedPicture)!!
        buttonArray = ArrayList()
        buttonArray.add(buttonMyPicture)
        buttonArray.add(backMyPicture)
        buttonArray.add(buttonLikedPicture)
        buttonArray.add(backLikedPicture)


        buttonLikedPicture.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.buttonreleaseColor))
        buttonMyPicture.setColorFilter(ContextCompat.getColor(this.requireContext(), R.color.buttonSelectedColor))


        return returnValue
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        buttonArray[2].setOnClickListener {
            isOnMyPicture = false
            switchMode()
        }
        buttonArray[0].setOnClickListener {
            isOnMyPicture = true
            switchMode()
        }
        buttonArray[1].setOnClickListener {
            isOnMyPicture = true
            switchMode()
        }

        buttonArray[3].setOnClickListener {
            isOnMyPicture = false
            switchMode()
        }

        /////////////////////////////// GET USER DATA ///////////////////


        if (username == "") {
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
        switchMode()
    }
}