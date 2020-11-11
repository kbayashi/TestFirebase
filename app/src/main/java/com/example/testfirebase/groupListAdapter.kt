package com.example.testfirebase

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class groupListAdapter(private val context: Context)
    : RecyclerView.Adapter<groupListAdapter.ViewHolder>() {

    var itemClickListener: ((String)->Unit)? = null

    fun setOnclickListener(listener: (String)->Unit) {
        itemClickListener = listener
    }

    //１行で使用する各部品（ビュー）を保持したもの
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val group_name: TextView = itemView.findViewById(R.id.group_list_row_textView)
        val group_image: ImageView = itemView.findViewById(R.id.group_list_row_imageView)
        val group_topic: TextView = itemView.findViewById(R.id.group_list_row_topic_textView)
    }

    //現状userデータがないのでダミーデータを格納するだけの処理になっている
    class groupListItem(val group: Group)

    private var itemList = mutableListOf<groupListItem>()

    fun add(group: Group){
        itemList.add(groupListItem(group))
    }

    //セルが必要になるたびに呼び出される。
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //ビューを生成
        val layoutInflater = LayoutInflater.from(context)
        val layout = layoutInflater.inflate(R.layout.group_list_row, parent, false)
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int = itemList.size

    //保持されているビューにデータなどを設定するここでリスナなどを設定する
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.group_name.text = itemList[position].group.name
        holder.group_topic.text = itemList[position].group.topic
        Picasso.get().load(itemList[position].group.icon).into(holder.group_image)

        holder.itemView.setOnClickListener {
            itemClickListener?.invoke(itemList[position].group.gid)
        }
    }
}