package com.example.testfirebase

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FriendAddAdapter(private val context: Context)
    : RecyclerView.Adapter<FriendAddAdapter.ViewHolder>() {

    //１行で使用する各部品（ビュー）を保持したもの
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val friendName: TextView = itemView.findViewById(R.id.friend_add_row_name_textView)
        val friendPr: TextView = itemView.findViewById(R.id.friend_add_row_pr_textView)
    }

    //現状userデータがないのでダミーデータを格納するだけの処理になっている
    class friendListItem(){}

    private var itemList = mutableListOf<friendListItem>()

    fun add(){
        itemList.add(friendListItem())
    }

    //セルが必要になるたびに呼び出される。
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        //ビューを生成
        val layout = layoutInflater.inflate(R.layout.friend_add_row, parent, false)
        return FriendAddAdapter.ViewHolder(layout)
    }

    override fun getItemCount(): Int = itemList.size

    //保持されているビューにデータなどを設定するここでリスナなどを設定する
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }
}