package com.example.testfirebase

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class GroupChatActivity : AppCompatActivity() {

    // Firebase
    private val auth = FirebaseAuth.getInstance()
    private val me = auth.currentUser
    private val db = FirebaseFirestore.getInstance()

    // 変数
    private var gid: String? = null             // GroupID
    private var isJoin: Boolean = false         // 参加承認変数(true: 参加済み / false: 未参加)
    private var me_name: String? = null         // 自分の名前

    // 画像選択
    private var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        // 各種コンポーネント
        val g_recy = findViewById<RecyclerView>(R.id.group_chat_recyclerView)
        val g_edit = findViewById<EditText>(R.id.group_chat_editText)
        val g_send = findViewById<Button>(R.id.group_chat_send_button)
        val g_img_send = findViewById<Button>(R.id.group_image_send_button)
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
        db.collection("group").document(gid!!).get()
            .addOnSuccessListener {
                if (it != null) {
                    setTitle(it["name"].toString())
                    g_join_label.text = "あなたは" + it["name"].toString() + "に招待されています"
                }
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
                var message = it.document.toObject(GroupMessage::class.java)

                // 除外判定
                if (message.message.takeLast(6) == "除外しました" && message.send_id == me?.uid) {
                    // 除外されている
                    AlertDialog.Builder(this)
                        .setTitle(R.string.app_name)
                        .setMessage(message.message)
                        .setPositiveButton("はい") { dialog, which ->
                            finish()
                        }
                        .setNegativeButton("いいえ") { dialog, which ->
                            // 何もしない
                        }
                        .show()
                } else {
                    // リサイクルビューに追加
                    groupMessageListAdapter?.add(message)
                    // 一番下にスクロール
                    g_recy.scrollToPosition(groupMessageListAdapter.itemCount-1)
                }
            }
        }

        // 送信ボタン
        g_send.setOnClickListener {

            // テキストボックスに何も記入がないときはスルーする
            if (g_edit.text.isEmpty()){
                Toast.makeText(applicationContext, "メッセージを入力してください", Toast.LENGTH_SHORT).show()
            }else{
                // メッセージをDBに送信
                send_group_message(me!!.uid, g_edit.text.toString(), false, false)
                // 削除
                g_edit.text.clear()
            }
        }

        // 画像送信ボタン
        g_img_send.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        // 参加ボタン
        g_join_btn.setOnClickListener {

            // 自分の名前を取得してくる
            db.collection("user").document(me!!.uid)
                .get().addOnSuccessListener {
                    // ユーザオブジェクトを取得
                    val user: User? = it.toObject(User::class.java)
                    me_name = user!!.name
                }

            // メンバー
            data class cUser(
                val uid: String? = null
            )
            // データ構造
            data class jUser(
                val gid: String? = null,
                val uid: String? = null
            )

            AlertDialog.Builder(this) // FragmentではActivityを取得して生成
                .setTitle(R.string.app_name)
                .setMessage("グループに参加しますか？")
                .setPositiveButton("はい") { dialog, which ->
                    // no-joinレコードを削除
                    db.collection("group-status").document(me!!.uid).collection("no-join").document(gid!!).delete()
                    // inviteレコードを削除
                    db.collection("group").document(gid!!).collection("invite").document(me.uid).delete()
                    // joinレコードを追加
                    db.collection("group-status").document(me!!.uid).collection("join").document(gid!!).set(jUser(gid, me.uid))
                    // memberレコードを追加
                    db.collection("group").document(gid!!).collection("member").document(me.uid).set(cUser(me.uid))
                    // チャット画面内にログを記録
                    send_group_message(me!!.uid, me_name + " さんが参加しました", true, false)
                    // スタックからこの画面を削除する
                    finish()
                    // 引数を割り当てる
                    intent.putExtra("GroupId", gid)
                    intent.putExtra("isJoin", true)
                    // 遷移
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

    // 右上のグループ設定ボタン
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
    private fun send_group_message(uid: String, msg: String, log_flag: Boolean, img_flag: Boolean) {

        // メッセージインスタンス
        var g_msg: GroupMessage?
        // 送信時間を確定
        val time = System.currentTimeMillis()

        // メッセージ内容を格納
        if (log_flag == true) {
            // ログの場合
            g_msg = GroupMessage(uid, msg, true, false, time)
        } else {
            if (img_flag == true) {
                // 画像の場合
                g_msg = GroupMessage(uid, msg, false, true, time)
            } else {
                // メッセージの場合
                g_msg = GroupMessage(uid, msg, false, false, time)
            }

            // 最新トークデータに格納する型準備
            var les: Message?
            if (img_flag == true) {
                les = Message("画像を送信しました", gid!!, me!!.uid, time,true,true)
            } else {
                les = Message(msg, gid!!, me!!.uid, time,false,true)
            }
            // 最新トークデータを更新する処理
            db.collection("group").document(gid!!).collection("member").get()
                .addOnSuccessListener {
                    for (item in it) {
                        // 対象ユーザにLatest更新
                        db.collection("user-latest").document("les")
                            .collection(item.data.toString().substring(5, 33)).document(gid!!)
                            .set(les)
                    }
                }
        }
        // グループメッセージテーブルに保存
        db.collection("group-message").document("get").collection(gid!!).document().set(g_msg)
    }

    // ギャラリーから画像を選択するメソッド
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 写真選択アプリが呼び出され、ちゃんと操作して、データが入っていたら
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            // 写真をボタンの背景に設定
            // Log.e("GroupIcon","Photo select")
            // Log.d("GroupIcon","${data.data}")
            selectedPhotoUri = data.data
            uploadImageFirebaseStorage()
        }
    }

    // Firebaseに画像をアップロードするメソッド
    private fun uploadImageFirebaseStorage(){

        if(selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/chat_img/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(UserProfileEditActivity.USER_REGISTAR, "アップロード成功${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    send_group_message(me!!.uid,it.toString(),false, true)
                    Log.d(UserProfileEditActivity.USER_REGISTAR, "File 場所$it")
                    // FirebaseFirestore.getInstance().collection("user").document(my_user!!.uid).set(my_user!!)
                }
            }
            .addOnFailureListener{
                //do same logging here
                Log.d(UserProfileEditActivity.USER_REGISTAR, "作成に失敗しました ${it.message}")
                Toast.makeText(this, "作成に失敗しました ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}