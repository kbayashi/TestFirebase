package com.example.testfirebase

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
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

        dummy(timeLineListAdapter!!)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        timeLineListAdapter = timeLineListAdapter(context)
    }

    fun dummy(timeLineListAdapter: timeLineListAdapter){
        timeLineListAdapter.add()
        timeLineListAdapter.add()
        timeLineListAdapter.add()
    }
}
