package com.example.testfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import com.example.testfirebase.UserListFragment.Companion.SELECT_USER
import kotlinx.android.synthetic.main.activity_user_profile_edit.*

class UserProfileEditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_edit)
        val intent = getIntent()
        val get_user = intent.getParcelableExtra<User>(SELECT_USER)

        user_profile_edit_user_name_textView.text = get_user.name
        user_profile_edit_name_textView.text = "名前"
        user_profile_edit_user_pr_textView.text = get_user.pr
        user_profile_edit_pr_textView.text = "自己紹介"
        user_profile_edit_user_gender_textView.text = get_user.gender
        user_profile_edit_gender_textView.text = "性別"
        user_profile_edit_user_old_textView.text = get_user.birthday
        user_profile_edit_old_textView.text = "生年月日"
        user_profile_edit_user_prefectures_textView.text = get_user.live
        user_profile_edit_prefectures_textView.text = "都道府県"
        user_profile_edit_user_hobby_textView.text = "非公開"
        user_profile_edit_hobby_textView.text = "趣味"
        user_profile_edit_user_sick_textView.text = get_user.sick
        user_profile_edit_sick_textView.text = "病名"
        user_profile_edit_user_lifeexpectancy_textView.text = get_user.life_expectancy
        user_profile_edit_lifeexpectancy_textView.text = "余命"
        Picasso.get().load(get_user.img).into(user_profile_edit_imageview)

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
            intent.putExtra("table","old")
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
            intent.putExtra("table","lifeexpectancy")
                .putExtra("edit",get_user.life_expectancy)
            startActivity(intent)
        }
    }
}