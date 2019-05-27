package com.messutgungor.instakotlinapp.Share

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.utils.SharePagerAdapter
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : AppCompatActivity() {
    private val ACTIVITY_NUMBER = 2
    private val TAG = "ShareActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        setupShareViewPager()

    }

    private fun setupShareViewPager() {

        var tabAdlari= ArrayList<String>()
        tabAdlari.add("GALERİ")
        tabAdlari.add("FOTOĞRAF")
        tabAdlari.add("VİDEO")


        var sharePagerAdapter = SharePagerAdapter(supportFragmentManager,tabAdlari)
        sharePagerAdapter.addFragment(ShareGalleryFragment())
        sharePagerAdapter.addFragment(ShareCameraFragment())
        sharePagerAdapter.addFragment(ShareVideoFragment())

        shareViewPager.adapter=sharePagerAdapter
        shareTapLayout.setupWithViewPager(shareViewPager)
    }


}
