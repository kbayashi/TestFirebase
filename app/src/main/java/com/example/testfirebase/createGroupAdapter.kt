package com.example.testfirebase

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class createGroupAdapter(private val context: Context)
    :RecyclerView.Adapter<createGroupAdapter.ViewHolder>(){

    // ビューホルダ
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val user_select: Switch = itemView.findViewById(R.id.create_group_list_user_select_switch)
        val user_icon: CircleImageView = itemView.findViewById(R.id.create_group_list_user_icon_imageView)
        val user_name: TextView = itemView.findViewById(R.id.create_group_list_user_name_textView)
    }

    // ユーザオブジェクトデータを格納
    class userListItem(val user: User)

    // 配列
    private var itemList = mutableListOf<userListItem>()

    // 追加
    fun add(user: User){
        itemList.add(userListItem(user))
    }

    // ビューを生成
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val layout = layoutInflater.inflate(R.layout.create_group_user_list_row, parent, false)
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        holder.user_select.setChecked(false)
        Picasso.get().load(itemList[position].user.img).into(holder.user_icon)
        holder.user_name.text = itemList[position].user.name

        holder.itemView.setOnClickListener{
            if (holder.user_select.isChecked == false){
                holder.user_select.setChecked(true)
            }else{
                holder.user_select.setChecked(false)
            }
        }
    }
}