package com.example.testfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.RadioButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_profile_edit_radio_button.*

class UserProfileEditRadioButtonActivity : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_edit_radio_button)
        val man = findViewById<RadioButton>(R.id.activity_user_profile_edit_radio_button_man_radioButton)
        val woman = findViewById<RadioButton>(R.id.activity_user_profile_edit_radio_button_woman_radioButton)

        activity_user_profile_edit_radio_button_save_button.setOnClickListener{
            if(man.isChecked == true){

                finish()
            }else if(woman.isChecked == true){

                finish()
            }
        }
    }
}