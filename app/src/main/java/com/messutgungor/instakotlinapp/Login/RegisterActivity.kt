package com.messutgungor.instakotlinapp.Login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.messutgungor.instakotlinapp.Home.HomeActivity
import com.messutgungor.instakotlinapp.Model.Users
import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.utils.EventbusDataEvents
import kotlinx.android.synthetic.main.activity_register.*
import org.greenrobot.eventbus.EventBus

class RegisterActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {

    lateinit var manager: FragmentManager
    lateinit var mRef: DatabaseReference
    lateinit var mAuth : FirebaseAuth    //Giriş işlemleri için
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    var TAG="REGISTER ACTIVITY"

    //TODO Bazen Databaseden telefon no kontrol ve mail konrol uzun sürüyor buton aktif olduğu için de Kayıt fragmentine geçiş sorunlu oluyor. Progress bar koymak akıllıca olabilir.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setupAuthListener()

        mRef=FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        manager = supportFragmentManager
        manager.addOnBackStackChangedListener(this)


        init()


    }

    private fun init() {

        tvGirisYapRegister.setOnClickListener {
            var intent = Intent(this@RegisterActivity,LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }

        tvEposta.setOnClickListener {
            viewTelefon.visibility=View.INVISIBLE
            viewEposta.visibility=View.VISIBLE
            etGirisYontemi.setText("")
            etGirisYontemi.inputType=InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            etGirisYontemi.setHint("E-Posta")
            btnIleri.isEnabled=false
            btnIleri.setTextColor(ContextCompat.getColor(this@RegisterActivity,R.color.sonukmavi))
            btnIleri.background=ContextCompat.getDrawable(this@RegisterActivity,R.drawable.register_button)
            tvEposta.setTextColor(ContextCompat.getColor(this@RegisterActivity,R.color.siyah))
            tvTelefon.setTextColor(ContextCompat.getColor(this@RegisterActivity,R.color.gri))

        }

        tvTelefon.setOnClickListener {
            viewTelefon.visibility=View.VISIBLE
            viewEposta.visibility=View.INVISIBLE
            etGirisYontemi.setText("")
            etGirisYontemi.inputType=InputType.TYPE_CLASS_PHONE
            etGirisYontemi.setHint("Telefon")
            btnIleri.isEnabled=false
            btnIleri.setTextColor(ContextCompat.getColor(this@RegisterActivity,R.color.sonukmavi))
            btnIleri.background=ContextCompat.getDrawable(this@RegisterActivity,R.drawable.register_button)
            tvTelefon.setTextColor(ContextCompat.getColor(this@RegisterActivity,R.color.siyah))
            tvEposta.setTextColor(ContextCompat.getColor(this@RegisterActivity,R.color.gri))

        }

        etGirisYontemi.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.length>=10){
                    btnIleri.isEnabled=true
                    btnIleri.setTextColor(ContextCompat.getColor(this@RegisterActivity,R.color.beyaz))
                    btnIleri.background=ContextCompat.getDrawable(this@RegisterActivity,R.drawable.register_button_aktif)
                }else{
                    btnIleri.isEnabled=false
                    btnIleri.setTextColor(ContextCompat.getColor(this@RegisterActivity,R.color.sonukmavi))
                    btnIleri.background=ContextCompat.getDrawable(this@RegisterActivity,R.drawable.register_button)
                }
            }

        })


        btnIleri.setOnClickListener {
           if(etGirisYontemi.hint.toString().equals("Telefon")) {
               Log.e(TAG,"İleri butonuna tıklandı")

               var isAvailablePhoneNumber=false
               if(isValidPhoneNumber(etGirisYontemi.text.toString())){
                   Log.e(TAG,"Düzgün bir telefon numarası girildi.")

                   mRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener{
                       override fun onCancelled(p0: DatabaseError) {
                           Log.e(TAG,"DataBase hatası alındı"+p0.message)
                       }

                       override fun onDataChange(p0: DataSnapshot) {
                           if(p0.getValue()==null) Log.e(TAG,"Database den null değer döndü")

                           if(p0.getValue()!=null){
                               for (user in p0.children){
                                   var okunanKullanici = user.getValue(Users::class.java)
                                   if(okunanKullanici!!.phone_number!!.equals(etGirisYontemi.text.toString())){
                                       Toast.makeText(this@RegisterActivity,"Bu Telefon Numarası kullanımda.",Toast.LENGTH_SHORT).show()
                                       isAvailablePhoneNumber=true
                                       break
                                   }
                               }
                               if (isAvailablePhoneNumber==false){
                                   registerRoot.visibility=View.GONE
                                   var transaction=supportFragmentManager.beginTransaction()
                                   transaction.replace(R.id.registerContainer,TelefonKoduGirFragment())
                                   transaction.addToBackStack("telefonKoduGirFragmentEklendi")
                                   transaction.commit()
                                   EventBus.getDefault().postSticky(EventbusDataEvents.KayitBilgileriniGonder(etGirisYontemi.text.toString(),null,null,null,false))
                               }

                           }

                       }
                   }

                   )

                  }else
               {
                   Toast.makeText(this,"Lütfen geçerli bir telefon Numarası giriniz",Toast.LENGTH_SHORT).show()
               }

           }else{
               if(isValidEmail(etGirisYontemi.text.toString())){

                   var isAvailableEmail=false

                   //mRef ile mailin daha önce alınıp alınmadığını kontrol edip buna göre işlem yapıyoruz.
                   mRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener{
                       override fun onCancelled(p0: DatabaseError) {

                       }

                       override fun onDataChange(p0: DataSnapshot) {
                          if(p0.getValue() !=null){
                              for(user in p0.children){
                               var okunanKullanici= user.getValue(Users ::class.java)
                                  if(okunanKullanici!!.email!!.equals(etGirisYontemi.text.toString())){
                                      Toast.makeText(this@RegisterActivity,"Mail kullanımda.",Toast.LENGTH_SHORT).show()
                                      isAvailableEmail=true
                                      break
                                  }
                              }

                              if(isAvailableEmail==false){

                                  registerRoot.visibility=View.GONE
                                  var transaction=supportFragmentManager.beginTransaction()
                                  transaction.replace(R.id.registerContainer,KayitFragment())
                                  transaction.addToBackStack("emailIleGirisFragmentEklendi")
                                  transaction.commit()
                                  EventBus.getDefault().postSticky(EventbusDataEvents.KayitBilgileriniGonder(null,etGirisYontemi.text.toString(),null,null,true))


                              }
                          }
                       }


                   })


               }else{
                   Toast.makeText(this,"Lütfen geçerli bir E-posta adresi giriniz.",Toast.LENGTH_SHORT).show()
               }

           }

            //var intent = Intent(this,HomeActivity::class.java)
            //startActivity(intent)
        }
    }

    override fun onBackStackChanged() {

      val elemanSayisi= manager.backStackEntryCount

      if(elemanSayisi==0) {
          registerRoot.visibility=View.VISIBLE
      }
    }


    fun isValidEmail(kontroledilecekMail : String): Boolean {
        if(kontroledilecekMail==null){
            return false
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(kontroledilecekMail).matches()
    }

    fun isValidPhoneNumber(konroledilecekTelefon : String):Boolean{

        if(konroledilecekTelefon==null || (konroledilecekTelefon.length >14)){
            return false
        }

        return android.util.Patterns.PHONE.matcher(konroledilecekTelefon).matches()
    }

    private fun setupAuthListener(){
        mAuthListener = object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser
                if (user!=null){
                    var intent = Intent(this@RegisterActivity, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
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
