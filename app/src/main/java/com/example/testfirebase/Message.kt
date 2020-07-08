package com.example.testfirebase

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class Message(val message: String,val send_user:String,val receive_user: String,val time: Long) {
    constructor():this("", "", "", -1L)

    fun changeTimeStampToString(long:Long): String{
        val date = Date(long)
        val format = SimpleDateFormat("MM月dd日 hh時mm分")
        return format.format(date)
    }

}