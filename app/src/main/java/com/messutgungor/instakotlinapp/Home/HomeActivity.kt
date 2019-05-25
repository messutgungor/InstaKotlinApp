package com.messutgungor.instakotlinapp.Home

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.messutgungor.instakotlinapp.Login.LoginActivity
import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.utils.BottomNavigationViewHelper
import com.messutgungor.instakotlinapp.utils.HomePagerAdapter
import com.messutgungor.instakotlinapp.utils.UniversalImageLoader
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private val ACTIVITY_NUMBER = 0
    private val TAG = "HomeActivity"
    lateinit var mAuth : FirebaseAuth    //Giriş işlemleri için
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()
        setupNavigationView()
        setupHomeViewPager()
        initImageLoader()

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

    private fun initImageLoader(){
        var universalImageLoader= UniversalImageLoader(this)
        ImageLoader.getInstance().init(universalImageLoader.config)

    }


    private fun setupAuthListener(){
        mAuthListener = object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser
                if (user==null){
                    var intent = Intent(this@HomeActivity,LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)// Geri Tuşuna basıldığında aktivitenin tekrar açıkmasını önlemek için eklendi
                    startActivity(intent)
                    finish()
                }else{


                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if(mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }
}
