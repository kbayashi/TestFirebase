package com.example.testfirebase

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_list_fragment.view.*

class userListAdapter(private val context: Context)
    : RecyclerView.Adapter<userListAdapter.ViewHolder>() {

    var itemClickListner : ((User)->Unit)? = null

    fun setOnclickListener(listener:(User)->Unit){
        itemClickListner = listener
    }

    var talkTransitionListner:((User)->Unit)? = null

    fun setTalkTransitionListener(listener: (User) -> Unit){
        talkTransitionListner = listener
    }

    //１行で使用する各部品（ビュー）を保持したもの
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val user_name:TextView = itemView.findViewById(R.id.user_list_row_user_name_textview)
        val user_pr:TextView = itemView.findViewById(R.id.user_list_row_user_pr_textview)
        val user_image:ImageView = itemView.findViewById(R.id.user_list_row_user_imageView)
    }

    class userListItem(val user: User){}

    private var itemList = mutableListOf<userListItem>()

    fun add(user: User){
        itemList.add(userListItem(user))
    }

    fun clear(){
        itemList.clear()
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
        Picasso.get().load(itemList[position].user.img).
            into(holder.user_image)

        holder.itemView.setOnClickListener {
            Log.d("ユーザ一覧","タッチ")
            itemClickListner?.invoke(itemList[position].user)
        }

        //長押ししたらダイアログを表示
        holder.itemView.setOnLongClickListener {
            val builder =  AlertDialog.Builder(context)
            val items =
                arrayOf("トーク", "削除")

            builder.setItems(items, DialogInterface.OnClickListener { dialogInterface, i ->
                when(i){
                    //トーク
                    0 ->{
                        talkTransitionListner?.invoke(itemList[position].user)
                    }
                    //削除
                    1->{

                    }

                }
            }).show()
            true
        }
    }



}