package com.example.testfirebase

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class groupListAdapter(private val context: Context)
    : RecyclerView.Adapter<groupListAdapter.ViewHolder>() {

    //１行で使用する各部品（ビュー）を保持したもの
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val group_name: TextView = itemView.findViewById(R.id.group_list_row_textView)
        val group_image: ImageView = itemView.findViewById(R.id.group_list_row_imageView)
    }

    //現状userデータがないのでダミーデータを格納するだけの処理になっている
    class groupListItem(){}

    private var itemList = mutableListOf<groupListItem>()

    fun add(){
        itemList.add(groupListItem())
    }

    //セルが必要になるたびに呼び出される。
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        //ビューを生成
        val layout = layoutInflater.inflate(R.layout.group_list_row, parent, false)
        return groupListAdapter.ViewHolder(layout)
    }

    override fun getItemCount(): Int = itemList.size

    //保持されているビューにデータなどを設定するここでリスナなどを設定する
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }



}