package com.messutgungor.instakotlinapp.Share


import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.utils.EventbusDataEvents
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.Gesture
import com.otaliastudios.cameraview.GestureAction
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.fragment_share_camera.*
import kotlinx.android.synthetic.main.fragment_share_camera.view.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class ShareCameraFragment : Fragment() {

    var TAG ="ShareCameraFragment"
    var cameraView:CameraView?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_share_camera, container, false)

        cameraView = view.cameraView
        cameraView!!.mapGesture(Gesture.PINCH,GestureAction.ZOOM)
        cameraView!!.mapGesture(Gesture.TAP,GestureAction.FOCUS_WITH_MARKER)

        view.imgResimCek.setOnClickListener {
            cameraView!!.capturePicture()
        }

        cameraView!!.addCameraListener(object: CameraListener(){
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


                activity!!.anaLayout.visibility= View.GONE // fragment içerisinden direk ulaşamayız bu yüzden activity içerisinden ulaşıyoruz.
                activity!!.fragmentContainerShare.visibility=View.VISIBLE
                var transaction = activity!!.supportFragmentManager.beginTransaction() //SupportFragmentManager a bir fragment içerisinden ulaşamadığımız için activity içerisinden ulaşıyoruz.

                EventBus.getDefault().postSticky(EventbusDataEvents.PaylasilacakDosyaGonder(cekilenFoto.absolutePath.toString(),true))
                transaction.replace(R.id.fragmentContainerShare,ShareNextFragment())
                transaction.addToBackStack("shareNextFragmentEklendi.")
                transaction.commit()

            }
        })

        view.imgCloseCamera.setOnClickListener {
            activity!!.onBackPressed()
        }

        return view
    }

    override fun onResume() {
        cameraView!!.start()
        Log.e("BİLGİ","Camera Fragment on Resume")
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.e("BİLGİ","Camera Fragment on Pause")
        if(cameraView!=null){
            cameraView!!.stop()
            cameraView!!.destroy()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("BİLGİ","Camera Fragment on Destroy")
        if(cameraView!=null){
            cameraView!!.destroy()}
    }
}
