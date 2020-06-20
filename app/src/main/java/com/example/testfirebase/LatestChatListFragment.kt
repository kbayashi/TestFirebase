package com.example.testfirebase

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.chat_list_fragment.view.*

class LatestChatListFragment: Fragment() {

    var adapter:latestChatListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.chat_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dummydata(adapter!!)
        view.latest_chat_list_recyclerView.adapter = adapter
        view.latest_chat_list_recyclerView.
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        )

        //チャット画面に移動
        adapter?.setOnclickListener {
            val intent = Intent(context, ChatActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        adapter = latestChatListAdapter(context)
    }


    //ダミーデータ格納
    fun dummydata(adapter: latestChatListAdapter){
        adapter.add()
        adapter.add()
        adapter.add()
        adapter.add()
    }
}