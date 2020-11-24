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

        // ユーザオブジェクト
        var getUser: User?

        // グループIDを取得
        gid = intent.getStringExtra("GroupId")

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

        // リスト
        val delete_members: ArrayList<String> = ArrayList()
        val delete_select: ArrayList<Boolean> = ArrayList()
        val join_members: ArrayList<String> = ArrayList()
        val join_select: ArrayList<Boolean> = ArrayList()

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
                            user_remove_adapter?.add(getUser!!)
                            // リストに格納
                            delete_members.add(getUser!!.uid)
                            delete_select.add(false)
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

                        for (item in delete_members) {
                            if (item == getUser!!.uid) {
                                flag = true
                                break
                            }
                        }

                        if (flag == false){
                            // 追加
                            user_add_adapter?.add(getUser!!)
                            join_members.add(getUser!!.uid)
                            join_select.add(false)
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
        user_add_adapter.setOnclickListener { position: Int, bool: Boolean ->
            join_select[position] = bool
        }
        user_remove_adapter.setOnclickListener { position: Int, bool: Boolean ->
            delete_select[position] = bool
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

            // 追加除外フラグ
            var add_flag = false
            var remove_flag = false
            // メッセージ表示用変数
            var str = ""
            // メンバー追加処理
            for (i in 0 .. join_members.size-1) {
                // 参加者
                if (join_select[i] == true) {
                    if (add_flag == false) {
                        str += "追加するメンバー\n"
                        add_flag = true
                    }
                    str += join_members[i]
                    str += "\n"
                }
            }

            // メンバー除外処理
            for (i in 0 .. delete_members.size-1){
                // 除外
                if (delete_select[i] == true) {
                    if (remove_flag == false) {
                        str += "除外するメンバー\n"
                        remove_flag = true
                    }
                    str += delete_members[i]
                    str += "\n"
                }
            }

            // 追加除外判定
            if (add_flag == true || remove_flag == true) {
                // 表示
                AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage(str)
                    .setPositiveButton("変更") { dialog, which ->
                        // メンバー
                        data class cUser(
                            val uid: String? = null
                        )
                        //　メンバーを追加
                        if (add_flag == true) {
                            join_members.forEach {
                                db.collection("group").document(gid).collection("member").add(cUser(it))
                            }
                        }
                        // メンバーを除外
                        if (remove_flag == true) {
                            delete_members.forEach {

                            }
                        }
                    }
                    .setNegativeButton("取消", { dialog, which ->
                        // TODO:Noが押された時の挙動
                    })
                    .show()
            } else {
                // 表示
                AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage("あんたにコンティニューなんてないのさ！")
                    .setPositiveButton("OK") { dialog, which ->
                        // 今の画面を木端微塵に破壊する
                        finish()
                    }
                    .show()
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
}