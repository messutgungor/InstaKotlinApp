package com.messutgungor.instakotlinapp.Login


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.firebase.ui.auth.data.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.messutgungor.instakotlinapp.Model.Users

import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.utils.EventbusDataEvents
import kotlinx.android.synthetic.main.fragment_kayit.*
import kotlinx.android.synthetic.main.fragment_kayit.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class KayitFragment : Fragment() {

    var telNo = ""
    var verificationID = ""
    var gelenKod = ""
    var gelenEmail = ""
    var emailKayitIslemleri = true
    lateinit var mAuth: FirebaseAuth
    lateinit var mRef: DatabaseReference
    lateinit var progressBarKullaniciKayit: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        var view = inflater.inflate(R.layout.fragment_kayit, container, false)
        progressBarKullaniciKayit=view.progressBarKullaniciKayit
        progressBarKullaniciKayit.visibility=View.INVISIBLE
        mAuth = FirebaseAuth.getInstance()


        //Şimdilik kullanıcı bu fragmente ulaşmışsa çıkış yaptırıyoruz.
        if (mAuth.currentUser != null) {
            mAuth.signOut()
        }

        mRef = FirebaseDatabase.getInstance().reference

        view.etAdSoyad.addTextChangedListener(watcher)
        view.etKullaniciAdi.addTextChangedListener(watcher)
        view.etSifre.addTextChangedListener(watcher)

        view.tvGirisYapKayit.setOnClickListener {
            var intent = Intent(activity,LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

        view.btnGiris.setOnClickListener {

            var isAvailableUserName=false

            mRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    if(p0.getValue()!=null){
                        for(user in p0.children){
                            var okunanKullanici = user.getValue(Users::class.java)

                            if(okunanKullanici!!.user_name.equals(view.etKullaniciAdi.text.toString())){
                                Toast.makeText(activity,"Bu Kullanıcı adı kullanımda.",Toast.LENGTH_SHORT).show()
                                isAvailableUserName=true
                                break
                            }
                        }
                        if(isAvailableUserName==false){

                            progressBarKullaniciKayit.visibility=View.VISIBLE

                            //Kullanıcı email ile kayıt oluyorsa
                            if (emailKayitIslemleri) {
                                var sifre = view.etSifre.text.toString()
                                var adiSoyadi = view.etAdSoyad.text.toString()
                                var kullaniciAdi = view.etKullaniciAdi.text.toString()


                                mAuth.createUserWithEmailAndPassword(gelenEmail, sifre)
                                    .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                                        override fun onComplete(p0: Task<AuthResult>) {
                                            if (p0.isSuccessful) {
                                                Toast.makeText(activity, "Kayıt işlemi başarılı", Toast.LENGTH_SHORT).show()
                                                //Oturum Açan Kullanıcının verilerinin database ye kayıt edilmesi
                                                var userID = mAuth.currentUser!!.uid.toString()
                                                var kayitEdilecekKullanici =
                                                    Users(gelenEmail, sifre, kullaniciAdi, adiSoyadi, "", "", userID)

                                                mRef.child("users")
                                                    .child(userID).setValue(kayitEdilecekKullanici)
                                                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                                                        override fun onComplete(p0: Task<Void>) {
                                                            if (p0.isSuccessful) {
                                                                Toast.makeText(
                                                                    activity,
                                                                    "Kullnıcı Database ye Kayıt Edildi.",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                                progressBarKullaniciKayit.visibility=View.INVISIBLE

                                                            } else {
                                                                mAuth.currentUser!!.delete()
                                                                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                                                                        override fun onComplete(p0: Task<Void>) {
                                                                            if (p0.isSuccessful) {
                                                                                Toast.makeText(
                                                                                    activity,
                                                                                    "Hata! Kullanıcı Databaseye Kayıt olmadı.",
                                                                                    Toast.LENGTH_SHORT
                                                                                ).show()
                                                                            }
                                                                        }
                                                                    })
                                                                progressBarKullaniciKayit.visibility=View.INVISIBLE

                                                            }
                                                        }
                                                    })
                                            } else {
                                                Toast.makeText(activity, "Kayıt yapılamadı. " + p0.exception, Toast.LENGTH_SHORT).show()
                                                progressBarKullaniciKayit.visibility=View.INVISIBLE
                                            }
                                        }
                                    })
                            }
                            //Kullanıcı telefon no ile kayıt oluyorsa
                            else {

                                var sifre = view.etSifre.text.toString()
                                var adiSoyadi = view.etAdSoyad.text.toString()
                                var kullaniciAdi = view.etKullaniciAdi.text.toString()
                                var sahteEmail =
                                    telNo + "@mesut.com" // Kullanıcı telefon numarası ile kayıt olmak isterse bu şekilde bir trik ile bir mail adresi oluşturuyoruz.
                                //ÖRNEK : 0554123456@mesut.com

                                mAuth.createUserWithEmailAndPassword(sahteEmail, sifre)
                                    .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                                        override fun onComplete(p0: Task<AuthResult>) {
                                            if (p0.isSuccessful) {
                                                Toast.makeText(
                                                    activity,
                                                    "Kayıt işlemi tel no ile başarılı" + mAuth.currentUser!!.uid,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                //Oturum Açan Kullanıcının verilerinin database ye kayıt edilmesi
                                                var userID = mAuth.currentUser!!.uid.toString()
                                                var kayitEdilecekKullanici =
                                                    Users("", sifre, kullaniciAdi, adiSoyadi, telNo, sahteEmail, userID)

                                                mRef.child("users")
                                                    .child(userID).setValue(kayitEdilecekKullanici)
                                                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                                                        override fun onComplete(p0: Task<Void>) {
                                                            if (p0.isSuccessful) {
                                                                Toast.makeText(
                                                                    activity,
                                                                    "Kullnıcı Database ye Kayıt Edildi.",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()

                                                                progressBarKullaniciKayit.visibility=View.INVISIBLE

                                                            } else {
                                                                progressBarKullaniciKayit.visibility=View.INVISIBLE

                                                                mAuth.currentUser!!.delete()
                                                                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                                                                        override fun onComplete(p0: Task<Void>) {
                                                                            if (p0.isSuccessful) {
                                                                                Toast.makeText(
                                                                                    activity,
                                                                                    "Hata! Kullanıcı Databaseye Kayıt olmadı.",
                                                                                    Toast.LENGTH_SHORT
                                                                                ).show()
                                                                            }
                                                                        }
                                                                    })
                                                            }
                                                        }
                                                    })
                                            } else {
                                                progressBarKullaniciKayit.visibility=View.INVISIBLE
                                                Toast.makeText(
                                                    activity,
                                                    "Kayıt işlemi tel no ile yapılamadı. " + p0.exception,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    })
                            }
                        }

                    }


                }


            })


        }

        return view
    }

    var watcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s!!.length > 5) {

                if (etAdSoyad.text.toString().length > 5 && etKullaniciAdi.text.toString().length > 5 && etSifre.text.toString().length > 5) {
                    btnGiris.isEnabled = true
                    btnGiris.setTextColor(ContextCompat.getColor(activity!!, R.color.beyaz))
                    btnGiris.setBackgroundResource(R.drawable.register_button_aktif)
                }

            } else {
                btnGiris.isEnabled = false
                btnGiris.setTextColor(ContextCompat.getColor(activity!!, R.color.sonukmavi))
                btnGiris.setBackgroundResource(R.drawable.register_button)
            }
        }

    }


    //----------------------------------EVENTBUS-----------------------------
    //EventBus Kütüphanesi ile telefon numarasını register activity den alıyoruz
    @Subscribe(sticky = true)
    internal fun onKayitEvent(kayitBilgileri: EventbusDataEvents.KayitBilgileriniGonder) {
        if (kayitBilgileri.emailkayit == true) {
            emailKayitIslemleri = true
            gelenEmail = kayitBilgileri.email!!
            Toast.makeText(activity!!, "E-Posta: " + gelenEmail, Toast.LENGTH_LONG).show()

        } else {
            emailKayitIslemleri = false
            telNo = kayitBilgileri.telNo!!
            verificationID = kayitBilgileri.verificationID!!
            gelenKod = kayitBilgileri.code!!
            Toast.makeText(activity!!, "Verification: " + verificationID + " Gelen Kod: " + gelenKod, Toast.LENGTH_LONG)
                .show()
        }

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        //Fragmente Attach olduğumuzda EventBusa kayıt oluyoruz
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        //Fragmente deAttach olduğumuzda EventBusa kayıttan çıkıyoruz
        EventBus.getDefault().unregister(this)
    }
    //----------------------------------EVENTBUS-----------------------------


}
