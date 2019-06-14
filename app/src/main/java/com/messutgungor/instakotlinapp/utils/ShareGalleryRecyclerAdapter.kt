package com.messutgungor.instakotlinapp.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.messutgungor.instakotlinapp.R
import kotlinx.android.synthetic.main.tek_sutun_grid_resim.view.*
import org.greenrobot.eventbus.EventBus

class ShareGalleryRecyclerAdapter(var klasordekiDosyalar:ArrayList<String>, var myContext : Context): RecyclerView.Adapter<ShareGalleryRecyclerAdapter.MyViewHolder>() {

    var inflater : LayoutInflater

    init {
        inflater = LayoutInflater.from(myContext)
    }


    override fun getItemCount(): Int {
        return klasordekiDosyalar.size
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        var tekSutunDosya = inflater.inflate(R.layout.tek_sutun_grid_resim,p0,false)

        return MyViewHolder(tekSutunDosya)
    }



    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {

        var dosyaYolu = klasordekiDosyalar.get(p1)
        var dosyaTuru = dosyaYolu.substring(dosyaYolu.lastIndexOf("."))


        if(dosyaTuru.equals(".mp4")){
            p0.videoSure.visibility=View.VISIBLE
            var retriver = MediaMetadataRetriever()
            retriver.setDataSource(myContext, Uri.parse("file://"+dosyaYolu))
            var videoSuresi = retriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            var videoSuresiLong = videoSuresi.toLong()
            p0.videoSure.text=convertDuration(videoSuresiLong)
            UniversalImageLoader.setImage(dosyaYolu, p0.dosyaResim,p0.progressBar, "file://")
        }else{
            p0.videoSure.visibility=View.GONE
            UniversalImageLoader.setImage(dosyaYolu, p0.dosyaResim,p0.progressBar, "file://")
        }

        p0.tekSutunDosya.setOnClickListener{
           //Toast.makeText(myContext,"Seçilen Öğenin Dosya Yolu :"+dosyaYolu,Toast.LENGTH_SHORT).show()
            EventBus.getDefault().post(EventbusDataEvents.GallerySecilenDosyaYoluGonder(dosyaYolu))
        }



    }


    class MyViewHolder (itemView:View?): RecyclerView.ViewHolder(itemView!!){
        var tekSutunDosya = itemView as ConstraintLayout

        var dosyaResim = tekSutunDosya.imgTekSutunImage
        var videoSure = tekSutunDosya.tvSure
        var progressBar = tekSutunDosya.progressBarGridImage


    }

    fun convertDuration(duration: Long): String {

        val second:Long = duration/1000 %60
        val minute:Long = duration/(1000*60)%60
        val hour:Long = duration/(1000*60*60)%24

        var time =""

        if(hour>0){
            time= String.format("%02d:%02d:%02d",hour,minute,second)
        }else{
            time= String.format("%02d:%02d",minute,second)
        }

        return time

    }


}