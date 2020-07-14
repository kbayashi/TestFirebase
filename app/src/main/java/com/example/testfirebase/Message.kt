package com.example.testfirebase

import java.text.SimpleDateFormat
import java.util.*

class Message(val message: String,val send_user:String,val receive_user: String,val time: Long) {
    constructor():this("", "", "", 0L)

    //メッセージを送ったタイムスタンプを時間表示にする関数
    fun sendTimestampToString(long: Long): String{
        //今日の日付を取得
        val today = System.currentTimeMillis()
        //フォーマットを指定
        var format = SimpleDateFormat("h:mm")
        //送ったメッセージが1日より前か比較
        if(long < (today - 43200000L)){
            format = SimpleDateFormat("M/d h:mm")
        }
        //ロング型からDate形に変換
        val date = Date(long)
        //フォーマット通りに表示
        return format.format(date)
    }

}