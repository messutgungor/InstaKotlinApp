package com.messutgungor.instakotlinapp.Login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.messutgungor.instakotlinapp.Home.HomeActivity
import com.messutgungor.instakotlinapp.Model.Users
import com.messutgungor.instakotlinapp.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var mAuth : FirebaseAuth    //Giriş işlemleri için
    lateinit var mRef : DatabaseReference  //Database işlemleri için
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference
        init()
    }



    fun init(){

        etEmailTelUserName.addTextChangedListener(watcher)
        etSifreLogin.addTextChangedListener(watcher)

        btnGirisYap.setOnClickListener {

            oturumAcacakKullaniciyiDenetle(etEmailTelUserName.text.toString(),etSifreLogin.text.toString())
        }

        tvGirisYapLogin.setOnClickListener {
            var intent = Intent(this@LoginActivity,RegisterActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) //Geri Tuşuna basıldığında aktivitenin tekrar açıkmasını önlemek için eklendi
            startActivity(intent)
            finish()
        }

    }

    private fun oturumAcacakKullaniciyiDenetle(emailPhoneNumberUserName: String, sifre: String) {

        var kullaniciBulundu=false

        mRef.child("users").orderByChild("email").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (ds in p0!!.children){
                    var okunanKullanici = ds.getValue(Users ::class.java)

                    if(okunanKullanici!!.email!!.toString().equals(emailPhoneNumberUserName)){
                        oturumAc(okunanKullanici,sifre,false)
                        kullaniciBulundu=true
                        break
                    }
                    else if(okunanKullanici!!.user_name!!.toString().equals(emailPhoneNumberUserName)){
                        oturumAc(okunanKullanici,sifre,false)
                        kullaniciBulundu=true
                        break
                    }else if(okunanKullanici!!.phone_number!!.toString().equals(emailPhoneNumberUserName)){
                        oturumAc(okunanKullanici,sifre,true)
                        kullaniciBulundu=true
                        break
                    }
                }
                if(kullaniciBulundu==false){
                    Toast.makeText(this@LoginActivity,"Kullanıcı bulunamadı",Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    private fun oturumAc(okunanKullanici: Users, sifre: String, telefonlaGiris: Boolean) {
        var girisYapacakEmail=""
        if(telefonlaGiris==true){
            girisYapacakEmail=okunanKullanici.email_phone_number.toString()
        }else{
            girisYapacakEmail=okunanKullanici.email.toString()
        }

        mAuth.signInWithEmailAndPassword(girisYapacakEmail,sifre)
            .addOnCompleteListener(object :OnCompleteListener<AuthResult>{
                override fun onComplete(p0: Task<AuthResult>) {
                    if(p0.isSuccessful){
                        Toast.makeText(this@LoginActivity,"Başarıyla oturum açılmıştır. "+mAuth.currentUser!!.uid,Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@LoginActivity,"Kullanıcı Adı/Şifre Hatalı",Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }


    var watcher : TextWatcher= object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if(etEmailTelUserName.text.toString().length>=6 && etSifreLogin.text.toString().length>=6){
                btnGirisYap.isEnabled = true
                btnGirisYap.setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.beyaz))
                btnGirisYap.setBackgroundResource(R.drawable.register_button_aktif)

            }else{

                btnGirisYap.isEnabled = false
                btnGirisYap.setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.sonukmavi))
                btnGirisYap.setBackgroundResource(R.drawable.register_button)
            }
        }


    }


    private fun setupAuthListener(){
        mAuthListener = object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser
                if (user!=null){
                    var intent = Intent(this@LoginActivity,HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
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
