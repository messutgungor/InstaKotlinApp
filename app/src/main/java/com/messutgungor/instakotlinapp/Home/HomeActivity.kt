package com.messutgungor.instakotlinapp.Home

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.utils.BottomNavigationViewHelper
import com.messutgungor.instakotlinapp.utils.HomePagerAdapter
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private val ACTIVITY_NUMBER = 0
    private val TAG = "HomeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupNavigationView()
        setupHomeViewPager()

    }

    private fun setupHomeViewPager() {
        var homePagerAdapter = HomePagerAdapter(supportFragmentManager)
        homePagerAdapter.addFragment(CameraFragment()) // id=0
        homePagerAdapter.addFragment(HomeFragment())  // id=1
        homePagerAdapter.addFragment(MessagesFragment()) // id=2

        // yukarıda oluşturduğumuz adaptörü aşağıda layout ta id sini homeViewPager verdiğimiz viewpagera ekliyoruz
        homeViewPager.adapter=homePagerAdapter

        homeViewPager.setCurrentItem(1) // id=1 olanı yolluyoruz. Bu sayede homeactivity çalıştığında ilk olarak home fragmentini gösterecek

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
