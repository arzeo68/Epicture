package com.example.epicture.ui.upload

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
import androidx.preference.PreferenceManager
import com.example.epicture.App
import com.example.epicture.ImgurAuth
import com.example.epicture.R
import kotlinx.android.synthetic.main.favorite_list_view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_upload.*
import java.io.ByteArrayOutputStream
import java.net.URI


class UploadFragment : Fragment() {
    private var image: Boolean = false
    private lateinit var imagePath: URI
    private lateinit var bitmapImage: Bitmap

    private lateinit var uploadViewModel: UploadViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        uploadViewModel = ViewModelProviders.of(this).get(UploadViewModel::class.java)
        val res = inflater.inflate(R.layout.fragment_upload, container, false)




        return res
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        upload_button.setOnClickListener {
            if (image) {
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                val encoded: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                Log.d("UPLOAD", encoded)
                ImgurAuth.uploadImage({
                    activity?.runOnUiThread {
                        selected_image.setImageDrawable(null)
                        upload_title.text?.clear()
                        upload_description.text?.clear()
                        upload_description.clearFocus()
                        upload_title.clearFocus()
                    }
                }, {}, "image", encoded, "base64", upload_title.text.toString(), upload_title.text.toString(), upload_description.text.toString())
            }
        }

        select_image.setOnClickListener {
            openGalleryForImage()
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            selected_image.setImageURI(data?.data) // handle chosen image
            image = true
            imagePath = URI(data?.data.toString())
            bitmapImage = MediaStore.Images.Media.getBitmap(activity?.contentResolver, data?.data)
            Log.d("UPLOAD", imagePath.toString())
        }
    }

    companion object {
        private const val REQUEST_CODE = 100
    }
}