package com.messutgungor.instakotlinapp.Share


import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.utils.EventbusDataEvents
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.fragment_share_video.view.*
import org.greenrobot.eventbus.EventBus
import java.io.File

class ShareVideoFragment : Fragment() {

    var videoView : CameraView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_share_video, container, false)
        // Inflate the layout for this fragment

        videoView=view.videoView

        var olusacakVideoDosyaAdi = System.currentTimeMillis()
        var olusacakVideoKlasor = File(Environment.getExternalStorageDirectory().absolutePath+"/DCIM/TestKlasoru")
        var olusacakVideoDosya = File (Environment.getExternalStorageDirectory().absolutePath+"/DCIM/TestKlasoru/"+olusacakVideoDosyaAdi+".mp4")

        if(!olusacakVideoKlasor.exists()){
            olusacakVideoKlasor.mkdir()
        }

        videoView!!.addCameraListener(object :CameraListener(){
            override fun onVideoTaken(video: File?) {
                super.onVideoTaken(video)

                activity!!.anaLayout.visibility= View.GONE // fragment içerisinden direk ulaşamayız bu yüzden activity içerisinden ulaşıyoruz.
                activity!!.fragmentContainerShare.visibility=View.VISIBLE
                var transaction = activity!!.supportFragmentManager.beginTransaction() //SupportFragmentManager a bir fragment içerisinden ulaşamadığımız için activity içerisinden ulaşıyoruz.

                EventBus.getDefault().postSticky(EventbusDataEvents.PaylasilacakDosyaGonder(video!!.absolutePath.toString(),false))
                transaction.replace(R.id.fragmentContainerShare,ShareNextFragment())
                transaction.addToBackStack("shareNextFragmentEklendi.")
                transaction.commit()
            }
        })

        view.imgVideoCek.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if(event!!.action == MotionEvent.ACTION_DOWN){

                    videoView!!.startCapturingVideo(olusacakVideoDosya)
                    Toast.makeText(activity,"Video Kaydediliyor",Toast.LENGTH_SHORT).show()

                    return true
                }else if(event!!.action == MotionEvent.ACTION_UP){
                    videoView!!.stopCapturingVideo()
                    Toast.makeText(activity,"Video Kaydedildi",Toast.LENGTH_SHORT).show()

                    return true
                }
                return false
            }

        })

        view.imgCloseVideo.setOnClickListener {
            activity!!.onBackPressed()
        }

        return view
    }

    override fun onResume() {
        videoView!!.start()
        Log.e("BİLGİ","Video Fragment on Resume")
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.e("BİLGİ","Video Fragment on Pause")
        if(videoView!=null){videoView!!.stop()
            videoView!!.destroy()}


    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("BİLGİ","Video Fragment on Destroy")
        if(videoView!=null){
            videoView!!.destroy()}

    }
}
