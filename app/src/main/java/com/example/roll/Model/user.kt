package com.example.roll.Model

class user {
    var name:String? = null
    var email:String? = null
    var uid:String? = null
    var message: String? = null
    var friendsList: ArrayList<String?> = arrayListOf()

    constructor(){}
    constructor(name: String?, email: String?, uid: String?) {
        this.name = name
        this.email = email
        this.uid = uid
    }

}