package com.example.testfirebase

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.testfirebase.UserListFragment.Companion.SELECT_USER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

        fechLatestMessage(adapter!!)
        view.latest_chat_list_recyclerView.
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        )

        // チャット画面に移動
        adapter?.setOnclickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(SELECT_USER, it)
            Log.d(SELECT_USER, "$it")
            startActivity(intent)
        }

        // グループ画面に移動
        adapter?.setOnGroupClickListener {
            val intent = Intent(context, GroupChatActivity::class.java)
            intent.putExtra("GroupId", it)
            startActivity(intent)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        adapter = latestChatListAdapter(context)
    }

    //最新のメッセージを格納
    private fun fechLatestMessage(adapter: latestChatListAdapter){
        val uid = FirebaseAuth.getInstance().uid.toString()
        Log.d("UID", uid)
        val ref = FirebaseFirestore.getInstance().collection("user-latest").document("les").
        collection(uid).orderBy("time", Query.Direction.DESCENDING).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            Log.d("GETDATA", "${querySnapshot}")
            adapter.latesItemDel()
            querySnapshot?.forEach {
                Log.d("GETDATA", "${it.toObject(Message::class.java).message}")
                var message = it.toObject(Message::class.java)
                adapter.add(message)
            }
            view?.latest_chat_list_recyclerView?.adapter = adapter
        }
    }
}