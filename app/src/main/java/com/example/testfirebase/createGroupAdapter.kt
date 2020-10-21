package com.example.testfirebase

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class createGroupAdapter(private val context: Context)
    : RecyclerView.Adapter<createGroupAdapter.ViewHolder>(){

    var itemClickListener: ((User)->Unit)? = null

    fun setOnclickListener(listener:(User)->Unit){
        itemClickListener = listener
    }

    //１行で使用する各部品（ビュー）を保持したもの
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val user_name: TextView = itemView.findViewById(R.id.create_group_list_user_name_textView)
        val user_icon: ImageView = itemView.findViewById(R.id.create_group_list_user_icon_imageView)
        val user_swit: Switch = itemView.findViewById(R.id.create_group_list_user_select_switch)
    }

    class userListItem(val user: User)

    private var itemList = mutableListOf<userListItem>()

    fun add(user: User){
        itemList.add(userListItem(user))
    }

    //セルが必要になるたびに呼び出される
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        //ビューを生成
        val layout = layoutInflater.inflate(R.layout.create_group_user_list, parent, false)
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int = itemList.size

    //保持されているビューにデータなどを設定するここでリスナなどを設定する
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.user_name.text = itemList[position].user.name
        holder.user_swit.setChecked(false)
        if(itemList[position].user.img.isNotEmpty()){
            Picasso.get().load(itemList[position].user.img).into(holder.user_icon)
        }else{
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/firevasetest-1d5b9.appspot.com/o/user_icon%2Fnoimage.png?alt=media&token=b9ae62b8-8c42-4791-9507-c84c93f6871f").into(holder.user_icon)
        }
    }
}