package com.example.testfirebase

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_create_group.*
import java.util.*

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
        val tcon = findViewById<TextView>(R.id.group_topic_textView)
        val recy = findViewById<RecyclerView>(R.id.user_list_recyclerView)
        val subb = findViewById<Button>(R.id.submit_button)

        // Firebase
        val auth = FirebaseAuth.getInstance()
        val me = auth.currentUser
        val db = FirebaseFirestore.getInstance()

        // グループIDを取得
        val gid = intent.getStringExtra("GroupId")

        // アダプタ(グループ作成アダプタと同じ)
        var user_adapter = createGroupAdapter(this)

        // グループ名を取得
        db.collection("group").document(gid)
            .get().addOnSuccessListener { document ->
                if (document != null) {
                    setTitle(document["name"].toString())
                    edit.setText(document["name"].toString())
                    topi.setText(document["topic"].toString())
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }

        // ユーザオブジェクト
        var loginUser: User?
        var getUser: User?

        // DBから取得してきたデータをアダプタに格納
        val loginUserRef = db.collection("user").document(me!!.uid)
        loginUserRef.get().addOnSuccessListener {
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
                        user_adapter?.add(getUser!!)
                        Log.d("ADD_USER", "${getUser}")
                    }
                }
                // アダプタに関連付け
                recy.adapter = user_adapter
                // ボタンを有効化
                subb.setEnabled(true)

            }.addOnFailureListener {
                Log.d("GET_FAILED", it.message)
            }

        }.addOnFailureListener {
            Log.d("GET_FAILED", it.message)
        }

        // グループアイコン変更の処理
        icon.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
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