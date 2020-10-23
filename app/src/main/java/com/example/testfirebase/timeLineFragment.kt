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
        timeLineListAdapter?.setOnCommentClickListener {
            val intent = Intent(context, TimeLineCommentActivity::class.java)
            intent.putExtra("TimeLin", it)
            startActivity(intent)
        }

        //タイムライン編集
        timeLineListAdapter?.setOnTimeLineEditListner {
            val intent = Intent(context, TimeLineAddActivity::class.java)
            intent.putExtra("TIME_LINE_EDIT", it)
            startActivity(intent)
        }

        //追加画面
        view.time_line_floatingActionButton.setOnClickListener {
            val intent:Intent = Intent(context, TimeLineAddActivity::class.java)
            startActivity(intent)
        }



    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        timeLineListAdapter = timeLineListAdapter(context)
    }


    override fun onStart() {
        super.onStart()
        getTimeLine()
    }

    //タイムライン取得
    fun getTimeLine(){
        FirebaseFirestore.getInstance().collection("time-line").orderBy("time", Query.Direction.DESCENDING).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            timeLineListAdapter?.clear()
            querySnapshot?.forEach {
                timeLineListAdapter?.add(it.toObject(TimeLine::class.java))
            }
            view?.time_line_recyclerview?.adapter = timeLineListAdapter
        }
            /*.addOnSuccessListener {
            timeLineListAdapter?.clear()
            it.forEach {
                timeLineListAdapter?.add(it.toObject(TimeLine::class.java))
            }
            view?.time_line_recyclerview?.adapter = timeLineListAdapter
        }*/
    }

}
