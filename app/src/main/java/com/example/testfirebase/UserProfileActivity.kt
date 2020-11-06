package com.example.testfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.testfirebase.UserListFragment.Companion.SELECT_USER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_profile.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_my_profile.*

class UserProfileActivity : AppCompatActivity() {

    val uid = FirebaseAuth.getInstance().uid.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        val intent = getIntent()
        val get_user = intent.getParcelableExtra<User>(SELECT_USER)
        user_profile_name_textView.text = "名前"
        user_profile_user_name_textView.text = get_user.name
        user_profile_pr_textView.text = "自己紹介"
        user_profile_user_pr_textView.text = get_user.pr
        user_profile_gender_textView.text = "性別"
        user_profile_user_gender_textView.text = get_user.gender
        user_profile_old_textView.text = "生年月日"
        user_profile_user_old_textView.text = get_user.birthday
        user_profile_prefectures_textView.text = "都道府県"
        user_profile_user_prefectures_textView.text = get_user.live
        user_profile_hobby_textView.text = "趣味"
        user_profile_user_hobby_textView.text = "非公開"
        user_profile_sick_textView.text = "病名"
        user_profile_user_sick_textView.text = get_user.sick
        user_profile_lifeexpectancy_textView.text = "余命"
        user_profile_user_lifeexpectancy_textView.text = get_user.life_expectancy
        Picasso.get().load(get_user.img).into(user_profile_user_imageview)
        user_profile_talk_floatingActionButton.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(SELECT_USER, get_user)
            startActivity(intent)
        }

        //すでに追加・申請しているなら友だち追加ボタンを消す
        FirebaseFirestore.getInstance().collection("user-friend")
            .document("get").collection(uid).document(get_user.uid).get().addOnSuccessListener {
                if(it["uid"] == get_user.uid){
                    Log.d("友達のID", "${it["uid"]}")
                    user_profile_friend_add_floatingActionButton.visibility = View.GONE
                }
            }

        Log.d("get_user", "${get_user.uid}")
        FirebaseFirestore.getInstance().collection("friend-temporary-registration").
            document("get").collection(uid).get().addOnSuccessListener {
                it.forEach {
                    if(it["uid"] == get_user.uid){
                      Log.d("自分のID", "${it["uid"]}")
                        user_profile_friend_add_floatingActionButton.visibility = View.GONE
                    }
                }
        }

        //友達追加・仮登録
        user_profile_friend_add_floatingActionButton.setOnClickListener {

            val ref = FirebaseFirestore.getInstance()
            val uid = FirebaseAuth.getInstance().uid.toString()

            val firendData = hashMapOf(
                "uid" to get_user.uid
            )
            val temporaryRegistrationData = hashMapOf(
                "uid" to uid
            )

            ref.collection("user-friend").
                document("get").collection(uid).document(get_user.uid)
                .set(firendData).addOnSuccessListener {
                    ref.collection("friend-temporary-registration").document("get").
                    collection(get_user.uid).document(uid).set(temporaryRegistrationData)
                }
        }

    }
}