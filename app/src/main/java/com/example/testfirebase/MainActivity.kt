package com.example.testfirebase

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
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
            val intent  = Intent(this, UserLoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}
