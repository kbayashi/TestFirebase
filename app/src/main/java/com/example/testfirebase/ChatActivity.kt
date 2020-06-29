package com.example.testfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ChatActivity : AppCompatActivity() {
    companion object{
        val SELECT_USER = "SELECT_USER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val intent = getIntent()
        supportActionBar?.title = intent.getStringExtra("SELECT_USER")

        //val adapter = GroupAdapter<ViewHolder>()

        //adapter.add(ChatItem())
        //adapter.add(ChatItem())
        //adapter.add(ChatItem())
        //adapter.add(ChatItem())

        //chat_recyclerView.adapter = adapter
    }
}

/*
class ChatItem: Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int){

    }

    override fun getLayout():Int{
        return R.layout.chat_me_row
    }
}
 */
