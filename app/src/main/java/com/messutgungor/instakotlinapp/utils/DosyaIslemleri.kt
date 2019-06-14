package com.messutgungor.instakotlinapp.utils

import android.os.AsyncTask
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import com.iceteck.silicompressorr.SiliCompressor
import com.messutgungor.instakotlinapp.Profile.YukleniyorFragment
import com.messutgungor.instakotlinapp.Share.CompressLoadingFragment
import com.messutgungor.instakotlinapp.Share.ShareNextFragment
import java.io.File
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class DosyaIslemleri {

    companion object {

        fun klasordekiDosyalariGetir(klasorAdi:String):ArrayList<String>{

            var tumDosyalar = ArrayList<String>()

            var file= File(klasorAdi)

            var klasordekiTumDosyalar = file.listFiles()

            //parametre olarak gonderdiğimiz klasor yolunda eleman olup olmadığı kontrol edildi
            if(klasordekiTumDosyalar!=null){

                //Aşağıdaki if bloğunda galeriden getirilen resimlerin tarihe göre sıralanmasını sağlıyoruz.
                if(klasordekiTumDosyalar.size>1){
                    Arrays.sort(klasordekiTumDosyalar, object  : Comparator<File>{
                        override fun compare(o1: File?, o2: File?): Int {
                            if(o1!!.lastModified()>o2!!.lastModified()){
                                return -1
                            }else return 1
                        }

                    })
                }

                for(i in 0..klasordekiTumDosyalar.size-1){
                    //sadece dosyalara bakıyoruz
                    if(klasordekiTumDosyalar[i].isFile){
                        //okuduğumuz dosyanın telefondaki yeri ve adının yeri
                        //files://root/logo.png
                        var okunanDosyaYolu = klasordekiTumDosyalar[i].absolutePath

                        var dosyaTuru = okunanDosyaYolu.substring(okunanDosyaYolu.lastIndexOf("."))


                        if(dosyaTuru.equals(".jpg") || dosyaTuru.equals(".jpeg") || dosyaTuru.equals(".png") || dosyaTuru.equals(".mp4")){
                            tumDosyalar.add(okunanDosyaYolu)
                        }
                    }
                }
            }
            return  tumDosyalar
        }

        fun compressResimDosya(fragment: Fragment, secilenResimYolu: String?) {
            ResimCompressAsyncTask(fragment).execute(secilenResimYolu)
        }

        fun compressVideoDosya(fragment: Fragment, secilenDosyaYolu: String?) {
            VideoCompressAsyncTask(fragment).execute(secilenDosyaYolu)
        }
    }


    internal class ResimCompressAsyncTask(fragment: Fragment) : AsyncTask<String,String,String>(){

        var myFragment:Fragment = fragment
        var compressFragment=CompressLoadingFragment()
        override fun onPreExecute() {

            compressFragment.show(myFragment.activity!!.supportFragmentManager,"compressDialogBasladi")
            compressFragment.isCancelable=false

            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): String {

            var yeniOlusanDosyaninKlasorYolu = File(Environment.getExternalStorageDirectory().absolutePath+"/DCIM/TestKlasoru/compressed")
            var yeniDosyaYolu :String=SiliCompressor.with(myFragment.context).compress(params[0],yeniOlusanDosyaninKlasorYolu)

            return yeniDosyaYolu
        }

        // doInBackground da yapılan iş bittikten sonra dönen yeni dosya yolu aşağıda onPostExecute da result olarak FilePath olarak alınıyor.

        override fun onPostExecute( filePath : String?) {
            Log.e("HATA " , "Yeni Dosyanın Yolu" + filePath )
            compressFragment.dismiss()
            (myFragment as ShareNextFragment).uploadStorage(filePath)
            super.onPostExecute(filePath)
        }
    }

    internal class VideoCompressAsyncTask(fragment: Fragment):AsyncTask<String,String,String>(){
        var myFragment = fragment
        var compressFragment = YukleniyorFragment()

        override fun onPreExecute() {
            compressFragment.show(myFragment.activity!!.supportFragmentManager,"compressDialogBasladi")
            compressFragment.isCancelable=false
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): String? {
            var yeniOlusanDosyaninKlasorYolu = File(Environment.getExternalStorageDirectory().absolutePath+"/DCIM/TestKlasoru/compressedVideo/")

            if(yeniOlusanDosyaninKlasorYolu.isDirectory || yeniOlusanDosyaninKlasorYolu.mkdirs()){
                var yeniDosyaninYolu = SiliCompressor.with(myFragment.context).compressVideo(params[0],yeniOlusanDosyaninKlasorYolu.path)
                return yeniDosyaninYolu
            }
            return null
        }

        override fun onPostExecute(yeniDosyaninYolu : String?) {

            if (!yeniDosyaninYolu.isNullOrEmpty()){
                Log.e("HATA " , "Yeni Video Dosyasının Yolu" + yeniDosyaninYolu )
                compressFragment.dismiss()
                (myFragment as ShareNextFragment).uploadStorage(yeniDosyaninYolu)
            }

            super.onPostExecute(yeniDosyaninYolu)
        }

    }
}