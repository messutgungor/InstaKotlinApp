package com.messutgungor.instakotlinapp.Model

class Posts {
    var user_id:String?=null
    var post_id:String?=null
    var yuklenme_tarihi:String?=null
    var aciklama:String?=null
    var file_url:String?=null

    constructor()
    constructor(user_id: String?, post_id: String?, yuklenme_tarihi: String?, aciklama: String?, file_url: String?) {
        this.user_id = user_id
        this.post_id = post_id
        this.yuklenme_tarihi = yuklenme_tarihi
        this.aciklama = aciklama
        this.file_url = file_url
    }

    override fun toString(): String {
        return "Posts(user_id=$user_id, post_id=$post_id, yuklenmeTarihi=$yuklenme_tarihi, aciklama=$aciklama, photo_url=$file_url)"
    }


}