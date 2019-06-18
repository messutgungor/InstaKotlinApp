package com.messutgungor.instakotlinapp.Home

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.*
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.messutgungor.instakotlinapp.Login.LoginActivity
import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.utils.EventbusDataEvents
import com.messutgungor.instakotlinapp.utils.HomePagerAdapter
import com.messutgungor.instakotlinapp.utils.UniversalImageLoader
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_home.*
import org.greenrobot.eventbus.EventBus

class HomeActivity : AppCompatActivity() {


    private val TAG = "HomeActivity"
    lateinit var mAuth : FirebaseAuth    //Giriş işlemleri için
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()
        initImageLoader()
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

        homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager,0)
        homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager,2)


        homeViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(p0: Int) {
                if(p0==0)
                {
                    //Camera fragmentine geçildiğinde ekranın üstündeki barı kaldırıp full ekran yapıyoruz
                    this@HomeActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    this@HomeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)

                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager,1)
                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager,2)
                    storageVeKameraIzinIste()
                    homePagerAdapter.secilenFragmentiViewPageraEkle(homeViewPager,0)

                    //Burada kameraya ulaşacağımız için kamera izni istiyoruz

                }

                if(p0==1)
                {    //Home fragmentine geçildiğinde ekranın üstündeki barı getirip full ekrandan çıkartıyoruz
                    this@HomeActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                    this@HomeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager,0)
                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager,2)
                    homePagerAdapter.secilenFragmentiViewPageraEkle(homeViewPager,1)
                }

                if(p0==2)
                {   //Messages fragmentine geçildiğinde ekranın üstündeki barı getirip full ekrandan çıkartıyoruz
                    this@HomeActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                    this@HomeActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager,0)
                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager,1)
                    homePagerAdapter.secilenFragmentiViewPageraEkle(homeViewPager,2)
                }


            }


        })

    }

    private fun storageVeKameraIzinIste() {
        Dexter.withActivity(this)
            .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if(report!!.areAllPermissionsGranted()){
                        EventBus.getDefault().postSticky(EventbusDataEvents.KameraIzinBilgisiGonder(true))
                    }
                    if(report!!.isAnyPermissionPermanentlyDenied){
                        Log.e("ShareActivity" , "izinlerden birine bir daha sorma denmiş.")
                        var builder = AlertDialog.Builder(this@HomeActivity)
                        builder.setTitle("İzin Gerekli")
                        builder.setMessage("Ayarlar Kısmından uygulamaya gerekli izinleri vermeniz gerekiyor. Onaylar mısınız?")
                        builder.setPositiveButton("AYARLARA GİT",object : DialogInterface.OnClickListener{
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog!!.cancel()
                                var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                var uri = Uri.fromParts("package",packageName,null)
                                intent.setData(uri)
                                startActivity(intent)
                            }

                        })
                        builder.setNegativeButton("İPTAL", object: DialogInterface.OnClickListener{
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog!!.cancel()
                                homeViewPager.setCurrentItem(1)
                            }
                        })
                        builder.show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    Log.e("HATA Mesajı " , "izinlerden biri reddedilmiş , kullanıcıyı ikna etmemiz gerekiyor dostum.")
                    token!!.continuePermissionRequest()
                    //token!!.cancelPermissionRequest()  // Eğer kullanırsak artık uygulama kullanıcıdan izin istemeyi bırakır.
                    var builder = AlertDialog.Builder(this@HomeActivity)
                    builder.setTitle("İzin Gerekli")
                    builder.setMessage("Uygulamaya izin vermeniz gerekiyor, Onaylar mısınız? ")
                    builder.setPositiveButton("ONAY VER",object : DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog!!.cancel()
                            token!!.continuePermissionRequest()
                            homeViewPager.setCurrentItem(1)
                        }

                    })
                    builder.setNegativeButton("İPTAL", object: DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog!!.cancel()
                            token!!.cancelPermissionRequest()
                            homeViewPager.setCurrentItem(1)
                        }

                    })
                    builder.show()
                }

            })
            .withErrorListener(object : PermissionRequestErrorListener {
                override fun onError(error: DexterError?) {
                    Log.e("HATA",""+error.toString())
                }

            })
            .check()
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



    override fun onResume() {
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
