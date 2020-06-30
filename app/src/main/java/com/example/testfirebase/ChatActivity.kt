package com.example.testfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testfirebase.UserListFragment.Companion.SELECT_USER
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val intent = getIntent()
        val get_username = intent.getParcelableExtra<User>(SELECT_USER)
        supportActionBar?.title = get_username.name

        send_button.setOnClickListener {

        }

    }
}
