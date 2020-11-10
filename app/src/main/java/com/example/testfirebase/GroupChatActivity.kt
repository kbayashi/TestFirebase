package com.example.testfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GroupChatActivity : AppCompatActivity() {

    // Firebase
    private val auth = FirebaseAuth.getInstance()
    private val me = auth.currentUser
    private val db = FirebaseFirestore.getInstance()

    // 変数
    private var gid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        // 各種コンポーネント
        val g_recy = findViewById<RecyclerView>(R.id.group_chat_recyclerView)
        val g_edit = findViewById<EditText>(R.id.group_chat_editText)
        val g_send = findViewById<Button>(R.id.group_chat_send_button)

        // グループID（前の画面からID情報を取得）
        gid = intent.getStringExtra("GroupId")

        // アクションバーの表記を変更
        setTitle("グループチャット")

        // メッセージ受信
        val docRef = db.collection("group-message").document("get").collection(gid!!).orderBy("timestamp")
        docRef.addSnapshotListener { snapshot, e ->

            // アダプタに関連付け
            val groupMessageListAdapter = groupMessageAdapter(this)

            // データを取り出す
            snapshot?.documentChanges?.forEach {

                // GroupMessage型に変換
                var groupMessagedata = it.document.toObject(GroupMessage::class.java)

                // アダプターに追加
                groupMessageListAdapter?.add(groupMessagedata)

                // 一番下にスクロール
                g_recy.scrollToPosition(groupMessageListAdapter.itemCount-1)
            }
        }

        // 送信ボタン
        g_send.setOnClickListener {

            // テキストボックスに何も記入がないときはスルーする
            if (g_edit.text.isEmpty()){
                Toast.makeText(applicationContext, "メッセージを入力してください", Toast.LENGTH_SHORT).show()
            }else{

                // メッセージをDBに送信
                send_group_message(me!!.uid, g_edit.text.toString())
                // 削除
                g_edit.text.clear()
            }
        }
    }

    // メッセージを送信する関数
    private fun send_group_message(id: String, msg: String){

        // メッセージ内容を格納
        val g_msg = GroupMessage(id, msg, System.currentTimeMillis())

        // Firebaseにメッセージを登録
        db.collection("group-message").document("get").collection(gid!!).document().set(g_msg)

    }
}