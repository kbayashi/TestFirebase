package com.example.testfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testfirebase.UserListFragment.Companion.SELECT_USER
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserMyProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_my_profile)
        val intent = getIntent();
        val get_user = intent.getParcelableExtra<User>(SELECT_USER)
        user_profile_user_name_textview.text = get_user.name
        user_profile_user_pr_textview.text = get_user.pr
        user_profile_user_gender_textview.text = get_user.gender
        user_profile_user_old_textview.text = get_user.birthday
        user_profile_user_prefectures_textview.text = get_user.live
        user_profile_user_hobby_textview.text = "非公開"
        user_profile_user_sick_textview.text = get_user.sick
        user_profile_user_lifeexpectancy_textview.text = get_user.life_expectancy
        user_profile_talk_floatingActionButton.setOnClickListener {
            val intent = Intent(this, UserProfileEditActivity::class.java)
            intent.putExtra(SELECT_USER, get_user)
            startActivity(intent)
        }
    }
}