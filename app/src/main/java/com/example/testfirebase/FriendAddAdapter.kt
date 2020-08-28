package com.example.testfirebase

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import androidx.recyclerview.widget.RecyclerView

class FriendAddAdapter(private val context: Context)
    : RecyclerView.Adapter<FriendAddAdapter.ViewHolder>() {

    //１行で使用する各部品（ビュー）を保持したもの
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val userName: TextView = itemView.findViewById(R.id.friend_add_row_name_textView)
        val userPr: TextView = itemView.findViewById(R.id.friend_add_row_pr_textView)
        val userImage: ImageView = itemView.findViewById(R.id.friend_add_row_imageView)
    }

    var itemClickListner : ((User)->Unit)? = null

    fun setOnclickListener(listener:(User)->Unit){
        itemClickListner = listener
    }

    //現状userデータがないのでダミーデータを格納するだけの処理になっている
    class friendListItem(val user: User){}

    private var itemList = mutableListOf<friendListItem>()

    fun add(user: User){
        itemList.add(friendListItem(user))
    }

    fun clear(){
        itemList.clear()
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
        holder.userName.text = itemList[position].user.name
        if(itemList[position].user.pr != "非公開")
        holder.userPr.text = itemList[position].user.pr
        Picasso.get().load(itemList[position].user.img).
            into(holder.userImage)

        holder.itemView.setOnClickListener {
            itemClickListner?.invoke(itemList[position].user)
        }
    }
}