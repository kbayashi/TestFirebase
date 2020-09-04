package com.example.testfirebase

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import kotlinx.android.parcel.Parcelize
import java.lang.IllegalArgumentException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
@Parcelize
class User (val uid:String, val name:String, val img:String,
            val gender:String, val birthday: String, val pr:String,
            val live: String, val sick:String, val life_expectancy: String,
            val hoby: String) :Parcelable{

    constructor():this("", "", "", "",
        "", "", "", "",
        "", "")


    //年齢算出
    fun ageCalculation(birthday:String): String {

        val now = Calendar.getInstance()
        val birthdayDate = birthday.toDate()
        val birthdayCalender = Calendar.getInstance()
        birthdayCalender.time = birthdayDate

        var age: Int = (now.get(Calendar.YEAR) - birthdayCalender.get(Calendar.YEAR))
        if(now.get(Calendar.DAY_OF_YEAR) < birthdayCalender.get(Calendar.DAY_OF_YEAR)) {
            age -= 1
        }

        Log.d("Test", age.toString())

        return age.toString()
    }

    private fun String.toDate(pattern: String = "yyyy/MM/dd"): Date?{
        return try{
            SimpleDateFormat(pattern).parse(this)
        }catch (e: IllegalArgumentException){
            return null
        }catch (e: ParseException){
            return null
        }
    }

}