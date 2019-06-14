package com.messutgungor.instakotlinapp.Profile


import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.messutgungor.instakotlinapp.Model.Users
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.utils.EventbusDataEvents
import com.messutgungor.instakotlinapp.utils.UniversalImageLoader
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.Exception


class ProfileEditFragment : Fragment() {

    lateinit var circleProfileImage:CircleImageView
    lateinit var progressBarEditProfile:ProgressBar
    lateinit var gelenKullaniciBilgileri:Users
    lateinit var mDatabaseRef : DatabaseReference
    lateinit var mStorageRef : StorageReference
    var profilPhotoUri : Uri? = null

    val RESIM_SEC=100
    val TAG = "Profile edit frag"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        var view =  inflater.inflate(R.layout.fragment_profile_edit, container, false)

        mDatabaseRef=FirebaseDatabase.getInstance().reference
        mStorageRef=FirebaseStorage.getInstance().reference
        setupKullaniciBilgileri(view)

      //  setupProfilePicture()

        view.imgClose.setOnClickListener {
            activity!!.onBackPressed()
        }

        view.tvFotografiDegistir.setOnClickListener {

            var intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_PICK)
            startActivityForResult(intent,RESIM_SEC)//RESIM_SEC Herhangi vir sayı olabilir. Bizim için 100 değeri işlemin başarılı olduğunu gösterecek.Hem de override metodunda Result Code olarak gidecek


        }

        view.imgBtnDegisiklikleriKaydet.setOnClickListener {

            if(profilPhotoUri!=null) {

                var dialogYukleniyor = YukleniyorFragment()
                dialogYukleniyor.show(activity!!.supportFragmentManager, "yukleniyorFragmenti")
                dialogYukleniyor.isCancelable = false

                var file = profilPhotoUri
                var profiliGuncelleUserID = FirebaseAuth.getInstance().currentUser!!.uid
                val profilePicReference =
                    mStorageRef.child("images/users/" + gelenKullaniciBilgileri!!.user_id + "/profile_picture/" + file!!.lastPathSegment)
                var uploadTask = profilePicReference.putFile(file)

                uploadTask.addOnFailureListener(OnFailureListener {
                    Log.e("EDIT", "foto yüklenirken hata çıktı")
                    kullaniciAdiniGuncelle(view,false)

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
                                profilPhotoUri = null
                                mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id!!)
                                    .child("user_detail").child("profile_picture").setValue(it.result.toString())
                                dialogYukleniyor.dismiss()
                                kullaniciAdiniGuncelle(view,true)
                            }
                        }


                })
            }else{
                kullaniciAdiniGuncelle(view,null)
            }



           /*
            var isAvailableUserName=false
            var degisiklikVarmi=false

            if(!gelenKullaniciBilgileri!!.adi_soyadi!!.equals(view.etProfileName.text.toString())){
                mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id!!).child("adi_soyadi").setValue(view.etProfileName.text.toString())
                degisiklikVarmi=true
            }

            if(!gelenKullaniciBilgileri!!.user_detail!!.biography!!.equals(view.etbiography.text.toString())){
                mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id!!).child("user_detail").child("biography").setValue(view.etbiography.text.toString())
                degisiklikVarmi=true

            }

            if(!gelenKullaniciBilgileri!!.user_detail!!.web_site!!.equals(view.etWebSite.text.toString())){
                mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id!!).child("user_detail").child("web_site").setValue(view.etWebSite.text.toString())
                degisiklikVarmi=true

            }


            if(!gelenKullaniciBilgileri!!.user_name!!.equals(view.etUserName.text.toString())){
                mDatabaseRef.child("users").orderByChild("user_name").addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        for (ds in p0!!.children){
                            var okunanKullniciAdi = ds!!.getValue(Users::class.java)!!.user_name

                            if(okunanKullniciAdi!!.equals(view.etUserName.text.toString())){
                                isAvailableUserName=true
                                Toast.makeText(activity,"Bu Kullanıcı adı kullanımda.",
                                    Toast.LENGTH_SHORT).show()
                                break
                            }
                        }

                        if(isAvailableUserName==false){
                            mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id!!).child("user_name").setValue(view.etUserName.text.toString())
                            degisiklikVarmi=true

                        }
                    }

                })
            }

            if(profilPhotoUri!=null){

                var dialogYukleniyor = YukleniyorFragment()
                dialogYukleniyor.show(activity!!.supportFragmentManager,"yukleniyorFragmenti")

                var file = profilPhotoUri
                var profiliGuncelleUserID = FirebaseAuth.getInstance().currentUser!!.uid
                val profilePicReference = mStorageRef.child("images/users/"+gelenKullaniciBilgileri!!.user_id+"/profile_picture/"+file!!.lastPathSegment)
                var uploadTask= profilePicReference.putFile(file)

                uploadTask.addOnFailureListener(OnFailureListener {
                    Log.e("EDIT", "foto yüklenirken hata çıktı")
                }).addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> {

                    Log.e("EDIT", "yükleme basarılı")

                    var urlTask = uploadTask.continueWithTask(object : Continuation<UploadTask.TaskSnapshot,Task<Uri>> {
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
                            if(activity!=null){
                            }
                            profilPhotoUri = null
                            mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id!!).child("user_detail").child("profile_picture").setValue(it.result.toString())
                            dialogYukleniyor.dismiss()
                        }
                    }


                })




            }

            if(degisiklikVarmi==true){Toast.makeText(activity,"Kullanıcı Güncellendi.",
                Toast.LENGTH_SHORT).show()}
*/

        }

        return view

    }


    //profilresmi değişti
    //true : başarılı bir şekilde resim storage'a yüklenmiş ve veritabanına yazılmıştır.
    //false : resim yüklenirken hata oluşmuştur
    //null : kullanıcı resmini değiştirmek istememiştir
    private fun kullaniciAdiniGuncelle(view: View, profilResmiDegisti: Boolean?) {

        if(!gelenKullaniciBilgileri!!.user_name!!.equals(view.etUserName.text.toString())){
            mDatabaseRef.child("users").orderByChild("user_name").addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    var isAvailableUserName=false
                    for (ds in p0!!.children){
                        var okunanKullniciAdi = ds!!.getValue(Users::class.java)!!.user_name

                        if(okunanKullniciAdi!!.equals(view.etUserName.text.toString())){
                            isAvailableUserName=true
                            profilBilgileriniGuncelle(view,profilResmiDegisti,false)
                            break
                        }
                    }

                    if(isAvailableUserName==false){
                        mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id!!).child("user_name").setValue(view.etUserName.text.toString())
                        profilBilgileriniGuncelle(view,profilResmiDegisti,true)
                    }
                }

            })
        }else{
            profilBilgileriniGuncelle(view,profilResmiDegisti,null)
        }

    }

    private fun profilBilgileriniGuncelle(view: View, profilResmiDegisti: Boolean?, userNamedegisti: Boolean?) {

        var degisiklikVarmi : Boolean? =null

        if(!gelenKullaniciBilgileri!!.adi_soyadi!!.equals(view.etProfileName.text.toString())){
            mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id!!).child("adi_soyadi").setValue(view.etProfileName.text.toString())
            degisiklikVarmi=true
        }

        if(!gelenKullaniciBilgileri!!.user_detail!!.biography!!.equals(view.etbiography.text.toString())){
            mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id!!).child("user_detail").child("biography").setValue(view.etbiography.text.toString())
            degisiklikVarmi=true

        }

        if(!gelenKullaniciBilgileri!!.user_detail!!.web_site!!.equals(view.etWebSite.text.toString())){
            mDatabaseRef.child("users").child(gelenKullaniciBilgileri!!.user_id!!).child("user_detail").child("web_site").setValue(view.etWebSite.text.toString())
            degisiklikVarmi=true

        }


        if(profilResmiDegisti==null && userNamedegisti==null && degisiklikVarmi==null){
            //Profil Edit Kısmında herhangi bir değişiklik yok
            Toast.makeText(activity,"Değişiklik Yok.",Toast.LENGTH_SHORT).show()

        }else if(userNamedegisti==false && (degisiklikVarmi==true || profilResmiDegisti==true)){
            Toast.makeText(activity,"Bilgiler güncellendi ama kullanıcı adı kullanımda",Toast.LENGTH_SHORT).show()
        }else if(userNamedegisti==false && (degisiklikVarmi==false || profilResmiDegisti ==null)){
            Toast.makeText(activity,"Kullanıcı adı kullanımda",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(activity,"Bilgiler güncellendi",Toast.LENGTH_SHORT).show()
            activity!!.onBackPressed()
        }

    }

