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

    var userListAdapter:userListAdapter? = null
    var groupListAdapter:groupListAdapter? = null
    var friendDisplayFlg = false
    var groupDisplayFlg = false

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

        dummydata(userListAdapter!!, groupListAdapter!!)


        setUp(view)


        //友達リストを表示・非表示
        view.user_list_friend_constraintLayout.setOnClickListener {
            friendDisplaySwitching(view)
        }
        //グループリストを表示・非表示
        view.user_list_group_constraintLayout.setOnClickListener {
            groupDisplaySwitching(view)
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userListAdapter = userListAdapter(context)
        groupListAdapter = groupListAdapter(context)
    }

    //ダミーデータ格納
    fun dummydata(adapter: userListAdapter, groupListAdapter: groupListAdapter){
        adapter.add()
        adapter.add()
        adapter.add()
        adapter.add()

        groupListAdapter.add()
        groupListAdapter.add()
        groupListAdapter.add()
        groupListAdapter.add()
        groupListAdapter.add()

    }

    //ビューの初期化
    private fun setUp(view: View){

        view.user_list_user_recyclerView.adapter = userListAdapter
        view.user_list_user_recyclerView.visibility = View.GONE

        view.user_list_group_list_recyclerView.adapter = groupListAdapter
        view.user_list_group_list_recyclerView.visibility = View.GONE

        //recyclerviewに下線を足す
        view.user_list_user_recyclerView.addItemDecoration(DividerItemDecoration(activity,
            DividerItemDecoration.VERTICAL))

    }

    //友達表示・非表示
    private fun friendDisplaySwitching(view: View){

        if(friendDisplayFlg == true){
            friendDisplayFlg = false
            view.user_list_user_recyclerView.visibility = View.GONE
            view.user_list_friend_title_arrow_imageView.setImageResource(R.drawable.ic_expand_less_24dp)
        }else{
            friendDisplayFlg = true
            view.user_list_user_recyclerView.visibility = View.VISIBLE
            view.user_list_friend_title_arrow_imageView.setImageResource(R.drawable.ic_expand_more_24dp)
        }
    }

    //グループ表示・非表示
    private fun groupDisplaySwitching(view: View){

        if(groupDisplayFlg == true){
            groupDisplayFlg = false
            view.user_list_group_list_recyclerView.visibility = View.GONE
            view.user_list_group_title_arrow_imageView.setImageResource(R.drawable.ic_expand_less_24dp)
        }else{
            groupDisplayFlg = true
            view.user_list_group_list_recyclerView.visibility = View.VISIBLE
            view.user_list_group_title_arrow_imageView.setImageResource(R.drawable.ic_expand_more_24dp)
        }
    }

}