package com.messutgungor.instakotlinapp.Profile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.utils.BottomNavigationViewHelper
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    private val ACTIVITY_NUMBER = 4
    private val TAG = "ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
       setupNavigationView()

    }


    fun setupNavigationView(){
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomNavigationViewHelper.setupNavigation(this,bottomNavigationView)
        //O an aktif olan aktivitenin ikonun seçili olması için
        var menu = bottomNavigationView.menu
        var menuItem=menu.getItem(ACTIVITY_NUMBER)
        menuItem.setEnabled(true)
        menuItem.isEnabled=true
        Log.d(TAG,""+menuItem.itemId)
        menuItem.isChecked=true

    }
}