// tvFotografiDegistir butonuna tıklanınca açılan sayfadan resmi seçtikten sonra sonuç için dönder yaptığımızda aşağıdaki metod sonucu alıp gerekli işlemleri yapacak
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RESIM_SEC && resultCode== AppCompatActivity.RESULT_OK && data!!.data!=null){
            profilPhotoUri= data!!.data
            circleProfileImage.setImageURI(profilPhotoUri)


        }
    }

    private fun setupKullaniciBilgileri(view :View?) {
        circleProfileImage=view!!.findViewById(R.id.circleProfilimage)
        progressBarEditProfile=view!!.findViewById(R.id.progressBarEditProfile)

        view!!.etProfileName.setText(gelenKullaniciBilgileri!!.adi_soyadi)
        view!!.etUserName.setText(gelenKullaniciBilgileri!!.user_name)
        if(!gelenKullaniciBilgileri!!.user_detail!!.biography!!.isNullOrEmpty()){
            view!!.etbiography.setText(gelenKullaniciBilgileri!!.user_detail!!.biography)
        }
        if(!gelenKullaniciBilgileri!!.user_detail!!.web_site!!.isNullOrEmpty()){
            view!!.etWebSite.setText(gelenKullaniciBilgileri!!.user_detail!!.web_site)
        }

        var imgURL = gelenKullaniciBilgileri!!.user_detail!!.profile_picture!!

        UniversalImageLoader.setImage(imgURL,circleProfileImage,progressBarEditProfile,"")

    }



    //----------------------------------EVENTBUS-----------------------------
    @Subscribe(sticky = true)
    internal fun onKullaniciBilgileriEvent(kullaniciBilgileri : EventbusDataEvents.KullaniciBilgileriniGonder){
        gelenKullaniciBilgileri=kullaniciBilgileri!!.kullanici!!


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
