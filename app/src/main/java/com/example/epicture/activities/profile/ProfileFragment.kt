package com.example.epicture.activities.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import com.example.epicture.App
import com.example.epicture.ImgurAuth
import com.example.epicture.R
import com.example.epicture.services.http.AccountBase
import com.example.epicture.services.http.AlbumImage
import com.example.epicture.services.http.Gallery
import com.example.epicture.services.http.Image
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * this class handle all the interaction with the profile page
 */
class ProfileFragment : Fragment() {

    private var isOnMyPicture: Boolean = true
    private var _viewList: RecyclerView? = null
    private lateinit var adapterUser: MyAdapterMyImage
    private lateinit var adapterFavorite: MyAdapterFavorite
    private lateinit var adapterInAlbum: MyAdapterInAlbum
    private var account = AccountBase(0, "", "", "", 0, "", 0, false)
    private var username: String = ""
    private lateinit var buttonArray: ArrayList<ImageButton>
    private lateinit var myRefreshLayout: SwipeRefreshLayout
    /**
     * this function is called when the user wanna delete an image
     *  @param id id of the image
     *  @param dataSource "image" or "album" depending on the type of deletion
     */
    private fun unlikeButtonCallBack(id: String?, type: String) {
        if (id != null) {
            ImgurAuth.putFavorite({
                try {
                    unlikeResolve()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, {}, id, type)
        }
    }

    /**
     * this function call the api to get the user favorite
     */
    private fun getFavorite() {
        val pseudo: String? = PreferenceManager.getDefaultSharedPreferences(App.context)
            .getString("account_username", "")
        if (pseudo != null) {
            try {
                ImgurAuth.getFavorites({ res ->
                    try {
                        GetUserFavoriteResolve(res)
                        myRefreshLayout.isRefreshing = false
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, {
                    try {
                        myRefreshLayout.isRefreshing = false
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, pseudo)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    /**
     * this function call the api to get the user image
     */
    private fun getUserImage() {
        val pseudo: String? = PreferenceManager.getDefaultSharedPreferences(App.context)
            .getString("account_username", "")
        if (pseudo != null) {
            ImgurAuth.getImagesByAccountAuth({ res ->
                try {
                    getUserImagesResolve(res)
                    myRefreshLayout.isRefreshing = false
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, {
                try {
                    myRefreshLayout.isRefreshing = false
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, pseudo)
        }
    }
    /**
     * this function call the api to get all the image contain in an album
     * @param data id of the album
     */
    private fun getAlbumImage(data: String?) {
        myRefreshLayout.isRefreshing = true
        if (data != null) {
        }
        val pseudo: String? = PreferenceManager.getDefaultSharedPreferences(App.context)
            .getString("account_username", "")
        if (pseudo != null) {
            if (data != null) {
                ImgurAuth.getAlbumImages(
                    { res ->
                        try {
                            getImagesInAlbumResolve(res)
                            myRefreshLayout.isRefreshing = false
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    {
                        try {
                            myRefreshLayout.isRefreshing = false
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, data
                )
            }
        }
    }
    /**
     * this function switch between user favorite and user post and call the right api function
     */
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
    /**
     * this function is called when the user wanna delete something
     * @param id id of what you want to delete
     * @param type "image" or "album" depending of what you want to delete
     */
    private fun deleteCallBack(id: String?, type: String) {
        if (id != null) {
            ImgurAuth.deleteImageOrAlbum({
                try {
                    deleteResolve()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, {}, type, username, id)
        }
    }
    /**
     * this function is the resolved callback called when you get the user image
     * @param data list of the user image
     */
    private fun getUserImagesResolve(data: List<Image>) {
        activity?.runOnUiThread {
            adapterUser =
                MyAdapterMyImage(requireContext(), data, { res, res2 -> deleteCallBack(res, res2) })
            _viewList?.adapter = adapterUser
        }
    }
    /**
     * this function is the resolved callback called when you delete something
     */
    private fun deleteResolve() {
        activity?.runOnUiThread {
            switchMode()
        }
    }
    /**
     * this function is the resolved callback called when you like or unlike something
     */
    private fun unlikeResolve() {
        activity?.runOnUiThread {
            switchMode()
        }
    }
    /**
     * this function is the resolved callback called when you get all image in an album
     * @param data list of all the image in the album
     */
    private fun getImagesInAlbumResolve(data: List<AlbumImage>) {
        activity?.runOnUiThread {
            adapterInAlbum = MyAdapterInAlbum(requireContext(), data)
            _viewList?.adapter = adapterInAlbum
        }
    }
    /**
     * this function is the resolved callback called when you get the user favorite images
     * @param data list of all the user favorite images
     */
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

    /**
     * this function is the resolved callback called when you get the user information
     * @param data user information
     */
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
    /**
     * this function is the reject callback called when you get the user information
     */
    private fun callBackGetUserDataReject() {

    }
    /**
     * this function init the view
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
    /**
     * this function is called when the view is created
     * @param view view
     * @param bundle bundle
     */
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
                    try {
                        callBackGetUserDataResolve(res)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }, {
                    try {
                        callBackGetUserDataReject()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }, pseudo)
            }
        } else {
            callBackGetUserDataResolve(account)
        }
        switchMode()
    }
    /**
     * this function is called when the user update his information
     */
    override fun onResume() {
        super.onResume()
        val pseudo: String? = PreferenceManager.getDefaultSharedPreferences(App.context)
            .getString("account_username", "")
        if (pseudo != null) {
            username = pseudo
            ImgurAuth.getAccountBase({ res ->
                try {
                    callBackGetUserDataResolve(res)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }, {
                try {
                    callBackGetUserDataReject()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }, pseudo)
        }
    }
}