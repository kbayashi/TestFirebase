package com.example.testfirebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        verifyfyUserIsLogin()

        //下のナビゲーションボタンが押されたら画面を切り替える
        main_bottmnavview.setOnNavigationItemSelectedListener {
            when(it.itemId){

                R.id.bottom_nav_user ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_FrameLayout, UserListFragment())
                        .commit()
                    title = "ユーザ一覧"
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.bottom_nav_chat ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_FrameLayout, LatestChatListFragment())
                        .commit()
                    title = "トーク一覧"
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.bottom_nav_time_line ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_FrameLayout, timeLineFragment(), "TimeLine")
                        .commit()
                    title = "タイムライン"
                    return@setOnNavigationItemSelectedListener true
                }


            }
            return@setOnNavigationItemSelectedListener false

        }

        //初期化
        startFlagment()


    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.user_list_fragment_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.user_list_fragment_menu_friend_add -> {
                val intent = Intent(this, FriendAddActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //初期化
    private fun startFlagment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_FrameLayout, UserListFragment())
            .commit()
        title = "ユーザ一覧"
    }

    //ログイン確認
    private fun verifyfyUserIsLogin(){
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null){
            val intent  = Intent(this, TitleActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}
