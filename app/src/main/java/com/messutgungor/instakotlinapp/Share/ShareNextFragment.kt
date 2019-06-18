package com.messutgungor.instakotlinapp.Share


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.messutgungor.instakotlinapp.Home.HomeActivity
import com.messutgungor.instakotlinapp.Model.Posts
import com.messutgungor.instakotlinapp.Profile.YukleniyorFragment

import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.utils.DosyaIslemleri
import com.messutgungor.instakotlinapp.utils.EventbusDataEvents
import com.messutgungor.instakotlinapp.utils.UniversalImageLoader
import kotlinx.android.synthetic.main.fragment_share_next.view.*
import kotlinx.android.synthetic.main.fragment_yukleniyor.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class ShareNextFragment : Fragment() {

    var secilenDosyaYolu : String? = null
    var dosyaTuruResimMi : Boolean?=null
    var photoUri : Uri? =null

    lateinit var mAuth : FirebaseAuth    //Giriş işlemleri için
    lateinit var mRef : DatabaseReference
    lateinit var mUser: FirebaseUser
    lateinit var mStorageReference: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_share_next, container, false)

        UniversalImageLoader.setImage(secilenDosyaYolu!!,view!!.imgShare,null,"file://")

        photoUri= Uri.parse("file://"+secilenDosyaYolu)

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mRef = FirebaseDatabase.getInstance().reference
        mStorageReference = FirebaseStorage.getInstance().reference

        view.tvPaylasButon.setOnClickListener {

            //Resim dosyasını sıkıştır
            if(dosyaTuruResimMi==true){
                DosyaIslemleri.compressResimDosya(this,secilenDosyaYolu)
            }
            //video dosyasını sıkıştır
            else if(dosyaTuruResimMi==false){
                DosyaIslemleri.compressVideoDosya(this,secilenDosyaYolu)
            }

        }

            view.imgBackShare.setOnClickListener {
                this.activity!!.onBackPressed()
            }
        // Inflate the layout for this fragment
        return view

    }

    private fun veritabaninaBilgileriYaz(yuklenenDosyaURL: String,view: View) {
        var postID :String? = mRef.child("posts").child(mUser.uid).push().key
        var yuklenenPost = Posts(mUser.uid,postID,"",view.edShareAciklama.text.toString(),yuklenenDosyaURL)
        mRef.child("posts").child(mUser.uid).child(postID!!).setValue(yuklenenPost)
        mRef.child("posts").child(mUser.uid).child(postID!!).child("yuklenme_tarihi").setValue(ServerValue.TIMESTAMP)

        var intent = Intent(activity,HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)

    }


    //----------------------------------EVENTBUS-----------------------------
    @Subscribe(sticky = true)
    internal fun onSecilenDosyaEventEvent(secilenDosya : EventbusDataEvents.PaylasilacakDosyaGonder){
        secilenDosyaYolu=secilenDosya!!.dosyaYolu!!
        dosyaTuruResimMi = secilenDosya!!.dosyaTuruResimMi
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

fun uploadStorage(filePath: String?) {


//DOSYA YÜKLEME İŞLEMİ

    var dialogYukleniyor = YukleniyorFragment()
    dialogYukleniyor.show(activity!!.supportFragmentManager, "yukleniyorFragmenti")
    dialogYukleniyor.isCancelable = false


    var file = Uri.parse("file://"+filePath)
    val profilePicReference =
        mStorageReference.child("users").child(mUser!!.uid).child(file!!.lastPathSegment)
    var uploadTask = profilePicReference.putFile(file!!)

    uploadTask.addOnFailureListener(OnFailureListener {
        Log.e("EDIT", "foto yüklenirken hata çıktı")
        Toast.makeText(activity, "Hata Oluştu " + it.message, Toast.LENGTH_SHORT).show()

    }).addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> {

        Log.e("EDIT", "yükleme basarılı")

        var urlTask =
            uploadTask.continueWithTask(object : Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                override fun then(task: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                    if (!task.isSuccessful()) {
                        throw task.getException()!!
                    }

                    // Continue with the task to get the download URL
                    return profilePicReference.getDownloadUrl()
                }

            }).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.e("EDIT", "URL:" + it.getResult())
                    if (activity != null) {
                    }
                    photoUri = null
                    veritabaninaBilgileriYaz(it.result.toString(),view!!)
                    dialogYukleniyor.dismiss()
                    activity!!.onBackPressed()
                }
            }


    }).addOnProgressListener (object : OnProgressListener<UploadTask.TaskSnapshot>{
        override fun onProgress(p0: UploadTask.TaskSnapshot?) {
            var progress =100.0*p0!!.bytesTransferred/p0!!.totalByteCount
            Log.e("HATA","ILERLEME: "+progress)
            dialogYukleniyor.tvYukleniyor.text="%"+progress.toInt().toString()+" yüklendi."

        }

    }

    )

}
}

