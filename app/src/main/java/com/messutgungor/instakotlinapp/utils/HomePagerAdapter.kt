package com.messutgungor.instakotlinapp.utils

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class HomePagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm){

    private var mFragmentList:ArrayList<Fragment> = ArrayList()

    override fun getItem(p0: Int): Fragment {
        return mFragmentList.get(p0)
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment:Fragment){
        mFragmentList.add(fragment)
    }

}