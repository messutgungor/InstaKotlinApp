package com.messutgungor.instakotlinapp.Share


import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter

import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.utils.DosyaIslemleri
import com.messutgungor.instakotlinapp.utils.ShareActivityGridViewAdapter
import com.messutgungor.instakotlinapp.utils.UniversalImageLoader
import kotlinx.android.synthetic.main.fragment_share_gallery.*
import kotlinx.android.synthetic.main.fragment_share_gallery.view.*

class ShareGalleryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_share_gallery, container, false)
        // Inflate the layout for this fragment

        var klasorPaths = ArrayList<String>()
        var klasorAdlari = ArrayList<String>()

        var root = Environment.getExternalStorageDirectory().path

        var kameraResimleri = root + "/DCIM/Camera"
        var indirilenResimler = root + "/Download"
        var whatsappResimleri = root + "/WhatsApp/Media/WhatsApp Images"

        klasorPaths.add(kameraResimleri)
        klasorPaths.add(indirilenResimler)
        klasorPaths.add(whatsappResimleri)

        klasorAdlari.add("Kamera")
        klasorAdlari.add("İndirilenler")
        klasorAdlari.add("WhatsApp")

        var spinnerArrayAdapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, klasorAdlari)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        view.spnKlasorAdlari.adapter = spinnerArrayAdapter

        //Fragment ilk Açıldığında ilk dosyayı bize göstermesi için aşağıdaki kodu yazdık.
        view.spnKlasorAdlari.setSelection(0)


        view.spnKlasorAdlari.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setupGridView(DosyaIslemleri.klasordekiDosyalariGetir(klasorPaths.get(position)))
            }

        }



        return view
    }


    fun setupGridView(secilenKlasordekiDosyalar: ArrayList<String>) {
        var gridAdapter =
            ShareActivityGridViewAdapter(activity!!, R.layout.tek_sutun_grid_resim, secilenKlasordekiDosyalar)
        gridResimlerGallery.adapter = gridAdapter
        for (str in secilenKlasordekiDosyalar) {
            Log.e("HATA : ", str)
        }


        //ilk dosyayı gosterelim
        var ilkGosDosyaYolu=secilenKlasordekiDosyalar.get(0)
        resimVeyaVideoGoster(ilkGosDosyaYolu)
        Log.e("İLK DOSYA YOLU",ilkGosDosyaYolu)


        gridResimlerGallery.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                resimVeyaVideoGoster(secilenKlasordekiDosyalar.get(position))
            }
        })
    }

    private fun resimVeyaVideoGoster(dosyaYolu: String) {
        var dosyaTuru = dosyaYolu.substring(dosyaYolu.lastIndexOf("."))
        //file://deneme.mp4
        if(dosyaTuru!=null){
            if(dosyaTuru.equals(".mp4")){
            videoView.visibility=View.VISIBLE
            imgCropView.visibility=View.GONE
            videoView.setVideoURI(Uri.parse("file://"+dosyaYolu))
            videoView.start()
        }else{
                videoView.visibility=View.GONE
                imgCropView.visibility=View.VISIBLE
                UniversalImageLoader.setImage(dosyaYolu,imgCropView,null,"file://")
            }
        }

    }
}
