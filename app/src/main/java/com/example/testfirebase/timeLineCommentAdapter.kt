package com.example.testfirebase

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class timeLineCommentAdapter(private val context: Context)
    : RecyclerView.Adapter<timeLineCommentAdapter.ViewHolder>(){

    //１行で使用する各部品（ビュー）を保持したもの
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val comment:TextView = itemView.findViewById(R.id.time_line_comment_row_textView)
        val image:ImageView = itemView.findViewById(R.id.time_line_comment_row_circleImageView)
        val time:TextView = itemView.findViewById(R.id.time_line_comment_row_time_textView)
        val name:TextView = itemView.findViewById(R.id.time_line_comment_row_name_textView)
    }

    class commentListItem(val comment: Comment){}

    private var itemList = mutableListOf<commentListItem>()

    fun add(comment: Comment){
        itemList.add(commentListItem(comment))
    }

    fun clear(){
        itemList.clear()
    }

    //セルが必要になるたびに呼び出される。
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        //ビューを生成
        val layout = layoutInflater.inflate(R.layout.time_line_comment_row, parent, false)
        return timeLineCommentAdapter.ViewHolder(layout)
    }

    override fun getItemCount(): Int = itemList.size

    //保持されているビューにデータなどを設定するここでリスナなどを設定する
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        FirebaseFirestore.getInstance().collection("user").document(itemList[position].comment.uid).
        get().addOnSuccessListener {

            val user = it.toObject(User::class.java)
            Picasso.get().load(user?.img).
            into(holder.image)
            holder.name.text = user?.name
        }

        holder.time.text = itemList[position].comment.getTime(itemList[position].comment.time)
        holder.comment.text = itemList[position].comment.comment

    }
}