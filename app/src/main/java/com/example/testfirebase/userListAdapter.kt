package com.example.testfirebase

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class userListAdapter(private val context: Context)
    : RecyclerView.Adapter<userListAdapter.ViewHolder>() {

    //１行で使用する各部品（ビュー）を保持したもの
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val user_name:TextView = itemView.findViewById(R.id.user_list_row_user_name_textview)
        val user_pr:TextView = itemView.findViewById(R.id.user_list_row_user_pr_textview)
        val user_image:ImageView = itemView.findViewById(R.id.user_list_row_user_imageView)
    }

    //現状userデータがないのでダミーデータを格納するだけの処理になっている
    class userListItem(val user: User){}

    private var itemList = mutableListOf<userListItem>()

    fun add(user: User){
        itemList.add(userListItem(user))
    }

    //セルが必要になるたびに呼び出される。
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        //ビューを生成
        val layout = layoutInflater.inflate(R.layout.user_list_row, parent, false)
        return userListAdapter.ViewHolder(layout)
    }

    override fun getItemCount(): Int = itemList.size

    //保持されているビューにデータなどを設定するここでリスナなどを設定する
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.user_name.text = itemList[position].user.name
        holder.user_pr.text = itemList[position].user.pr

    }



}