package com.example.testfirebase

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class add_remove_userAdapter(private val context: Context)
    :RecyclerView.Adapter<add_remove_userAdapter.ViewHolder>(){

    // 追加
    var itemClickListner : ((String, Boolean)->Unit)? = null
    fun setOnclickListener(listener:(String, Boolean)->Unit){
        itemClickListner = listener
    }

    // 除外

    // ViewHolder
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val user_select: Switch = itemView.findViewById(R.id.user_add_remove_select_switch)
        val user_icon: CircleImageView = itemView.findViewById(R.id.user_add_remove_icon_imageView)
        val user_name: TextView = itemView.findViewById(R.id.user_add_remove_name_textView)
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
        val layout = layoutInflater.inflate(R.layout.user_add_remove_list_row, parent, false)
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.user_select.setChecked(false)
        Picasso.get().load(itemList[position].user.img).into(holder.user_icon)
        holder.user_name.text = itemList[position].user.name

        // ユーザの選択
        holder.itemView.setOnClickListener {
            if (holder.user_select.isChecked == false){
                holder.user_select.setChecked(true)
                itemClickListner?.invoke(itemList[position].user.uid, true)
            }else{
                holder.user_select.setChecked(false)
                itemClickListner?.invoke(itemList[position].user.uid, false)
            }
        }
    }

}