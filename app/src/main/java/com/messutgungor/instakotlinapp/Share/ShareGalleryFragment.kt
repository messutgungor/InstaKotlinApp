package com.messutgungor.instakotlinapp.Share


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter

import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.utils.*
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.fragment_share_gallery.*
import kotlinx.android.synthetic.main.fragment_share_gallery.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ShareGalleryFragment : Fragment() {

    var secilenDosyaYolu:String? = null
    var dosyaTuru :String? = null
    var dosyaTuruResimMi : Boolean? = null

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
        var testKlasoru = root+"/DCIM/TestKlasoru"

        klasorPaths.add(kameraResimleri)
        klasorPaths.add(indirilenResimler)
        klasorPaths.add(whatsappResimleri)
        klasorPaths.add(testKlasoru)

        klasorAdlari.add("Kamera")
        klasorAdlari.add("İndirilenler")
        klasorAdlari.add("WhatsApp")
        klasorAdlari.add("Test Klasörü")


        var spinnerArrayAdapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, klasorAdlari)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        view.spnKlasorAdlari.adapter = spinnerArrayAdapter

        //Fragment ilk Açıldığında ilk dosyayı bize göstermesi için aşağıdaki kodu yazdık.
        view.spnKlasorAdlari.setSelection(0)


        view.spnKlasorAdlari.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
               // setupGridView(DosyaIslemleri.klasordekiDosyalariGetir(klasorPaths.get(position)))

                stupRecylerView(DosyaIslemleri.klasordekiDosyalariGetir(klasorPaths.get(position)))

            }

        }

        view.tvIleriButon.setOnClickListener {

            activity!!.anaLayout.visibility= View.GONE // fragment içerisinden direk ulaşamayız bu yüzden activity içerisinden ulaşıyoruz.
            activity!!.fragmentContainerShare.visibility=View.VISIBLE
            var transaction = activity!!.supportFragmentManager.beginTransaction() //SupportFragmentManager a bir fragment içerisinden ulaşamadığımız için activity içerisinden ulaşıyoruz.

            EventBus.getDefault().postSticky(EventbusDataEvents.PaylasilacakDosyaGonder(secilenDosyaYolu,dosyaTuruResimMi))
            view.videoView.stopPlayback()
            transaction.replace(R.id.fragmentContainerShare,ShareNextFragment())
            transaction.addToBackStack("shareNextFragmentEklendi.")
            transaction.commit()

        }


        view.imgCloseGallery.setOnClickListener {
            activity!!.onBackPressed()
        }

        return view
    }

    private fun stupRecylerView(klasordekiDosyalariGetir: ArrayList<String>) {

        var recyclerViewAdapter = ShareGalleryRecyclerAdapter(klasordekiDosyalariGetir,this.activity!!)
        recyclerViewDosyalar.adapter=recyclerViewAdapter

        var layoutManager = GridLayoutManager(this.activity!!,4)
        recyclerViewDosyalar.layoutManager = layoutManager

        recyclerViewDosyalar.setHasFixedSize(true)
        recyclerViewDosyalar.setItemViewCacheSize(30)
        recyclerViewDosyalar.isDrawingCacheEnabled=true
        recyclerViewDosyalar.drawingCacheQuality=View.DRAWING_CACHE_QUALITY_LOW

        //uygulama ilk açıldığında ilk dosyayı gösterelim
        secilenDosyaYolu=klasordekiDosyalariGetir.get(0)
        resimVeyaVideoGoster(secilenDosyaYolu!!)
    }


   /*  fun setupGridView(secilenKlasordekiDosyalar: ArrayList<String>) {
        var gridAdapter =
            ShareActivityGridViewAdapter(activity!!, R.layout.tek_sutun_grid_resim, secilenKlasordekiDosyalar)
        recyclerViewDosyalar.adapter = gridAdapter
        for (str in secilenKlasordekiDosyalar) {
            Log.e("HATA : ", str)
        }


        //ilk dosyayı gosterelim
        secilenResimYolu = secilenKlasordekiDosyalar.get(0)
        var ilkGosDosyaYolu=secilenKlasordekiDosyalar.get(0)
        resimVeyaVideoGoster(ilkGosDosyaYolu)
        Log.e("İLK DOSYA YOLU",ilkGosDosyaYolu)


        recyclerViewDosyalar.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                secilenResimYolu=secilenKlasordekiDosyalar.get(position)
                resimVeyaVideoGoster(secilenKlasordekiDosyalar.get(position))
            }
        })
    }
        */

    private fun resimVeyaVideoGoster(dosyaYolu: String) {
        dosyaTuru = dosyaYolu.substring(dosyaYolu.lastIndexOf("."))
        //file://deneme.mp4
        if(dosyaTuru!=null){
            if(dosyaTuru.equals(".mp4")){
            videoView.visibility=View.VISIBLE
            imgCropView.visibility=View.GONE
                dosyaTuruResimMi=false
            videoView.setVideoURI(Uri.parse("file://"+dosyaYolu))
            videoView.start()
        }else{
                dosyaTuruResimMi=true
                videoView.visibility=View.GONE
                imgCropView.visibility=View.VISIBLE
                UniversalImageLoader.setImage(dosyaYolu,imgCropView,null,"file://")
            }
        }

    }
    //----------------------------------EVENTBUS-----------------------------
    @Subscribe
    internal fun onSecilenDosyaEvent(secilenDosya : EventbusDataEvents.GallerySecilenDosyaYoluGonder){
        secilenDosyaYolu=secilenDosya!!.dosyaYolu!!
        resimVeyaVideoGoster(secilenDosyaYolu!!)
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
