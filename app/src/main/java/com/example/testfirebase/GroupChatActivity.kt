package com.example.testfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class GroupChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        // アクションバーの表記を変更
        setTitle("グループチャット")
    }
}