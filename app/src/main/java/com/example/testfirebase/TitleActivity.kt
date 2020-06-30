package com.example.testfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class TitleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        val registerButton: Button = findViewById(R.id.title_activitiy_user_register)
        registerButton.setOnClickListener {

            //val intent = Intent(this, MainActivity::class.java)
            //アクティビティを起動する前に既存のアクティビティを削除してからスタックに追加
            //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            //startActivity(intent)

        }
        val loginButton: Button = findViewById(R.id.title_activitiy_user_login)
        loginButton.setOnClickListener {

            val intent = Intent(this, UserLoginActivity::class.java)
            startActivity(intent)

        }

    }
}