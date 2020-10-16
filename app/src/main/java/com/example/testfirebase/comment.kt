package com.example.testfirebase

import java.text.SimpleDateFormat

class Comment(val id:String, val uid:String, val comment:String, val time:Long) {
    constructor():this("","","", 0L)

    fun getTime(long: Long):String{
        val format = SimpleDateFormat("MM月dd日hh時mm分")
        return  format.format(long)
    }
}