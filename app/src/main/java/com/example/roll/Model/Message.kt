package com.example.roll.Model

class Message {
    var message:String? = null
    var senderID:String? = null
    var receiverID: String? = null
    var timeStamp: String? = null

    constructor(){}
    constructor(message: String?, senderID: String?, receiverID: String?, timeStamp: String?) {
        this.message = message
        this.senderID = senderID
        this.receiverID = receiverID
        this.timeStamp = timeStamp
    }

}