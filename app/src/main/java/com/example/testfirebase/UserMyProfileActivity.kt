package com.example.testfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testfirebase.UserListFragment.Companion.SELECT_USER
import kotlinx.android.synthetic.main.activity_user_my_profile.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.activity_user_profile_edit.*

class UserMyProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_my_profile)
        val intent = getIntent()
        val get_user = intent.getParcelableExtra<User>(SELECT_USER)

        user_my_profile_name_textView.text = "名前"
        user_my_profile_user_name_textView.text = get_user.name
        user_my_profile_pr_textView.text = "自己紹介"
        user_my_profile_user_pr_textView.text = get_user.pr
        user_my_profile_gender_textView.text = "性別"
        user_my_profile_user_gender_textView.text = get_user.gender
        user_my_profile_old_textView.text = "生年月日"
        user_my_profile_user_old_textView.text = get_user.birthday
        user_my_profile_prefectures_textView.text = "都道府県"
        user_my_profile_user_prefectures_textView.text = get_user.live
        user_my_profile_hobby_textView.text = "趣味"
        user_my_profile_user_hobby_textView.text = "非公開"
        user_my_profile_sick_textView.text = "病名"
        user_my_profile_user_sick_textView.text = get_user.sick
        user_my_profile_lifeexpectancy_textView.text = "余命"
        user_my_profile_user_lifeexpectancy_textView.text = get_user.life_expectancy
        Picasso.get().load(get_user.img).into(user_my_profile_user_imageview)
        user_my_profile_user_edit_floatingActionButton.setOnClickListener {
            val intent = Intent(this, UserProfileEditActivity::class.java)
            intent.putExtra(SELECT_USER, get_user)
            startActivity(intent)
        }
    }
}