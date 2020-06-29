package com.example.testfirebase

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_registar.*


class UserRegistarActivity : AppCompatActivity() {


    var ragioGender:RadioButton? = null
    //ダイアログに渡す処理
    val listener:((TextView, String)->Unit)  = { TextView, String->
        TextView.text = String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_registar)

        //登録ボタン
        user_registar_button.setOnClickListener {
            ContentInspection()
        }

        //ラジオボタンタップ
        user_registar_gender_radioGroup.setOnCheckedChangeListener { radioGroup, i ->
                ragioGender = findViewById<RadioButton>(i)
                Log.d("ラジオグループ", ragioGender?.text.toString())
            }

        //病名ダイアログ表示
        user_registar_sick_textView.setOnClickListener {
            val dialog = selectDialogRadio("病名",user_registar_sick_textView)
            dialog.show(supportFragmentManager, "病名")
        }
    }

    //入力検査(まだ適当)と認証
    private fun ContentInspection(){

        val name = user_registar_name_editText.text.toString()
        val email = user_registar_email_editText.text.toString()
        val pass = user_registar_pass_editText.text.toString()
        val checkPass = user_registar_pass_check_editText.text.toString()

        if(email.isEmpty() || pass.isEmpty() || name.isEmpty() || pass != checkPass || ragioGender == null) {
            Toast.makeText(this, "入力内容が不足しています", Toast.LENGTH_SHORT).show()
            return
        }


        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener
                //認証
                Log.d("認証", "認証成功 ${it.result?.user?.uid}")
                val user = User(it.result?.user?.uid!!, name, "", ragioGender?.text.toString(),
                    "", "", "","", "")
                UserSaveDB(user)
            }
            .addOnFailureListener {
                Log.d("Main", "作成に失敗しました ${it.message}")
                Toast.makeText(this, "作成に失敗しました ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }


    //ユーザのデータをデータベースに登録
    private fun UserSaveDB(user:User){
        val ref  = FirebaseFirestore.getInstance().collection("user")
            .document(user.uid)

        ref.set(user)
            .addOnSuccessListener {
                Log.d("データベース", "データベースに保存成功 ")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("データベース", "データベースに保存失敗 ${it.message}")
            }
    }
}
