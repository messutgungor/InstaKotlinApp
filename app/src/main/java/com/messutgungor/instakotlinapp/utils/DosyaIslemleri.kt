package com.messutgungor.instakotlinapp.utils

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
    }
}