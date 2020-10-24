package com.example.epicture.activities.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.epicture.*
import com.example.epicture.services.http.AccountBase
import com.example.epicture.services.http.AlbumImage
import com.example.epicture.services.http.Gallery
import com.example.epicture.services.http.Image
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {

    private var isOnMyPicture: Boolean = true
    private var _viewList: RecyclerView? = null
    private lateinit var adapterUser: MyAdapterMyImage
    private lateinit var adapterFavorite: MyAdapterFavorite
    private lateinit var adapterInAlbum: MyAdapterInAlbum
    private lateinit var profileViewModel: ProfileViewModel
    private var account = AccountBase(0, "", "", "", 0, "", 0, false)
    private var username: String = ""
    private lateinit var buttonArray: ArrayList<ImageButton>
    private lateinit var myRefreshLayout: SwipeRefreshLayout

    private fun unlikeButtonCallBack(id: String?, type: String) {
        if (id != null) {
            ImgurAuth.putFavorite({ unlikeResolve() }, {}, id, type)
        }
    }


    private fun getFavorite() {
        val pseudo: String? = PreferenceManager.getDefaultSharedPreferences(App.context)
            .getString("account_username", "")
        if (pseudo != null) {
            ImgurAuth.getFavorites({ res ->
                GetUserFavoriteResolve(res)
                myRefreshLayout.isRefreshing = false
            }, {
                myRefreshLayout.isRefreshing = false
            }, pseudo)
        }
    }

    private fun getUserImage() {
        val pseudo: String? = PreferenceManager.getDefaultSharedPreferences(App.context)
            .getString("account_username", "")
        if (pseudo != null) {
            ImgurAuth.getImagesByAccountAuth({ res ->
                getUserImagesResolve(res)
                myRefreshLayout.isRefreshing = false
            }, {
                myRefreshLayout.isRefreshing = false
            }, pseudo)
        }
    }

    private fun getAlbumImage(data: String?) {
        myRefreshLayout.isRefreshing = true
        if (data != null) {
            Log.d("JHGFDDF", data)
        }
        val pseudo: String? = PreferenceManager.getDefaultSharedPreferences(App.context)
            .getString("account_username", "")
        if (pseudo != null) {
            if (data != null) {
                ImgurAuth.getAlbumImages(
                    { res ->
                        getImagesInAlbumResolve(res)
                        myRefreshLayout.isRefreshing = false
                    },
                    {
                        myRefreshLayout.isRefreshing = false
                        Log.d("JHGFDDF", "call back failed")
                    }, data
                )
            }
        }
    }

    private fun switchMode() {
        myRefreshLayout.isRefreshing = true
        _viewList?.removeAllViews()
        if (isOnMyPicture) {
            getUserImage()
            buttonArray[2].setColorFilter(
                ContextCompat.getColor(
                    this.requireContext(),
                    R.color.buttonreleaseColor
                )
            )
            buttonArray[0].setColorFilter(
                ContextCompat.getColor(
                    this.requireContext(),
                    R.color.buttonSelectedColor
                )
            )
        } else {
            getFavorite()
            buttonArray[0].setColorFilter(
                ContextCompat.getColor(
                    this.requireContext(),
                    R.color.buttonreleaseColor
                )
            )
            buttonArray[2].setColorFilter(
                ContextCompat.getColor(
                    this.requireContext(),
                    R.color.buttonSelectedColor
                )
            )
        }
    }

    private fun deleteCallBack(id: String?, type: String) {
        if (id != null) {
            ImgurAuth.deleteImageOrAlbum({ deleteResolve() }, {}, type, username, id)
        }
    }

    private fun getUserImagesResolve(data: List<Image>) {
        activity?.runOnUiThread {
            adapterUser =
                MyAdapterMyImage(requireContext(), data, { res, res2 -> deleteCallBack(res, res2) })
            _viewList?.adapter = adapterUser
        }
    }

    private fun deleteResolve() {
        activity?.runOnUiThread {
            switchMode()
        }
    }

    private fun unlikeResolve() {
        activity?.runOnUiThread {
            switchMode()
        }
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
            adapterFavorite = MyAdapterFavorite(
                requireContext(),
                data,
                { res -> getAlbumImage(res) },
                { res, res2 -> unlikeButtonCallBack(res, res2) })
            _viewList?.adapter = adapterFavorite
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun callBackGetUserDataResolve(data: AccountBase) {
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

    private fun callBackGetUserDataReject() {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        val returnValue: View? = inflater.inflate(R.layout.fragment_profile, container, false)
        val viewManager = LinearLayoutManager(context)

        myRefreshLayout = returnValue?.findViewById(R.id.refreshLayout)!!
        myRefreshLayout.setOnRefreshListener { switchMode() }

        _viewList = returnValue?.findViewById<RecyclerView>(R.id.imageList)?.apply {
            setHasFixedSize(false)
            adapter =
                MyAdapterMyImage(requireContext(), null, { res, res2 -> deleteCallBack(res, res2) })
            layoutManager = viewManager
        }

        val edit: Button = returnValue?.findViewById(R.id.button_edit)!!
        edit.setOnClickListener {
            val intent = Intent(App.context, EditProfile::class.java)
            intent.putExtra("avatar", account.avatar.toString())
            intent.putExtra("username", username)
            intent.putExtra("bio", account.bio.toString())
            startActivity(intent)
        }
        val button: ImageButton = returnValue.findViewById(R.id.buttonSetting)!!
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
        val backLikedPicture: ImageButton =
            returnValue.findViewById(R.id.backgroundButtonLikedPicture)!!
        buttonArray = ArrayList()
        buttonArray.add(buttonMyPicture)
        buttonArray.add(backMyPicture)
        buttonArray.add(buttonLikedPicture)
        buttonArray.add(backLikedPicture)


        buttonLikedPicture.setColorFilter(
            ContextCompat.getColor(
                this.requireContext(),
                R.color.buttonreleaseColor
            )
        )
        buttonMyPicture.setColorFilter(
            ContextCompat.getColor(
                this.requireContext(),
                R.color.buttonSelectedColor
            )
        )


        return returnValue
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
        } else {
            callBackGetUserDataResolve(account)
        }
        switchMode()
    }

    override fun onResume() {
        super.onResume()
        val pseudo: String? = PreferenceManager.getDefaultSharedPreferences(App.context)
            .getString("account_username", "")
        if (pseudo != null) {
            username = pseudo
            ImgurAuth.getAccountBase({ res ->
                callBackGetUserDataResolve(res)
            }, { callBackGetUserDataReject() }, pseudo)
        }
    }
}