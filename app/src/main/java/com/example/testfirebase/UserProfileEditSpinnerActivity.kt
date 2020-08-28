package com.example.testfirebase

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_profile_edit_spinner.*

@SuppressWarnings("ResourceType")
class UserProfileEditSpinnerActivity : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_edit_spinner)
        val spinner = findViewById<Spinner>(R.id.activity_user_profile_edit_spinner_spinner)
        val edit = intent.getStringExtra("edit")

        if(edit == "old"){

        }else if(edit == "live"){
            val adapter = ArrayAdapter.createFromResource(this,R.array.prefecture,android.R.layout.simple_spinner_item)
            spinner.adapter = adapter
        }else{

        }
    }
}