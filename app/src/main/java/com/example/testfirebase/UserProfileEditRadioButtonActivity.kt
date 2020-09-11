package com.example.testfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_profile_edit_plain_text.*
import kotlinx.android.synthetic.main.activity_user_profile_edit_radio_button.*

class UserProfileEditRadioButtonActivity : AppCompatActivity() {

    //自分のユーザインスタンスを生成
    private val auth = FirebaseAuth.getInstance()
    private val me = auth.currentUser
    //DB
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_edit_radio_button)
        val man = findViewById<RadioButton>(R.id.activity_user_profile_edit_radio_button_man_radioButton)
        val woman = findViewById<RadioButton>(R.id.activity_user_profile_edit_radio_button_woman_radioButton)

        activity_user_profile_edit_radio_button_save_button.setOnClickListener{
            if(man.isChecked == true){
                //更新可能
                val ref = db.collection("user").document(me!!.uid).update("gender", man.text.toString())
                    .addOnSuccessListener {
                        Log.d("DB", "更新できました")
                    }
                    .addOnFailureListener {
                        Log.d("DB", "更新できません ${it.message}")
                    }
                finish()
            }else if(woman.isChecked == true){
                //更新可能
                val ref = db.collection("user").document(me!!.uid).update("gender", woman.text.toString())
                    .addOnSuccessListener {
                        Log.d("DB", "更新できました")
                    }
                    .addOnFailureListener {
                        Log.d("DB", "更新できません ${it.message}")
                    }
                finish()
            }else{
                Toast.makeText(applicationContext, "性別を選択してください", Toast.LENGTH_SHORT).show()
            }
        }
    }
}