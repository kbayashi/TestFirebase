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
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.bottom_nav_chat ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_FrameLayout, ChatListFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }


            }
            return@setOnNavigationItemSelectedListener false

        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_FrameLayout, UserListFragment())
            .commit()
    }
}
