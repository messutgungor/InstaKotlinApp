package com.messutgungor.instakotlinapp.Profile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.utils.BottomNavigationViewHelper
import kotlinx.android.synthetic.main.activity_profile_settings.*

class ProfileSettingsActivity : AppCompatActivity() {
    private val ACTIVITY_NUMBER = 4
    private val TAG = "ProfileSettingsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)
        setupNavigationView()
        setupToolbar()
        fragmentNavigation()
    }

    private fun fragmentNavigation() {
        tvProfilDuzenleHesapAyarlari.setOnClickListener {
            profileSettingsRoot.visibility= View.GONE
            var transaction= supportFragmentManager.beginTransaction()
            transaction.replace(R.id.profileSettingsContainer,ProfileEditFragment())
            transaction.addToBackStack("profilEditFragment")
            transaction.commit()
        }

        tvCikisYap.setOnClickListener {

            var dialog = SignOutFragment()
            dialog.show(supportFragmentManager,"cikisYapDialogGoster")

            /*profileSettingsRoot.visibility= View.GONE
            var transaction= supportFragmentManager.beginTransaction()
            transaction.replace(R.id.profileSettingsContainer,SignOutFragment())
            transaction.addToBackStack("signOutFragment")
            transaction.commit()*/
        }
    }

    private fun setupToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }


    fun setupNavigationView(){
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomNavigationViewHelper.setupNavigation(this,bottomNavigationView)
        //O an aktif olan aktivitenin ikonun seçili olması için
        var menu = bottomNavigationView.menu
        var menuItem=menu.getItem(ACTIVITY_NUMBER)
        menuItem.setEnabled(true)
        menuItem.isEnabled=true
        menuItem.isChecked=true

    }


    override fun onBackPressed() {
        //Geri butonuna basıldığında containerimizi tekrar görünür hale getiriyoruz
        profileSettingsRoot.visibility= View.VISIBLE
        super.onBackPressed()
    }
}
