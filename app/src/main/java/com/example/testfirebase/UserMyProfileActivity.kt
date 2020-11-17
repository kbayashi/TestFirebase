package com.example.testfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NavUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_my_profile.*
import com.squareup.picasso.Picasso

class UserMyProfileActivity : AppCompatActivity() {

    private var my_user:User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_my_profile)

        // 自分のユーザ情報を取得
        val auth = FirebaseAuth.getInstance()
        val me = auth.currentUser
        // データベースオブジェクト
        val db = FirebaseFirestore.getInstance()

        // 取得
        val docRef = db.collection("user").document(me!!.uid)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            my_user = documentSnapshot.toObject(User::class.java)
            // タイトル
            setTitle(my_user?.name)
            // プロパティ
            user_my_profile_user_name_textView.text = my_user?.name
            user_my_profile_user_pr_textView.text = my_user?.pr
            user_my_profile_user_gender_textView.text = my_user?.gender
            user_my_profile_user_old_textView.text = my_user?.birthday
            user_my_profile_user_prefectures_textView.text = my_user?.live
            user_my_profile_user_hobby_textView.text = my_user?.hobby
            user_my_profile_user_sick_textView.text = my_user?.sick
            user_my_profile_user_lifeexpectancy_textView.text = my_user?.life_expectancy
            Picasso.get().load(my_user?.img).into(user_my_profile_user_imageview)
        }

        // ボタン押下時
        user_my_profile_user_edit_floatingActionButton.setOnClickListener {
            val intent = Intent(this, UserProfileEditActivity::class.java)
            startActivity(intent)
        }
    }

    //戻るボタン押下時はこの画面を削除する
    override fun onBackPressed() {
        //自アプリに戻る
        val upIntent = NavUtils.getParentActivityIntent(this)
        if (upIntent != null) {
            NavUtils.navigateUpTo(this, upIntent)
        }
    }
}