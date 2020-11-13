package com.example.testfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        // アダプタの設定
        var groupMessageListAdapter = groupMessageAdapter(this)

        // グループID（前の画面からID情報を取得）
        gid = intent.getStringExtra("GroupId")

        // アクションバーの表記を変更
        val title = db.collection("group").document(gid!!)
        title.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    setTitle(document["name"].toString())
                }
            }
            .addOnFailureListener { exception ->
                setTitle("グループチャット")
            }

        // メッセージ受信
        val docRef = db.collection("group-message").document("get").collection(gid!!).orderBy("timestamp")
        docRef.addSnapshotListener { snapshot, e ->

            // アダプタに関連付け
            g_recy.adapter = groupMessageListAdapter

            // データを取り出す
            snapshot?.documentChanges?.forEach {

                // GroupMessage型に変換
                var groupMessagedata = it.document.toObject(GroupMessage::class.java)
                // リサイクルビューに追加
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
    private fun send_group_message(uid: String, msg: String){

        // 送信時間を確定
        val time = System.currentTimeMillis()

        // メッセージ内容を格納
        val g_msg = GroupMessage(uid, msg, time)

        // 最新トークデータに格納する型
        val les = Message(msg, gid!!, me!!.uid, time, true)

        // グループメッセージテーブルに保存
        db.collection("group-message").document("get").collection(gid!!).document().set(g_msg)
        // 最新メッセージテーブルにも保存
        db.collection("user-latest").document("les").collection(uid).document(gid!!).set(les)
    }
}