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
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_create_group.group_icon_imageView
import kotlinx.android.synthetic.main.activity_setting_group.*
import java.util.*
import kotlin.collections.ArrayList

class SettingGroupActivity : AppCompatActivity() {

    // Firebase
    private val auth = FirebaseAuth.getInstance()
    private val me = auth.currentUser
    private val db = FirebaseFirestore.getInstance()

    // 画像選択
    private var selectedPhotoUri: Uri? = null
    // グループアイコン保存パス
    private var gicon =
        "https://firebasestorage.googleapis.com/v0/b/firevasetest-1d5b9.appspot.com/o/user_icon%2Fnoimage.png?alt=media&token=b9ae62b8-8c42-4791-9507-c84c93f6871f"
    // グループID
    private lateinit var gid: String
    // グループ名
    private lateinit var gname: String
    // トピック
    private lateinit var gtopic: String
    // ログイン中のユーザの名前
    private var me_name: String? = null

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
        val inv_recy = findViewById<RecyclerView>(R.id.user_invite_list_recyclerView)
        val subb = findViewById<Button>(R.id.submit_button)

        // 配列リスト(現状の所属メンバー、招待中のユーザ、招待可能ユーザを保持) : RecyclerViewの時に使用
        val candidate_list: ArrayList<String> = ArrayList()             // 候補者の絞り込みをする一時的な配列
        val member_list: ArrayList<String> = ArrayList()                // 在籍するユーザのIDを格納する一時的な配列
        val inviting_list: ArrayList<String> = ArrayList()              // 招待中のユーザIDを格納する一時的な配列

        // 配列リスト(追加、除外、招待キャンセルするユーザを格納、保持) : Firebaseに追加、削除、更新を加える時に使用
        val join_members: ArrayList<String> = ArrayList()               // 追加するユーザのIDを格納
        val join_members_name: ArrayList<String> = ArrayList()          // 追加するユーザの名前を格納
        val remove_members: ArrayList<String> = ArrayList()             // 除外するユーザのIDを格納
        val remove_members_name: ArrayList<String> = ArrayList()        // 除外するユーザの名前を格納
        val invite_members: ArrayList<String> = ArrayList()             // 招待中のユーザIDを格納
        val invite_members_name: ArrayList<String> = ArrayList()        // 招待中のユーザの名前を格納

        // アダプタ(グループ作成アダプタと同じ)
        var user_add_adapter = add_remove_userAdapter(this)
        var user_remove_adapter = add_remove_userAdapter(this)
        var user_invite_adapter = add_remove_userAdapter(this)

        // ユーザオブジェクト
        var getUser: User?

        // グループIDを取得
        gid = intent.getStringExtra("GroupId")

