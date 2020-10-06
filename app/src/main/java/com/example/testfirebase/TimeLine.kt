package com.example.testfirebase

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.w3c.dom.Comment
@Parcelize
class TimeLine(val uid:String, val text:String, var good:Int, var comment: ArrayList<String>?, val imgRef:String?, val id:String) :
    Parcelable {
    constructor():this("", "",0,null,null, "")

}