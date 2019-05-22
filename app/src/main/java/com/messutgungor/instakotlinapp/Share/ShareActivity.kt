package com.messutgungor.instakotlinapp.Share

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.utils.BottomNavigationViewHelper
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : AppCompatActivity() {
    private val ACTIVITY_NUMBER = 2
    private val TAG = "ShareActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        setupNavigationView()

    }


    fun setupNavigationView(){
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomNavigationViewHelper.setupNavigation(this,bottomNavigationView)

        //O an aktif olan aktivitenin ikonun seçili olması için
        var menu = bottomNavigationView.menu
        var menuItem=menu.getItem(ACTIVITY_NUMBER)
        menuItem.isChecked=true

    }
}
