package com.example.testfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserPlofileEditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_plofile_edit)
        val intent = getIntent();
        val get_user = intent.getParcelableExtra<User>(UserListFragment.SELECT_USER)
        user_profile_user_name_textview.text = get_user.name
        user_profile_user_pr_textview.text = get_user.pr
        user_profile_user_gender_textview.text = get_user.gender
        user_profile_user_old_textview.text = get_user.birthday
        user_profile_user_prefectures_textview.text = get_user.live
        user_profile_user_hobby_textview.text = "非公開"
        user_profile_user_sick_textview.text = get_user.sick
        user_profile_lifeexpectancy_title_textview.text = get_user.life_expectancy
        Picasso.get().load(get_user.img).into(user_profile_user_imageview)

        //UserProfilePlanTextEditDialog表示処理
        user_profile_user_name_textview.setOnClickListener{
            val dialog = UserProfilePlainTextEditDialog("名前",user_profile_user_name_textview,"name")
            dialog.show(supportFragmentManager, "名前")
        }
        user_profile_user_prefectures_textview.setOnClickListener{
            val dialog = UserProfilePlainTextEditDialog("自己紹介",user_profile_user_prefectures_textview,"pr")
            dialog.show(supportFragmentManager,"自己紹介")
        }
    }
}