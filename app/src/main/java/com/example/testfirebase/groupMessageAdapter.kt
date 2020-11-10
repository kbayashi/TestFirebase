package com.example.testfirebase

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class groupMessageAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // UID
    val me = FirebaseAuth.getInstance().uid
    val db = FirebaseFirestore.getInstance()

    // View(Me)
    class ViewGroupMeHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val me_img: ImageView = itemView.findViewById(R.id.chat_me_row_pic_imageView)
        val me_msg: Button = itemView.findViewById(R.id.chat_me_row_msg_button)
        val me_time: TextView = itemView.findViewById(R.id.chat_me_row_time_textView)
    }

    // View(Your)
    class ViewGroupYourHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val you_img: ImageView = itemView.findViewById(R.id.chat_you_row_pic_imageView)
        val you_msg: Button = itemView.findViewById(R.id.chat_you_row_msg_button)
        val you_time: TextView = itemView.findViewById(R.id.chat_you_row_time_textView)
    }

    // クラス
    class groupMessageListItem(val message: GroupMessage)

    // リスト
    private val itemList = mutableListOf<groupMessageListItem>()

    // メッセージ追加
    fun add(message: GroupMessage) {
        itemList.add(groupMessageListItem(message))
    }

    // View生成
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(context)

        if (viewType == 0) {
            val layout = layoutInflater.inflate(R.layout.chat_me_row, parent, false)
            return ViewGroupMeHolder(layout)
        } else {
            val layout = layoutInflater.inflate(R.layout.chat_you_row, parent, false)
            return ViewGroupYourHolder(layout)
        }
    }

    // ViewTypeを返す
    override fun getItemViewType(position: Int): Int {

        if (itemList[position].message.send_id == me) {
            return 0
        } else {
            return 1
        }
    }

    // カウント
    override fun getItemCount(): Int = itemList.size

    // 表示
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder.itemViewType) {
            0 -> {
                val holder_me = holder as ViewGroupMeHolder
                holder_me.me_msg.text = itemList[position].message.message
                holder_me.me_time.text = itemList[position].message.TimestampToDate(itemList[position].message.timestamp)
                db.collection("user").document(itemList[position].message.send_id).get().addOnSuccessListener { document ->
                    if (document != null) {
                        Picasso.get().load(document.getString("img")).into(holder_me.me_img)
                    } else {
                        // 自分のアイコンが取得できなかった時は、未設定時のアイコンを表示する
                        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/firevasetest-1d5b9.appspot.com/o/user_icon%2Fnoimage.png?alt=media&token=b9ae62b8-8c42-4791-9507-c84c93f6871f")
                    }
                }
            }

            1 -> {
                val holder_your = holder as ViewGroupYourHolder
                holder_your.you_msg.text = itemList[position].message.message
                holder_your.you_time.text = itemList[position].message.TimestampToDate(itemList[position].message.timestamp)
                db.collection("user").document(itemList[position].message.send_id).get().addOnSuccessListener { document ->
                    if (document != null) {
                        Picasso.get().load(document.getString("img")).into(holder_your.you_img)
                    } else {
                        // 相手のアイコンが取得できなかった時は、未設定時のアイコンを表示する
                        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/firevasetest-1d5b9.appspot.com/o/user_icon%2Fnoimage.png?alt=media&token=b9ae62b8-8c42-4791-9507-c84c93f6871f")
                    }
                }
            }
        }
    }
}