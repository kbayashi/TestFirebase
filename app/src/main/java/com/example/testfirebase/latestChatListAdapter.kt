package com.example.testfirebase

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class latestChatListAdapter(private val context: Context)
    : RecyclerView.Adapter<latestChatListAdapter.ViewHolder>(){

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val latestChatTitle:TextView = itemView.findViewById(R.id.latest_chat_row_title_textView)
        val latestChatLatst:TextView = itemView.findViewById(R.id.latest_chat_row_chat_textView)
        val latestChatTime:TextView = itemView.findViewById(R.id.latest_chat_row_time_textView)
        val latestChatTImage:ImageView = itemView.findViewById(R.id.latest_chat_row_imageView)
    }

    //現状データがないのでダミーデータを格納するだけの処理になっている
    class latestChatListItem(){}

    private var itemList = mutableListOf<latestChatListItem>()

    fun add(){
        itemList.add(latestChatListItem())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): latestChatListAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        //ビューを生成
        val layout = layoutInflater.inflate(R.layout.latest_chat_row, parent, false)
        return latestChatListAdapter.ViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: latestChatListAdapter.ViewHolder, position: Int) {
        holder.latestChatTime.text = "9:00"
    }


}