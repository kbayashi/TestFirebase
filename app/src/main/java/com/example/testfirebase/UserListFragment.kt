package com.example.testfirebase

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.user_list_fragment.view.*

class UserListFragment: Fragment() {

    var adapter:userListAdapter? = null

    //フラグメントにレイアウトを設定
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_list_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dummydata(adapter!!)

        view.user_list_user_recyclerView.adapter = adapter
        //recyclerviewに下線を足す
        view.user_list_user_recyclerView.addItemDecoration(DividerItemDecoration(activity,
            DividerItemDecoration.VERTICAL))

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        adapter = userListAdapter(context)
    }

    //ダミーデータ格納
    fun dummydata(adapter: userListAdapter){
        adapter?.add()
        adapter?.add()
        adapter?.add()
        adapter?.add()
    }
}