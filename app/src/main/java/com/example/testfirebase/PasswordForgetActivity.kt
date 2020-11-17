package com.example.testfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_password_forget.*

class PasswordForgetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_forget)

        //パスワード再設定をメールを送信
        password_forget_button.setOnClickListener {

            if(password_forget_editTextTextEmailAddress.text.length > 0) {
                password_forget_editTextTextEmailAddress.text.toString()
                FirebaseAuth.getInstance()
                    .sendPasswordResetEmail(password_forget_editTextTextEmailAddress.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            finish()
                        }
                    }
            }
        }
    }
}