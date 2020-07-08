package com.example.testfirebase

class Message(val message: String,val send_user:String,val receive_user: String,val time: Long) {
    constructor():this("", "", "", 0L)
}