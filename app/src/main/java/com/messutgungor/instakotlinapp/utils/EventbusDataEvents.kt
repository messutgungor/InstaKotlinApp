package com.messutgungor.instakotlinapp.utils

import com.messutgungor.instakotlinapp.Model.Users


//Eventbus ile bilgi göndermek için buradaki sınıfı oluşturuyoruz.
class EventbusDataEvents{
    internal class KayitBilgileriniGonder(var telNo:String?, var email: String?, var verificationID:String?, var code:String?, var emailkayit:Boolean)
    internal class KullaniciBilgileriniGonder(var kullanici:Users?)
}
