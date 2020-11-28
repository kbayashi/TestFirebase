package com.example.testfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_group_chat.*

class GroupChatActivity : AppCompatActivity() {

    // Firebase
    private val auth = FirebaseAuth.getInstance()
    private val me = auth.currentUser
    private val db = FirebaseFirestore.getInstance()

    // グループID
    private var gid: String? = null

    // 参加承認変数(false: 未参加 / true: 参加済み)
    private var isJoin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        // 各種コンポーネント
        val g_recy = findViewById<RecyclerView>(R.id.group_chat_recyclerView)
        val g_edit = findViewById<EditText>(R.id.group_chat_editText)
        val g_send = findViewById<Button>(R.id.group_chat_send_button)
        val g_join_layout = findViewById<LinearLayout>(R.id.group_join_layout)
        val g_join_label = findViewById<TextView>(R.id.group_join_text)
        val g_join_btn = findViewById<Button>(R.id.group_join_button)
        val g_chat_layout = findViewById<LinearLayout>(R.id.chat_layout)

        // アダプタの設定
        var groupMessageListAdapter = groupMessageAdapter(this)

        // グループID（前の画面からID情報を取得）
        gid = intent.getStringExtra("GroupId")
        isJoin = intent.getBooleanExtra("isJoin", false)

        // アクションバーの表記を変更
        val title = db.collection("group").document(gid!!)
        title.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    setTitle(document["name"].toString())
                    g_join_label.text = "あなたは" + document["name"].toString() + "に招待されています"
                }
            }
            .addOnFailureListener { exception ->
                setTitle("グループチャット")
            }

        // 参加済みユーザか判定
        if (isJoin) {
            g_edit.setEnabled(true)
            g_send.setEnabled(true)
        } else {
            // 隠れていた参加ボタンを表示
            g_join_layout.visibility = View.VISIBLE
            // メッセージ入力欄を非表示
            g_chat_layout.visibility = View.GONE
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

        // 参加ボタン
        g_join_btn.setOnClickListener {
            AlertDialog.Builder(this) // FragmentではActivityを取得して生成
                .setTitle(R.string.app_name)
                .setMessage("グループに参加しますか？")
                .setPositiveButton("はい") { dialog, which ->
                    // no-joinレコードを削除
                    // joinレコードを追加
                    // 画面を再読み込みする
                    finish()
                    startActivity(intent)
                }
                .setNegativeButton("いいえ") { dialog, which ->
                    // 何もしない
                }
                .show()
        }
    }

    // 配置
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        // 正式に参加しているユーザか判定
        if (isJoin == true) {
            menuInflater.inflate(R.menu.group_chat_menu, menu)
            return true
        } else {
            // 右上の設定アイコンを表示しない
            return false
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting -> {
                // グループ設定画面へ移動
                val intent = Intent(this, SettingGroupActivity::class.java)
                intent.putExtra("GroupId", gid)
                startActivity(intent)
            }
        }
        return true
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