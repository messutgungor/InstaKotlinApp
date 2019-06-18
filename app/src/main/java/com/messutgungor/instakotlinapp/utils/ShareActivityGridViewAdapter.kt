package com.messutgungor.instakotlinapp.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView
import com.messutgungor.instakotlinapp.R
import kotlinx.android.synthetic.main.tek_sutun_grid_resim.view.*



class ShareActivityGridViewAdapter(context: Context, resource: Int, var klasordekiDosyalar: ArrayList<String>) :
    ArrayAdapter<String>(context, resource, klasordekiDosyalar) {

    var inflater: LayoutInflater
    lateinit var viewHolder: ViewHolder
    var tek_sutun_resim: View? = null

    init {
        inflater = LayoutInflater.from(context)
    }

    inner class ViewHolder() {
        lateinit var imageView: GridViewCustomImage
        lateinit var progressBar: ProgressBar
        lateinit var tvSure : TextView
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        //inflate işlemi yoğum mermory kullandığı için işlemi hafifletmek için aşağıdaki gibi bir if bloğu ile kodumuzu değiştirdik.
        tek_sutun_resim = convertView

        if (tek_sutun_resim == null) {
            tek_sutun_resim = inflater.inflate(R.layout.tek_sutun_grid_resim, parent, false)
            viewHolder = ViewHolder()
            viewHolder.imageView = tek_sutun_resim!!.imgTekSutunImage
            viewHolder.progressBar = tek_sutun_resim!!.progressBarGridImage
            viewHolder.tvSure = tek_sutun_resim!!.tvSure

            tek_sutun_resim!!.setTag(viewHolder)
        } else {
            viewHolder=tek_sutun_resim!!.getTag() as ViewHolder
        }


        var dosyaYolu = klasordekiDosyalar.get(position)
        var dosyaTuru = dosyaYolu.substring(dosyaYolu.lastIndexOf("."))

        if(dosyaTuru.equals(".mp4")){
            viewHolder.tvSure.visibility=View.VISIBLE
            var retriver = MediaMetadataRetriever()
            retriver.setDataSource(context, Uri.parse("file://"+dosyaYolu))
            var videoSuresi = retriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            var videoSuresiLong = videoSuresi.toLong()
            viewHolder.tvSure.text=convertDuration(videoSuresiLong)
            UniversalImageLoader.setImage(klasordekiDosyalar.get(position), viewHolder.imageView,viewHolder.progressBar, "file://")
        }else{
            viewHolder.tvSure.visibility=View.GONE
            UniversalImageLoader.setImage(klasordekiDosyalar.get(position), viewHolder.imageView,viewHolder.progressBar, "file://")

        }


        return tek_sutun_resim!!
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