package com.example.testfirebase

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class GroupMessage(
    val send_id: String,
    val message: String,
    val log_flag: Boolean,
    val img_flag: Boolean,
    val timestamp: Long) {
    constructor():this("","",false,false,-1L)

    // タイムスタンプを時間に変換
    fun TimestampToDate(long: Long): String{

        // 今日の日付を取得
        val today = System.currentTimeMillis()

        // フォーマット
        var format: DateFormat

        // メッセージが今日以前に送信されたものか
        if (long < (today - 86400000L)) {
            format = SimpleDateFormat("M/d h:mm")
        } else {
            format = SimpleDateFormat("h:mm")
        }

        // Date型に変換
        val date = Date(long)

        return format.format(date)
    }
}