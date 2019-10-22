package com.example.retrofit.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by Kim Joonsung on 2019-08-13.
 */
class ViewPagerAdapter : FragmentPagerAdapter {
    private var mFragmentLists:MutableList<Fragment> = mutableListOf()

    constructor(fm:FragmentManager) : super(fm)

    override fun getItem(position: Int) = mFragmentLists[position]

    override fun getCount() = mFragmentLists.size

    fun addFragment(fragment:Fragment) {
        mFragmentLists.add(fragment)
    }
}