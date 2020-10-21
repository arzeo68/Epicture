package com.example.epicture.ui.upload

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.epicture.R
import kotlinx.android.synthetic.main.fragment_upload.*
import java.io.ByteArrayOutputStream


class UploadFragment : Fragment() {
    private var image: Boolean = false
    private var imagePath: String = ""

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
                val bm = BitmapFactory.decodeFile(imagePath)
                val byteArrayOutputStream = ByteArrayOutputStream()
                bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                byteArrayOutputStream.toByteArray()
            }
        }

        select_image.setOnClickListener {
            select_image.setColorFilter(0)
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
            imagePath = data?.data?.path.toString()
            Log.d("UPLOAD", imagePath)
        }
    }

    companion object {
        private const val REQUEST_CODE = 100
    }
}