package com.example.testfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.testfirebase.UserListFragment.Companion.SELECT_USER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_chat.*

private lateinit var auth: FirebaseAuth

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //下のオブジェクトは相手のオブジェクト情報です。
        val intent = getIntent()
        val get_you = intent.getParcelableExtra<User>(SELECT_USER)
        //アクションバーに相手の名前を表記
        supportActionBar?.title = get_you.name

        send_button.setOnClickListener {
            if(message_editText.text.isEmpty()){
                Toast.makeText(applicationContext, "メッセージを入力してください", Toast.LENGTH_SHORT).show()
            }else{
                sendMessage(get_you,message_editText.text.toString())
            }
        }

    }

    // Firebaseの「user-message」コレクションにオブジェクトを登録する関数
    // 引数: you(チャット相手のユーザオブジェクト) / msg(送信するメッセージ内容)
    private fun sendMessage(you:User,msg:String){
        //自分のユーザ情報を取得
        auth = FirebaseAuth.getInstance()
        val me = auth.currentUser
        //
        val city = City(msg,me!!.uid,you.uid,System.currentTimeMillis())
        //データベースにメッセージを登録(自分Ver)
        val ref = FirebaseFirestore.getInstance().collection("user-message").document(me!!.uid).collection(you.uid).add(city)
            .addOnSuccessListener{
                Log.d("DB", "データベースにメッセージを登録しました")
            }
            .addOnFailureListener{
                Log.d("DB", "データベースにメッセージの登録が失敗しました ${it.message}")
            }
    }

    data class City(
        val message: String? = null,
        val send_user: String? = null,
        val receive_user: String? = null,
        val time: Long? = null
    )
}