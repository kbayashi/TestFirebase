package com.example.testfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_group.*

class CreateGroupActivity : AppCompatActivity() {

    // データベースオブジェクト
    val db = FirebaseFirestore.getInstance()
    // 自分のユーザID
    val uid = FirebaseAuth.getInstance().uid
    // 自分のユーザオブジェクト
    var loginUser:User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        // アクションバー表記変更
        supportActionBar?.title = "グループを作成"
        // アダプターの設定
        var user_ListAdapter = createGroupAdapter(this)

        // ユーザを取り出す
        val loginUserRef = db.collection("user").document(uid!!)
        loginUserRef.get().addOnSuccessListener {
            loginUser = it.toObject(User::class.java)
            Log.d("ユーザ取得", "${it.data}")
            Log.d("ユーザ取得", "ログイン:${loginUser?.name}")

            // アタッチメント
            user_list_recyclerView.adapter = user_ListAdapter

            // その他のユーザを取得
            val users = db.collection("user")
            users.get().addOnSuccessListener {
                it.forEach {
                    var getUser = it.toObject(User::class.java)
                    Log.d("ユーザ取得","${it.toObject(User::class.java)}")
                    Log.d("ユーザ取得","${getUser.name}")

                    // 取り出したユーザが自分以外か？
                    if(!(loginUser?.name == getUser.name)) {
                        user_ListAdapter?.add(getUser)
                    }
                }
            }.addOnFailureListener {
                Log.d("失敗", it.message)
            }

        }

        // ボタン押下時のアクション
        submit_button.setOnClickListener {
            // 何も名前を入力していない時
            if(group_name_editText.text.isEmpty()){
                Toast.makeText(applicationContext, "何も入力されていません", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(applicationContext, "近日実装", Toast.LENGTH_SHORT).show()
            }
        }
    }
}