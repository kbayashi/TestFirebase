package com.example.testfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_profile_edit_radio_button.*

class UserProfileEditRadioButtonActivity : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_edit_radio_button)
        val man = activity_user_profile_edit_radio_button_man_radioButton.isChecked
        val woman = activity_user_profile_edit_radio_button_woman_radioButton.isChecked

        activity_user_profile_edit_radio_button_save_button.setOnClickListener{
            if(man == true){
                db
            }else if(woman == true){
                db
            }
        }
    }
}