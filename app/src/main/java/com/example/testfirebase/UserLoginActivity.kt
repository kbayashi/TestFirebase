package com.example.testfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.android.synthetic.main.activity_user_login.*


class UserLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)

        var email:String = "";
        var password:String = "";

        val toastButton: Button = findViewById(R.id.user_login_login_Button)
        toastButton.setOnClickListener {
            email = user_login_user_mail_editText.text.toString()
            password = user_login_user_pass_editText.text.toString()

            if(email.isEmpty() || password.isEmpty()) {
                user_login_user_alarm_textView.text = "値が無効です"
                Log.d("通ってます", "aaaa")
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(!it.isSuccessful){
                        Log.d("ログインできません", "aaaa")
                        return@addOnCompleteListener
                    }
                    Log.d("ログイン" ,"ログイン成功")
                    val intent = Intent(this, MainActivity::class.java)
                    //アクティビティを起動する前に既存のアクティビティを削除してからスタックに追加
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }.addOnFailureListener {
                    Log.d("例外", "$it")
                    try{
                        throw  it.fillInStackTrace()
                    }catch (e:FirebaseNetworkException){
                        user_login_user_alarm_textView.text = "ネットワークに接続できません"
                    }catch (e:FirebaseAuthInvalidUserException){
                        user_login_user_alarm_textView.text = "メールアドレスまたはパスワードが無効です"
                    }
                }

        }

        //パスワードを忘れた場合
        user_login_forget_textView.setOnClickListener {

            val intent = Intent(this, PasswordForgetActivity::class.java)
            startActivity(intent)

        }

    }
}