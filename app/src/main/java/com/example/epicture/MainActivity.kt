package com.example.epicture

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var layout: RelativeLayout
    private lateinit var viewPager: ViewPager


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewPager = findViewById(
            R.id.viewpager
        )
        val adapter = PageAdapter(
            supportFragmentManager
        )
        viewPager.adapter = adapter

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener { menuItem ->
            print(menuItem.itemId)
            when (menuItem.itemId) {
                R.id.navigation_home -> viewPager.currentItem = 0
                R.id.navigation_upload -> viewPager.currentItem = 1
                R.id.navigation_profile -> viewPager.currentItem = 2
            }
            true
        }

        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> navView.menu.findItem(R.id.navigation_home).isChecked = true
                    1 -> navView.menu.findItem(R.id.navigation_upload).isChecked = true
                    2 -> navView.menu.findItem(R.id.navigation_profile).isChecked = true
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }


}