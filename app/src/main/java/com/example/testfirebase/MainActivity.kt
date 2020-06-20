package com.example.testfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

    //初期化
    private fun startFlagment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_FrameLayout, UserListFragment())
            .commit()
        title = "ユーザ一覧"
    }
}
