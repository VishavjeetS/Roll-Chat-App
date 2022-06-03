package com.example.roll.Model

class Contact{
    var name: String? = null
    var uid: String? = null

    constructor(){}
    constructor(name: String?, uid: String?){
        this.name = name
        this.uid = uid
    }
}