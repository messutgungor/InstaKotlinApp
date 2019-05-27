package com.messutgungor.instakotlinapp.utils

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class SharePagerAdapter(fm : FragmentManager, tabAdlari : ArrayList<String>) : FragmentPagerAdapter(fm) {

    private var mFragmentList: ArrayList<Fragment> = ArrayList()
    private var mTabAdlari :ArrayList<String> =tabAdlari

    override fun getItem(position: Int): Fragment {
        return mFragmentList.get(position)
    }

    override fun getCount(): Int {
       return mFragmentList.size
    }

    fun addFragment(fragment: Fragment){
        mFragmentList.add(fragment)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTabAdlari.get(position)
    }
}