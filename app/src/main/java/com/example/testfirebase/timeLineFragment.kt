package com.example.testfirebase

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.time_line_fragment.view.*

class timeLineFragment:Fragment(){

    var timeLineListAdapter:timeLineListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.time_line_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.time_line_recyclerview.adapter = timeLineListAdapter
        view.time_line_recyclerview.addItemDecoration(
            DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        )

        //チャットボタンを押すと画面遷移
        timeLineListAdapter?.setOnclickListener {
            val intent = Intent(context, TimeLineCommentActivity::class.java)
            intent.putExtra("TimeLine", it)
            startActivity(intent)
        }

        //追加画面
        view.time_line_floatingActionButton.setOnClickListener {
            val intent:Intent = Intent(context, TimeLineAddActivity::class.java)
            startActivity(intent)
        }

        //タイムライン取得
        FirebaseFirestore.getInstance().collection("time-line").orderBy("time", Query.Direction.DESCENDING).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            timeLineListAdapter?.clear()
            querySnapshot?.forEach {
                timeLineListAdapter?.add(it.toObject(TimeLine::class.java))
            }
            view.time_line_recyclerview.adapter = timeLineListAdapter
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        timeLineListAdapter = timeLineListAdapter(context)
    }

}
