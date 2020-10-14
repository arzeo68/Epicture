package com.example.epicture

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.epicture.ui.home.HomeFragment
import com.example.epicture.ui.profile.ProfileFragment
import com.example.epicture.ui.upload.UploadFragment


class PageAdapter(
    fm: FragmentManager?
) : FragmentPagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            HomeFragment()
        } else if (position == 1) {
            UploadFragment()
        } else {
            ProfileFragment()
        }
    }

    override fun getCount(): Int {
        return 3
    }
}