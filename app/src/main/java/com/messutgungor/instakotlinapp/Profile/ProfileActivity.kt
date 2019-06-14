package com.messutgungor.instakotlinapp.Profile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.messutgungor.instakotlinapp.Login.LoginActivity
import com.messutgungor.instakotlinapp.Model.Users
import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.utils.BottomNavigationViewHelper
import com.messutgungor.instakotlinapp.utils.EventbusDataEvents
import com.messutgungor.instakotlinapp.utils.UniversalImageLoader
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.bottomNavigationView
import org.greenrobot.eventbus.EventBus


class ProfileActivity : AppCompatActivity() {
    private val ACTIVITY_NUMBER = 4
    private val TAG = "ProfileActivity"
    lateinit var mAuth : FirebaseAuth    //Giriş işlemleri için
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mRef : DatabaseReference
    lateinit var mUser:FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupAuthListener()
        mRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        mUser= mAuth.currentUser!!

        setupToolbar()
        kullaniciBilgileriniGetir()
      //  setupProfilePhoto()


    }

    private fun kullaniciBilgileriniGetir() {

        tvProfilDuzenleProfil.isEnabled=false
        imgProfilSettings.isEnabled=false
        //addValueEventListener ile aktivite içerisindeki alanlarımız database de değişiklik olduğunda anlık olarak değişiyor.
        mRef.child("users").child(mUser.uid).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if(p0.getValue()!=null){
                    var okunanKullaniciBilgileri = p0.getValue(Users::class.java)

                    //EventBus kütüphanesi ile kullanıcı bilgelerini yayınlıyoruz.
                    EventBus.getDefault().postSticky(EventbusDataEvents.KullaniciBilgileriniGonder(okunanKullaniciBilgileri))
                    tvProfilDuzenleProfil.isEnabled=true
                    imgProfilSettings.isEnabled=true
                    tvProfilGercekAdi.text=okunanKullaniciBilgileri!!.adi_soyadi
                    tvProfilAdiToolbar.text=okunanKullaniciBilgileri!!.user_name
                    tvFollowerSayisi.text=okunanKullaniciBilgileri!!.user_detail!!.follower
                    tvFollowingSayisi.text=okunanKullaniciBilgileri!!.user_detail!!.following
                    tvPostSayisi.text=okunanKullaniciBilgileri!!.user_detail!!.post
                    var imgURL = okunanKullaniciBilgileri!!.user_detail!!.profile_picture!!
                    UniversalImageLoader.setImage(imgURL,circleProfilimage,progressBarProfile,"")

                    if(!okunanKullaniciBilgileri!!.user_detail!!.biography!!.isNullOrEmpty()){
                        tvBiyografi.text=okunanKullaniciBilgileri!!.user_detail!!.biography
                        tvBiyografi.visibility=View.VISIBLE
                    }

                    if(!okunanKullaniciBilgileri!!.user_detail!!.web_site!!.isNullOrEmpty()){
                        tvWebSitesi.text=okunanKullaniciBilgileri!!.user_detail!!.web_site
                        tvWebSitesi.visibility=View.VISIBLE
                    }

                }
            }

        })
    }

    private fun setupProfilePhoto() {
        //http://mehmetusak.com/wp-content/uploads/2018/10/android_robot_logo_by_ornecolorada_cc0_via_pixabay1904852_wide-100732483-large.jpg
        var imgURL="mehmetusak.com/wp-content/uploads/2018/10/android_robot_logo_by_ornecolorada_cc0_via_pixabay1904852_wide-100732483-large.jpg"

        UniversalImageLoader.setImage(imgURL,circleProfilimage,progressBarProfile,"http://")
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


    private fun setupAuthListener(){
        mAuthListener = object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser
                if (user==null){
                    var intent = Intent(this@ProfileActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) //Geri Tuşuna basıldığında aktivitenin tekrar açıkmasını önlemek için eklendi
                    startActivity(intent)
                    finish()
                }else{
                }
            }

        }
    }

    override fun onResume() {
        setupNavigationView()
        super.onResume()
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
