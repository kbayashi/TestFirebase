package com.example.testfirebase

import java.text.SimpleDateFormat
import java.util.*

class Message(val message: String,val send_user:String,val receive_user: String,val time: Long, val group_flag: Boolean) {
    constructor():this("", "", "", -1L, false)
  
    //メッセージを送ったタイムスタンプを時間表示にする関数
    fun sendTimestampToString(long: Long): String{
        //今日の日付を取得
        val today = System.currentTimeMillis()
        //フォーマットを指定
        var format = SimpleDateFormat("h:mm")
        //送ったメッセージが1日より前か比較
        if(long < (today - 86400000L)){
            format = SimpleDateFormat("M/d h:mm")
        }
        //ロング型からDate形に変換
        val date = Date(long)
        //フォーマット通りに表示
        return format.format(date)
    }
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