package com.example.retrofit

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.MenuItem
import com.example.retrofit.activities.BaseActivity
import com.example.retrofit.adapters.ViewPagerAdapter
import com.example.retrofit.fragments.ImagesFragment
import com.example.retrofit.fragments.UploadFragment
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.ui.LibsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    internal var prevMenuItem: MenuItem? = null

    private val mOnNavigationItemSelectedListener
            = object : BottomNavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when(item.itemId) {
                R.id.navigation_list    -> {
                    viewpager.currentItem = 0
                    return true
                }

                R.id.navigation_upload  -> {
                    viewpager.currentItem = 1
                    return true
                }
            }

            return false
        }
    }

    private val onPageChangeListener = object:ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            if(prevMenuItem != null) {
                prevMenuItem!!.isChecked = false
            } else {
                navigation.menu.getItem(0).isChecked = false
            }

            navigation.menu.getItem(position).isChecked = true
            prevMenuItem = navigation.menu.getItem(position)
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        viewpager.addOnPageChangeListener(onPageChangeListener)

        setupViewPager(viewpager)
    }

    private fun setupViewPager(viewPager:ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        val imagesFragment = ImagesFragment.newInstance(2)
        val uploadFragment = UploadFragment.newInstance()

        adapter.addFragment(imagesFragment)
        adapter.addFragment(uploadFragment)

        viewpager.offscreenPageLimit = 2
        viewpager.adapter = adapter
    }
}
