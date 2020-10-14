package com.example.epicture.ui.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.epicture.R

class UploadFragment : Fragment() {

    private lateinit var uploadViewModel: UploadViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        uploadViewModel =
                ViewModelProviders.of(this).get(UploadViewModel::class.java)
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }
}