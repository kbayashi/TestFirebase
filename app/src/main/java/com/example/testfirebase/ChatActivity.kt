package com.example.testfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testfirebase.UserListFragment.Companion.SELECT_USER

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val intent = getIntent()
        val get_username = intent.getParcelableExtra<User>(SELECT_USER)
        supportActionBar?.title = get_username.name

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
