package com.messutgungor.instakotlinapp.Login


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.utils.EventbusDataEvents
import kotlinx.android.synthetic.main.fragment_telefon_kodu_gir.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit

class TelefonKoduGirFragment : Fragment() {

    var gelenTelNo = ""
    lateinit var mCallBacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var verificationID =""
    var gelenKod=""
    lateinit var progressBar : ProgressBar
    lateinit var smsYollandiText: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_telefon_kodu_gir, container, false)

        view.tvKullanıcıTelNo.setText(gelenTelNo)

        progressBar = view.findViewById(R.id.progressBarTelNoOnayla)
        smsYollandiText= view.findViewById(R.id.tvSmsYollandı)


        setupCallBack()

        view.tvGirisYapTelefonKodu.setOnClickListener {
            var intent = Intent(activity,LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

        view.btnTelCodeIleri.setOnClickListener {

            if(gelenKod.equals(view.etOnayKodu.text.toString())){
                EventBus.getDefault().postSticky(EventbusDataEvents.KayitBilgileriniGonder(gelenTelNo,null,verificationID,gelenKod,false))
                var transaction=activity!!.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.telefonCodeContainer,KayitFragment())
                transaction.addToBackStack("KayitFragmentEklendi")
                transaction.commit()

            }else{
                Toast.makeText(this.activity,"Kod eşleşmiyor. Tekrar deneyin.",Toast.LENGTH_SHORT).show()
            }
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            gelenTelNo,
            60,
            TimeUnit.SECONDS,
            this.activity!!,
            mCallBacks
        )

        return view
    }

    private fun setupCallBack() {
        mCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                if(!credential.smsCode.isNullOrEmpty()){
                    gelenKod = credential.smsCode!!
                    progressBar.visibility=View.INVISIBLE
                    smsYollandiText.visibility=View.VISIBLE

                }


            }

            override fun onVerificationFailed(e: FirebaseException) {
                progressBar.visibility=View.INVISIBLE

            }

            override fun onCodeSent(
                verificationId: String?,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                verificationID=verificationId!!
                progressBar.visibility=View.VISIBLE
                smsYollandiText.visibility=View.INVISIBLE


            }
        }
    }

    //EventBus Kütüphanesi ile telefon numarasını register activity den alıyoruz
    @Subscribe (sticky = true)
    internal fun onTelefonNoEvent(kayitBilgileri : EventbusDataEvents.KayitBilgileriniGonder){
        gelenTelNo=kayitBilgileri.telNo!!
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


}
