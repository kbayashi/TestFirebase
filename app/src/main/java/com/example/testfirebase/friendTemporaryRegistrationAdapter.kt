package com.example.testfirebase

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class friendTemporaryRegistrationAdapter(private val context: Context)
    : RecyclerView.Adapter<friendTemporaryRegistrationAdapter.ViewHolder>(){


    var itemClickListner : ((User)->Unit)? = null

    fun setOnclickListener(listener:(User)->Unit){
        itemClickListner = listener
    }

    //１行で使用する各部品（ビュー）を保持したもの
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val user_name: TextView = itemView.findViewById(R.id.user_list_row_user_name_textview)
        val user_pr: TextView = itemView.findViewById(R.id.user_list_row_user_pr_textview)
        val user_image: ImageView = itemView.findViewById(R.id.user_list_row_user_imageView)
    }

    class friendTemporaryListItem(val user: User){}

    private var itemList = mutableListOf<friendTemporaryListItem>()

    fun add(user: User){
        itemList.add(friendTemporaryListItem(user))
    }

    //セルが必要になるたびに呼び出される。
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val layoutInflater = LayoutInflater.from(context)
        //ビューを生成
        val layout = layoutInflater.inflate(R.layout.user_list_row, parent, false)
        return friendTemporaryRegistrationAdapter.ViewHolder(layout)

    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.user_name.text = itemList[position].user.name
        holder.user_pr.text = itemList[position].user.pr
        Picasso.get().load(itemList[position].user.img).
        into(holder.user_image)
    }

}