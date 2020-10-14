package com.example.testfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_profile_edit.*

class UserProfileEditActivity : AppCompatActivity() {

    private var my_user:User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_edit)

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
            user_profile_edit_user_name_textView.text = my_user?.name
            user_profile_edit_user_pr_textView.text = my_user?.pr
            user_profile_edit_user_gender_textView.text = my_user?.gender
            user_profile_edit_user_old_textView.text = my_user?.birthday
            user_profile_edit_user_prefectures_textView.text = my_user?.live
            user_profile_edit_user_hobby_textView.text = my_user?.hoby
            user_profile_edit_user_sick_textView.text = my_user?.sick
            user_profile_edit_user_lifeexpectancy_textView.text = my_user?.life_expectancy
            Picasso.get().load(my_user?.img).into(user_profile_edit_imageview)
        }

        /*
        name_relativelayout.setOnClickListener{
            val intent = Intent(this,UserProfileEditPlainTextActivity::class.java)
            intent.putExtra("table", "name")
                .putExtra("edit",get_user.name)
            startActivity(intent)
        }
        pr_relativelayout.setOnClickListener{
            val intent = Intent(this,UserProfileEditPlainTextActivity::class.java)
            intent.putExtra("table","pr")
                .putExtra("edit",get_user.pr)
            startActivity(intent)
        }
        gender_relativelayout.setOnClickListener{
            val intent = Intent(this,UserProfileEditRadioButtonActivity::class.java)
            intent.putExtra("table","gender")
                .putExtra("edit",get_user.gender)
            startActivity(intent)
        }
        old_relativelayout.setOnClickListener{
            val intent = Intent(this,UserProfileEditSpinnerActivity::class.java)
            intent.putExtra("table","birthday")
                .putExtra("edit",get_user.birthday)
            startActivity(intent)
        }
        prefecture_relativelayout.setOnClickListener{
            val intent = Intent(this,UserProfileEditSpinnerActivity::class.java)
            intent.putExtra("table","live")
                .putExtra("edit",get_user.live)
            startActivity(intent)
        }
        sick_relativelayout.setOnClickListener {
            val intent = Intent(this,UserProfileEditSpinnerActivity::class.java)
            intent.putExtra("table","sick")
                .putExtra("edit",get_user.sick)
            startActivity(intent)
        }
        lifeexpectancy_relativelayout.setOnClickListener {
            val intent = Intent(this,UserProfileEditSpinnerActivity::class.java)
            intent.putExtra("table","lifeExpectancy")
                .putExtra("edit",get_user.life_expectancy)
            startActivity(intent)
        }

        //アイコン
        user_profile_edit_imageview.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

         */

         */
    }

}