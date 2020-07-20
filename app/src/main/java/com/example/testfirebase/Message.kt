package com.example.testfirebase

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class Message(val message: String,val send_user:String,val receive_user: String,val time: Long) {
    constructor():this("", "", "", -1L)
    //最新のメッセージ画面でロング型のタイムスタンプを 文字に変換して返す
    fun changeTimeStampToString(long:Long): String{
        var format = SimpleDateFormat("h:mm")
        val today = System.currentTimeMillis()
        if(long < (today - 172800000L)){
            format = SimpleDateFormat("M/d")
        }else if(long < (today - 86400000L)){
            return "昨日"
        }
        val date = Date(long)
        return format.format(date)
    }
}