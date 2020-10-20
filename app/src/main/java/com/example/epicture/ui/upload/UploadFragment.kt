package com.example.epicture.ui.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.epicture.R
import com.otaliastudios.cameraview.CameraListener
import kotlinx.android.synthetic.main.fragment_upload.*


class UploadFragment : Fragment() {

    private lateinit var uploadViewModel: UploadViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        uploadViewModel = ViewModelProviders.of(this).get(UploadViewModel::class.java)
        val res = inflater.inflate(R.layout.fragment_upload, container, false)
        // write code here
        return res
    }
}