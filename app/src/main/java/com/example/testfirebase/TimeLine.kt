package com.example.testfirebase

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.w3c.dom.Comment
import java.text.SimpleDateFormat

@Parcelize
class TimeLine(val uid:String, var text:String, var good:String?, var comment: String?, var imgRef:String?, val id:String, val time:Long) :
    Parcelable {
    constructor():this("", "",null, null,null, "",0L)

    fun getTime(long: Long):String{
        val format = SimpleDateFormat("MM月dd日hh時mm分")
        return  format.format(long)
    }

}