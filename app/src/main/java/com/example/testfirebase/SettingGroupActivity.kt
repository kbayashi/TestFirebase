package com.example.testfirebase

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_create_group.*
import java.util.*
import kotlin.collections.ArrayList

class SettingGroupActivity : AppCompatActivity() {

    // 画像選択
    private var selectedPhotoUri: Uri? = null
    // グループアイコン保存パス
    private var gIcon =
        "https://firebasestorage.googleapis.com/v0/b/firevasetest-1d5b9.appspot.com/o/user_icon%2Fnoimage.png?alt=media&token=b9ae62b8-8c42-4791-9507-c84c93f6871f"
    // グループID
    private lateinit var gid: String
    // ログインしているユーザの名前(招待、退会、除外の際にログ記録を残すのに使うため)
    private var me_name: String? = null

    // Firebase
    private val auth = FirebaseAuth.getInstance()
    private val me = auth.currentUser
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_group)

        // 各種コンポーネント
        val icon = findViewById<CircleImageView>(R.id.group_icon_imageView)
        val edit = findViewById<EditText>(R.id.group_name_editText)
        val cont = findViewById<TextView>(R.id.group_name_count_textView)
        val topi = findViewById<EditText>(R.id.group_topic_editText)
        val tcon = findViewById<TextView>(R.id.group_topic_count_textView)
        val add_recy = findViewById<RecyclerView>(R.id.user_add_list_recyclerView)
        val rem_recy = findViewById<RecyclerView>(R.id.user_remove_list_recyclerView)
        val subb = findViewById<Button>(R.id.submit_button)

        // リスト
        val join_members: ArrayList<String> = ArrayList()           // 追加するユーザのIDを格納
        val remove_members: ArrayList<String> = ArrayList()         // 除外するユーザのIDを格納
        val join_member_name: ArrayList<String> = ArrayList()       // 追加するユーザの名前を格納
        val remove_member_name: ArrayList<String> = ArrayList()     // 除外するユーザの名前を格納
        val group_membres: ArrayList<String> = ArrayList()          // 在籍するユーザのIDを格納

        // グループIDを取得
        gid = intent.getStringExtra("GroupId")

        // ユーザオブジェクト
        var getUser: User?

        // グループの取得
        db.collection("group").document(gid)
            .get().addOnSuccessListener { document ->
                if (document != null) {
                    setTitle(document["name"].toString() + "の編集")
                    edit.setText(document["name"].toString())
                    topi.setText(document["topic"].toString())
                    Picasso.get().load(document["icon"].toString()).into(icon)
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }

        // アダプタ(グループ作成アダプタと同じ)
        var user_add_adapter = add_remove_userAdapter(this)
        var user_remove_adapter = add_remove_userAdapter(this)

        // DBからグループメンバーの取得と表示(グループメンバーのみ)
        db.collection("group").document(gid).collection("member")
            .get().addOnSuccessListener { result ->

                // 在籍メンバー分ループ
                for (document in result) {
                    // 在籍グループをデバッグ出力
                    Log.d("Join_Member", "${document.id} => ${document.data}")
                    Log.d("Join_Member_Substring_ID", document.data.toString().substring(5, 33))
                    // グループメンバーのユーザIDを元にユーザテーブルから情報を取得
                    db.collection("user").document(document.data.toString().substring(5, 33))
                        .get().addOnSuccessListener {
                            // ユーザオブジェクトを取得
                            getUser = it.toObject(User::class.java)
                            // 自分の場合は除く
                            if (getUser!!.uid != me!!.uid) {
                                user_remove_adapter?.add(getUser!!)
                                group_membres.add(getUser!!.uid)
                            }else{
                                // 自分だった場合は、自分の名前を格納する文字列変数に保持しておく
                                me_name = getUser!!.name
                            }
                        }
                }

                // アダプタに関連付け
                rem_recy.adapter = user_remove_adapter

                // まだ追加できるユーザを取得と表示(現存の友だち - グループメンバー = 差分ユーザ)
                db.collection("user").get().addOnSuccessListener { result ->

                    // ユーザ数分ループ
                    for (document in result){
                        // ユーザオブジェクトを取得
                        getUser = document.toObject(User::class.java)
                        // フラグ変数
                        var flag: Boolean = false

                        // メンバー追加候補者を表示する処理
                        for (item in group_membres) {
                            // 既にメンバーは除外
                            if (getUser!!.uid == item) {
                                flag = true
                                break
                            }
                            // 自分の場合は除外
                            if (getUser!!.uid == me!!.uid) {
                                flag = true
                                break
                            }
                        }

                        if (flag == false){
                            // 追加
                            user_add_adapter?.add(getUser!!)
                        }
                    }
                    // アダプタに関連付け
                    add_recy.adapter = user_add_adapter
                    subb.setEnabled(true)

                }.addOnFailureListener {
                    finish()
                }

        }.addOnFailureListener {
            finish()
        }

        // 選択処理
        user_add_adapter.setOnclickListener { uid: String, name: String, bool: Boolean ->
            // 追加 or 除外
            if (bool == true) {
                join_members.add(uid)
                join_member_name.add(name)
            } else {
                join_members.remove(uid)
                join_member_name.remove(name)
            }
        }
        user_remove_adapter.setOnclickListener { uid: String, name: String, bool: Boolean ->
            // 追加 or 除外
            if (bool == true) {
                remove_members.add(uid)
                remove_member_name.add(name)
            } else {
                remove_members.remove(uid)
                remove_member_name.remove(name)
            }
        }

        // 保存ボタン
        subb.setOnClickListener {

            // グループ名の更新処理
            db.collection("group").document(gid)
                .update("name", edit.text.toString()).addOnSuccessListener {
                    Log.d("Icon Update Success", "DocumentSnapshot successfully updated!")
                }
                .addOnFailureListener {
                        e -> Log.w("Icon Update Error", "Error updating document", e)
                }

            // トピックの更新処理
            db.collection("group").document(gid)
                .update("topic", topi.text.toString()).addOnSuccessListener {
                    Log.d("Topic Update Success", "DocumentSnapshot successfully updated!")
                }
                .addOnFailureListener {
                        e -> Log.w("Topic Update Error", "Error updating document", e)
                }

            // メッセージ表示用変数
            var str = ""

            // 追加判定
            if (join_members.size != 0){
                str += "\n招待するメンバー\n"
                // 名前表示
                for (item in join_member_name) {
                    str += item
                    str += "\n"
                }
            }

            // 除外判定
            if (remove_members.size != 0) {
                str += "\n除外するメンバー\n"
                // 名前表示
                for (item in remove_member_name) {
                    str += item
                    str += "\n"
                }
            }

            // 確認表示
            if (join_members.size != 0 || remove_members.size != 0) {
                // 表示
                AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage("以下の変更を加えますがよろしいですか？\n" + str)
                    .setPositiveButton("はい") { dialog, which ->

                        //　メンバーを追加
                        if (join_members.size != 0) {
                            for (i in 0 .. join_members.size-1) {
                                // メンバー
                                data class cUser(
                                    val uid: String? = null
                                )
                                // group
                                val add_other = db.collection("group").document(gid).collection("member").document(join_members[i])
                                add_other.set(cUser(join_members[i]))

                                // データ構造
                                data class jUser(
                                    val gid: String? = null,
                                    val uid: String? = null
                                )
                                // group-status
                                db.collection("group-status").document(join_members[i]).collection("no-join").document(gid).set(jUser(gid, join_members[i]))

                                // チャット画面内にログを記録
                                send_group_message(me!!.uid, me_name + "さんが" + join_member_name[i] + "さんを招待しました")
                            }
                        }
                        // メンバーを除外
                        if (remove_members.size != 0) {
                            for (i in 0 .. remove_members.size-1) {
                                // group
                                db.collection("group").document(gid).collection("member").document(remove_members[i]).delete()
                                // group-status
                                db.collection("group-status").document(remove_members[i]).collection("join").document(gid).delete()
                                // チャット画面内にログを記録
                                send_group_message(me!!.uid, me_name + "さんが" + remove_member_name[i] + "さんを除外しました")
                            }
                        }
                        // 前の画面へ戻る
                        finish()
                    }
                    .setNegativeButton("いいえ") { dialog, which ->
                        // 何もしない
                    }
                    .show()
            } else {
                // 今の画面を木端微塵に破壊する。そんな面倒なことはさせない。
                finish()
            }
        }

        // グループアイコン変更の処理
        icon.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        // グループ名の文字数処理
        edit!!.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 文字数をカウント
                if (edit.length() > 20) {
                    // リミットになった時は赤色
                    cont.setTextColor(Color.MAGENTA)
                } else {
                    // それ以外は黒色
                    cont.setTextColor(Color.GRAY)
                }
                cont.text = edit.length().toString() + "/20"
            }
            override fun afterTextChanged(p0: Editable?) {}

        })

        // トピックの文字数処理
        topi!!.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 文字数をカウント
                if (topi.length() > 300) {
                    // リミットになった時は赤色
                    tcon.setTextColor(Color.MAGENTA)
                } else {
                    // それ以外は黒色
                    tcon.setTextColor(Color.GRAY)
                }
                tcon.text = topi.length().toString() + "/300"
            }
            override fun afterTextChanged(p0: Editable?) {}

        })

    }

    // ギャラリーから画像を選択するメソッド
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 写真選択アプリが呼び出され、ちゃんと操作して、データが入っていたら
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            // 写真をボタンの背景に設定
            Log.e("GroupIcon","Photo select")
            Log.d("GroupIcon","${data.data}")
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            group_icon_imageView.setImageBitmap(bitmap)
            uploadImageFirebaseStorage()
        }
    }

    // Firebaseに画像をアップロードするメソッド
    private fun uploadImageFirebaseStorage(){

        if(selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/group_icon/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(UserProfileEditActivity.USER_REGISTAR, "アップロード成功${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    gIcon = it.toString()
                    Log.d(UserProfileEditActivity.USER_REGISTAR, "File 場所$it")

                    // グループテーブル上のアイコンデータも更新
                    db.collection("group").document(gid)
                        .update("icon", gIcon).addOnSuccessListener {
                            Log.d("Icon Update Success", "DocumentSnapshot successfully updated!")
                        }
                        .addOnFailureListener {
                                e -> Log.w("Icon Update Error", "Error updating document", e)
                        }
                }
            }
            .addOnFailureListener{
                //do same logging here
                Log.d(UserProfileEditActivity.USER_REGISTAR, "作成に失敗しました ${it.message}")
                Toast.makeText(this, "作成に失敗しました ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.remove_me, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.remove_me -> {
                // 表示
                AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage("退会しますか？")
                    .setPositiveButton("はい") { _, _ ->
                        // group
                        db.collection("group").document(gid).collection("member").document(me!!.uid).delete()
                        // group-status
                        db.collection("group-status").document(me!!.uid).collection("join").document(gid).delete()
                        // チャット画面内にログを記録
                        send_group_message(me!!.uid, me_name + "さんが退会しました")
                        // 元の画面へ戻る
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                    }
                    .setNegativeButton("いいえ") { _, _ -> }
                    .show()
            }
        }
        return true
    }

    // メッセージを送信する関数
    private fun send_group_message(uid: String, msg: String){

        // 送信時間を確定
        val time = System.currentTimeMillis()

        // メッセージ内容を格納
        val g_msg = GroupMessage(uid, msg,true, time)

        // グループメッセージテーブルに保存
        db.collection("group-message").document("get").collection(gid!!).document().set(g_msg)
    }
}