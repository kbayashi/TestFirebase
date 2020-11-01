package com.example.testfirebase

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.core.view.forEach
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
        var user_id: String = "none"
    }

    // ユーザオブジェクトデータを格納
    class userListItem(val user: User)

    // 配列
    private var itemList = mutableListOf<userListItem>()

    // 追加
    fun add(user: User){
        itemList.add(userListItem(user))
    }

    // ユーザ選択メソッド
    // 返り値<String>: ユーザID or "none"
    fun selectUsers(r: RecyclerView, i: Int): String {

        // リサイクルビューに格納されているアイテム分ループ
        val vh: ViewHolder = r.findViewHolderForAdapterPosition(i) as ViewHolder
        return vh.user_id
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
        holder.user_id = itemList[position].user.uid

        // ユーザの選択
        holder.itemView.setOnClickListener{
            if (holder.user_select.isChecked == false){
                holder.user_select.setChecked(true)
            }else{
                holder.user_select.setChecked(false)
            }
        }
    }
}