        // グループ情報の取得
        db.collection("group").document(gid).get()
            .addOnSuccessListener {
                if (it != null) {
                    gname = it["name"].toString()
                    setTitle(gname + "の編集")
                    edit.setText(gname)
                    gtopic = it["topic"].toString()
                    topi.setText(gtopic)
                    Picasso.get().load(it["icon"].toString()).into(icon)
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }

        // グループに所属しているメンバーの取得と表示
        db.collection("group").document(gid).collection("member").get()
            .addOnSuccessListener {
                // 在籍メンバー分ループ
                for (document in it) {
                    // 在籍グループをデバッグ出力
                    Log.d("Join_Member_NAME", "${document.id} => ${document.data}")
                    Log.d("Join_Member_ID", document.data.toString().substring(5, 33))
                    // グループメンバーのユーザIDを元にユーザテーブルから情報を取得
                    db.collection("user").document(document.data.toString().substring(5, 33)).get()
                        .addOnSuccessListener {
                            // ユーザオブジェクトを取得
                            getUser = it.toObject(User::class.java)
                            // 自分の場合は除く
                            if (getUser!!.uid != me!!.uid) {
                                // ArrayList
                                member_list.add(getUser!!.uid)
                                // アダプタに関連付け
                                user_remove_adapter?.add(getUser!!)
                                rem_recy.adapter = user_remove_adapter
                            } else {
                                // 自分だった場合は、自分の名前を格納する文字列変数に保持しておく
                                me_name = getUser!!.name
                            }
                        }
                }
                // タイトル変更
                user_remove_list_textView.setText("除外(" + member_list.size + ")")
            }

        // 招待中のユーザの取得と表示
        db.collection("group").document(gid).collection("invite").get()
            .addOnSuccessListener {
                // 招待ユーザー分ループ
                for (document in it) {
                    // 在籍グループをデバッグ出力
                    Log.d("Invite_Member", "${document.id} => ${document.data}")
                    Log.d("Invite_Member_Substring_ID", document.data.toString().substring(5, 33))
                    // 招待したユーザのIDを元にユーザテーブルから情報を取得
                    db.collection("user").document(document.data.toString().substring(5, 33)).get()
                        .addOnSuccessListener {
                            // ユーザオブジェクトを取得
                            getUser = it.toObject(User::class.java)
                            // ArrayList
                            inviting_list.add(getUser!!.uid)
                            // アダプタに関連付け
                            user_invite_adapter?.add(getUser!!)
                            inv_recy.adapter = user_invite_adapter
                        }
                }
                // タイトル変更
                user_invite_list_textView.setText("招待中(" + inviting_list.size + ")")
            }

        // グループに招待するユーザを取得、処理、表示
        db.collection("user").get().addOnSuccessListener {
            // すべてのユーザを取得
            for (document in it) {
                // ユーザオブジェクトに変換
                getUser = document.toObject(User::class.java)
                // 自分は除く
                if (getUser!!.uid != me!!.uid) {
                    candidate_list.add(getUser!!.uid)
                }
            }
            // 全てのユーザを取得後(自分を除く)、招待可能なユーザを絞り込む(所属メンバー、招待中のユーザは除く)
            candidate_list.forEach {
                member_list.forEach { member ->
                    // 所属するメンバーと被った場合は除く
                    if (it == member) {
                        candidate_list.remove(it)
                    }
                }
                inviting_list.forEach { inviter ->
                    // 招待中のユーザと被った場合は除く
                    if (it == inviter) {
                        candidate_list.remove(it)
                    }
                }
            }
            // 招待可能ユーザを絞り込んだ後はRecyclerViewに表示
            candidate_list.forEach {
                db.collection("user").document(it).get()
                    .addOnSuccessListener {
                        // ユーザオブジェクト型に変換
                        getUser = it.toObject(User::class.java)
                        user_add_adapter?.add(getUser!!)
                        // アダプタに関連付け
                        add_recy.adapter = user_add_adapter
                    }
            }
            // タイトル変更
            user_add_list_textView.setText("招待(" + candidate_list.size + ")")
            // レイアウト表示判定
            if (candidate_list.size == 0) {
                user_add_list_linearLayout.visibility = View.GONE
            }
        }

        // レイアウト表示判定
        /*if (candidate_list.size == 0) {
            user_add_list_linearLayout.visibility = View.GONE
        }
        if (member_list.size == 0) {
            user_remove_list_linearLayout.visibility = View.GONE
        }
        if (inviting_list.size == 0) {
            user_invite_list_linearLayout.visibility = View.GONE
        }*/

        // 選択処理
        user_add_adapter.setOnclickListener { uid: String, name: String, bool: Boolean ->
            // 追加 or 消去
            if (bool) {
                // 既に追加していた場合は省く
                var same_flag = false
                for (item in join_members) {
                    if (item == uid) {
                        same_flag = true
                        // 既にあった場合はループする必要がないので抜ける
                        break
                    }
                }

                // まだ追加されていなかった場合は、追加する
                if (same_flag == false) {
                    join_members.add(uid)
                    join_members_name.add(name)
                }

            } else {
                join_members.remove(uid)
                join_members_name.remove(name)
            }
        }
        user_remove_adapter.setOnclickListener { uid: String, name: String, bool: Boolean ->
            // 追加 or 消去
            if (bool) {
                // 既に追加していた場合は省く
                var same_flag = false
                for (item in remove_members) {
                    if (item == uid) {
                        same_flag = true
                        // 既にあった場合はループから抜ける
                        break
                    }
                }

                // また追加されていない場合は追加する
                if (same_flag == false) {
                    remove_members.add(uid)
                    remove_members_name.add(name)
                }

            } else {
                remove_members.remove(uid)
                remove_members_name.remove(name)
            }
        }
        user_invite_adapter.setOnclickListener { uid: String, name: String, bool: Boolean ->
            // 追加 or 消去
            if (bool) {
                // 既に追加されていた場合は除く
                var same_flag = false
                for (item in invite_members) {
                    if (item == uid) {
                        same_flag = true
                        // 既にあった場合はループから抜ける
                        break
                    }
                }

                // まだ追加されていない場合は追加する
                if (same_flag == false) {
                    invite_members.add(uid)
                    invite_members_name.add(name)
                }

            } else {
                invite_members.remove(uid)
                invite_members_name.remove(name)
            }
        }

        // 保存ボタン
        subb.setOnClickListener {

            // ダイアログメッセージ用 文字列変数
            var str = ""

            // グループ名に変更があるか確認
            if (gname != edit.text.toString()) {
                str += "\nグループ名\n"
                str += edit.text.toString() + "\n"
            }

            // トピックに変更があるか確認
            if (gtopic != topi.text.toString()) {
                str += "\nトピック\n"
                str += topi.text.toString() + "\n"
            }

            // 追加判定
            if (join_members.size != 0){
                str += "\n招待するユーザ\n"
                // 名前表示
                for (item in join_members_name) {
                    str += item
                    str += "\n"
                }
            }

            // 除外判定
            if (remove_members.size != 0) {
                str += "\n除外するユーザ\n"
                // 名前表示
                for (item in remove_members_name) {
                    str += item
                    str += "\n"
                }
            }

            // 招待キャンセル判定
            if (invite_members.size != 0) {
                str += "\n招待をキャンセルするユーザ\n"
                // 名前表示
                for (item in invite_members_name) {
                    str += item
                    str += "\n"
                }
            }

            // 確認表示
            if (join_members.size != 0 || remove_members.size != 0 || invite_members.size != 0 || gname != edit.text.toString() || gtopic != topi.text.toString()) {
                // 表示
                AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage("以下の変更を加えますがよろしいですか？\n" + str)
                    .setPositiveButton("はい") { dialog, which ->

                        // グループ名の更新処理を行う
                        if (gname != edit.text.toString()) {
                            db.collection("group").document(gid)
                                .update("name", edit.text.toString()).addOnSuccessListener {
                                    Log.d("Icon Update Success", "DocumentSnapshot successfully updated!")
                                }
                                .addOnFailureListener {
                                        e -> Log.w("Icon Update Error", "Error updating document", e)
                                }

                            // グループ名変更ログを出力する
                            send_group_message(me!!.uid, me_name + " さんがグループ名を " + gname + " から " + edit.text.toString() + " に変更しました")
                        }

                        // トピックの更新処理を行う
                        if (gtopic != topi.text.toString()) {
                            db.collection("group").document(gid)
                                .update("topic", topi.text.toString()).addOnSuccessListener {
                                    Log.d("Topic Update Success", "DocumentSnapshot successfully updated!")
                                }
                                .addOnFailureListener {
                                        e -> Log.w("Topic Update Error", "Error updating document", e)
                                }

                            // トピック変更ログを出力する
                            send_group_message(me!!.uid, me_name + " さんがグループトピックを " + gtopic + " から " + topi.text.toString() + " に変更しました")
                        }

                        //　メンバーの招待
                        if (join_members.size != 0) {
                            for (i in 0 .. join_members.size-1) {
                                // メンバー
                                data class cUser(
                                    val uid: String? = null
                                )
                                // データ構造
                                data class jUser(
                                    val gid: String? = null,
                                    val uid: String? = null
                                )

                                // group
                                val add_other = db.collection("group").document(gid).collection("invite").document(join_members[i])
                                add_other.set(cUser(join_members[i]))

                                // group-status
                                db.collection("group-status").document(join_members[i]).collection("no-join").document(gid).set(jUser(gid, join_members[i]))

                                // チャット画面内にログを記録
                                send_group_message(me!!.uid, me_name + " さんが " + join_members_name[i] + " さんを招待しました")
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
                                send_group_message(me!!.uid, me_name + " さんが " + remove_members_name[i] + " さんを除外しました")
                            }
                        }
                        // 招待をキャンセル
                        if (invite_members.size != 0) {
                            for (i in 0 .. invite_members.size-1) {
                                // group
                                db.collection("group").document(gid).collection("invite").document(invite_members[i]).delete()
                                // group-status
                                db.collection("group-status").document(invite_members[i]).collection("no-join").document(gid).delete()
                                // チャット画面内にログを記録
                                send_group_message(me!!.uid, me_name + " さんが " + invite_members_name[i] + " さんの招待をキャンセルしました")
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

        // すべての処理が完遂した場合に保存ボタンを有効化する
        subb.setEnabled(true)
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
                    gicon = it.toString()
                    Log.d(UserProfileEditActivity.USER_REGISTAR, "File 場所$it")

                    // グループテーブル上のアイコンデータも更新
                    db.collection("group").document(gid)
                        .update("icon", gicon).addOnSuccessListener {
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
                        send_group_message(me!!.uid, me_name + " さんが退会しました")
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