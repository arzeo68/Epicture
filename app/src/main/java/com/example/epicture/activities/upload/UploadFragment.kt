package com.example.epicture.activities.upload

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.epicture.ImgurAuth
import com.example.epicture.R
import kotlinx.android.synthetic.main.fragment_upload.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.net.URI

/**
 * This fragment is called when the user is on the upload page which allow him to upload images on Imgur
 */
class UploadFragment : Fragment() {
    private var image: Boolean = false
    private lateinit var imagePath: URI
    private lateinit var bitmapImage: Bitmap
    private lateinit var myRefreshLayout: SwipeRefreshLayout
    private lateinit var uploadViewModel: UploadViewModel

    /**
     * onCreateView override called when the user is on the upload view
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        uploadViewModel = ViewModelProviders.of(this).get(UploadViewModel::class.java)
        val res = inflater.inflate(R.layout.fragment_upload, container, false)
        return res
    }

    /**
     * onViewCreated override called when the user is on the upload view and when this one has been created
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myRefreshLayout = refreshLayout2
        myRefreshLayout.setOnRefreshListener {
            myRefreshLayout.isRefreshing = false
        }

        upload_button.setOnClickListener {
            if (image) {
                myRefreshLayout.isRefreshing = true
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                val encoded: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                Log.d("UPLOAD", encoded)
                ImgurAuth.uploadImage({
                    try {
                    activity?.runOnUiThread {
                        selected_image.setImageDrawable(null)
                        upload_title.text?.clear()
                        upload_description.text?.clear()
                        upload_description.clearFocus()
                        upload_title.clearFocus()
                        myRefreshLayout.isRefreshing = false
                    }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, {
                    try {
                    myRefreshLayout.isRefreshing = false
                    } catch (e:Exception) {
                        e.printStackTrace()
                    }
                }, "image", encoded, "base64", upload_title.text.toString(), upload_title.text.toString(), upload_description.text.toString())
            }
        }

        select_image.setOnClickListener {
            openGalleryForImage()
        }
    }

    /**
     * Open the gallery to get an image
     */
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    /**
     * onActivityResult is override and called when the user finished picking an image from is gallery
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            selected_image.setImageURI(data?.data) // handle chosen image
            image = true
            imagePath = URI(data?.data.toString())
            bitmapImage = MediaStore.Images.Media.getBitmap(activity?.contentResolver, data?.data)
            Log.d("UPLOAD", imagePath.toString())
        }
    }
}