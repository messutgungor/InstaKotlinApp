package com.messutgungor.instakotlinapp.Home

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.utils.EventbusDataEvents
import com.otaliastudios.cameraview.*
import kotlinx.android.synthetic.main.fragment_camera.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.io.FileOutputStream

class CameraFragment : Fragment() {

    var myCamera: CameraView? = null
    var kameraIzinBilgisi: Boolean = false
    var TAG = "CameraFragment"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_camera, container, false)

        myCamera = view.camera_view
        myCamera!!.mapGesture(Gesture.PINCH, GestureAction.ZOOM)
        myCamera!!.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER)

        myCamera!!.addCameraListener(object: CameraListener(){
            override fun onPictureTaken(jpeg: ByteArray?) {
                super.onPictureTaken(jpeg)

                var cekilenFotoAdi : Long =System.currentTimeMillis()
                var file = File(Environment.getExternalStorageDirectory().absolutePath+"/DCIM/TestKlasoru")
                var cekilenFoto = File(Environment.getExternalStorageDirectory().absolutePath+"/DCIM/TestKlasoru/"+cekilenFotoAdi+".jpg")
                Log.e(TAG,"Resmin kayıt Yeri : " + cekilenFoto.absolutePath.toString())
                if (!file.exists()) {
                    file.mkdirs()
                }
                var dosyaOlustur = FileOutputStream(cekilenFoto.absolutePath)
                dosyaOlustur.write(jpeg)
                dosyaOlustur.close()



            }
        })


        view.imgCameraSwitch.setOnClickListener {
            if (myCamera!!.facing == Facing.BACK) {
                myCamera!!.facing = Facing.FRONT
            } else {
                myCamera!!.facing = Facing.BACK
            }

        }



        view.imgFotoCek.setOnClickListener {
            if(myCamera!!.facing==Facing.BACK){
                myCamera!!.capturePicture()
            }else{
                myCamera!!.captureSnapshot()
            }
        }

        return view
    }

    //----------------------------------EVENTBUS-----------------------------
    @Subscribe(sticky = true)
    internal fun onKameraIzinEvent(gelenKameraIzinBilgisi: EventbusDataEvents.KameraIzinBilgisiGonder) {
        kameraIzinBilgisi = gelenKameraIzinBilgisi.kameraIzinliMi
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
    override fun onResume() {
        if (kameraIzinBilgisi == true) {
            myCamera!!.start()
        }
        Log.e("BİLGİ", "Camera Fragment on Resume")
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.e("BİLGİ", "Camera Fragment on Pause")
        if (myCamera != null) {
            myCamera!!.stop()
            myCamera!!.destroy()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("BİLGİ", "Camera Fragment on Destroy")
        if (myCamera != null) {
            myCamera!!.destroy()
        }
    }
}