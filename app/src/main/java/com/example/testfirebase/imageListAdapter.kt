package com.example.testfirebase

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class imageListAdapter(private val context: Context)
    : RecyclerView.Adapter<imageListAdapter.ViewHolder>() {

    //１行で使用する各部品（ビュー）を保持したもの
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val ImageView: ImageView = itemView.findViewById(R.id.image_list_row_imageView)
    }

    //現状userデータがないのでダミーデータを格納するだけの処理になっている
    class imageListItem(){}

    private var itemList = mutableListOf<imageListItem>()

    fun add(){
        itemList.add(imageListItem())
    }


    //セルが必要になるたびに呼び出される。
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        //ビューを生成
        val layout = layoutInflater.inflate(R.layout.image_list_row, parent, false)
        return imageListAdapter.ViewHolder(layout)
    }

    override fun getItemCount(): Int = itemList.size

    //保持されているビューにデータなどを設定するここでリスナなどを設定する
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


    }



}