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
    private var gIcon = "none"

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

        // Firebase
        val auth = FirebaseAuth.getInstance()
        val me = auth.currentUser
        val db = FirebaseFirestore.getInstance()

        // ユーザオブジェクト
        var loginUser: User?
        var getUser: User?

        // グループIDを取得
        val gid = intent.getStringExtra("GroupId")

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

        // まだ追加できるユーザを取得と表示(友だち-グループメンバーの差分ユーザ)
        val add_User = db.collection("user").document(me!!.uid)
        add_User.get().addOnSuccessListener {
            loginUser = it.toObject(User::class.java)
            Log.d("LOGIN_USER", "${it.data}")
            Log.d("LOGIN_USER", "${loginUser?.name}")

            val users = db.collection("user")
            users.get().addOnSuccessListener {
                it.forEach {
                    // 自分のユーザオブジェクトを取得
                    getUser = it.toObject(User::class.java)
                    Log.d("GET_USER","${it.toObject(User::class.java)}")
                    Log.d("GET_USER","${getUser!!.name}")

                    if(!(loginUser?.name == getUser!!.name)) {
                        user_add_adapter?.add(getUser!!)
                        Log.d("ADD_USER", "${getUser}")
                    }
                }
                // アダプタに関連付け
                add_recy.adapter = user_add_adapter

            }.addOnFailureListener {
                finish()
            }

        }.addOnFailureListener {
            finish()
        }

        // DBからグループメンバーを取得と表示(グループメンバーのみ)
        val g_members = db.collection("group").document(gid).collection("member")
        g_members.get().addOnSuccessListener { result ->

            // 在籍メンバー分ループ
            for (document in result) {
                // 在籍グループをデバッグ出力
                Log.d("Join_Member", "${document.id} => ${document.data}")
                Log.d("Join_Member_Substring_ID", document.data.toString().substring(5, 33))
                // ユーザオブジェクトと関連付ける
                db.collection("user").document(document.data.toString().substring(5, 33))
                    .get().addOnSuccessListener {
                        // 自分のユーザオブジェクトを取得
                        getUser = it.toObject(User::class.java)
                        // 自分は除く
                        if(!(me.uid == getUser!!.uid)) {
                            user_remove_adapter?.add(getUser!!)
                            Log.d("ADD_USER", "${getUser}")
                        }
                    }
            }

            // アダプタに関連付け
            rem_recy.adapter = user_remove_adapter
            subb.setEnabled(true)

        }.addOnFailureListener {
            finish()
        }

        /*
        -----------------------------------------------------------------------------------------------------------------------------------------------------------
         */

        // 保存ボタン
        subb.setOnClickListener {

            // 仮置き
            AlertDialog.Builder(this) // FragmentではActivityを取得して生成
                .setTitle(R.string.app_name)
                .setMessage("Fill")
                .setPositiveButton("OK") { dialog, which ->
                    // None
                }
                .show()
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