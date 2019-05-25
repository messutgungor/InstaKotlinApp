package com.messutgungor.instakotlinapp.utils


//Eventbus ile bilgi göndermek için buradaki sınıfı oluşturuyoruz.
class EventbusDataEvents{
    internal class KayitBilgileriniGonder(var telNo:String?, var email: String?, var verificationID:String?, var code:String?, var emailkayit:Boolean)
}
