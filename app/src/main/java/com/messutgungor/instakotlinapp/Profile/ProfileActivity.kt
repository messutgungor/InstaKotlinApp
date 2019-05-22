package com.messutgungor.instakotlinapp.Profile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.utils.BottomNavigationViewHelper
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.bottomNavigationView


class ProfileActivity : AppCompatActivity() {
    private val ACTIVITY_NUMBER = 4
    private val TAG = "ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupToolbar()
        setupNavigationView()


    }

    private fun setupToolbar() {
        imgProfilSettings.setOnClickListener {
            var intent = Intent(this,ProfileSettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

        tvProfilDuzenleProfil.setOnClickListener {
            profileRootProfil.visibility= View.GONE
            var transaction= supportFragmentManager.beginTransaction()
            transaction.replace(R.id.profilContainerProfil,ProfileEditFragment())
            transaction.addToBackStack("profilEditFragment")
            transaction.commit()
        }
    }


    fun setupNavigationView(){
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomNavigationViewHelper.setupNavigation(this,bottomNavigationView)
        //O an aktif olan aktivitenin ikonun seçili olması için
        var menu = bottomNavigationView.menu
        var menuItem=menu.getItem(ACTIVITY_NUMBER)
        menuItem.isEnabled=true
        menuItem.isChecked=true
    }

    override fun onBackPressed() {
        //Geri butonuna basıldığında containerimizi tekrar görünür hale getiriyoruz
        profileRootProfil.visibility= View.VISIBLE
        super.onBackPressed()
    }
}
