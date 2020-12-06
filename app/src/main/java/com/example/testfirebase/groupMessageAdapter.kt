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

class groupMessageAdapter(private val context: Context)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

    // View(Me_img)
    class ViewGroupMeImgHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val me_img: ImageView = itemView.findViewById(R.id.chat_me_row_pic_imageView)
        val me_msg: Button = itemView.findViewById(R.id.chat_me_row_msg_button)
        val me_time: TextView = itemView.findViewById(R.id.chat_me_row_time_textView)
        val me_imgSrc: ImageView = itemView.findViewById(R.id.chat_me_row_imgSrc)
    }

    // View(Your_img)
    class ViewGroupYourImgHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val you_img: ImageView = itemView.findViewById(R.id.chat_you_row_pic_imageView)
        val you_msg: Button = itemView.findViewById(R.id.chat_you_row_msg_button)
        val you_time: TextView = itemView.findViewById(R.id.chat_you_row_time_textView)
        val you_imgSrc: ImageView = itemView.findViewById(R.id.chat_you_row_imgSrc)
    }

    // View(Log)
    class ViewGroupLogHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val log_msg: Button = itemView.findViewById(R.id.log_button)
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

        when (viewType) {
            0 -> {
                // 自分(メッセージ)
                val layout = layoutInflater.inflate(R.layout.chat_me_row, parent, false)
                return ViewGroupMeHolder(layout)
            }
            1 -> {
                // 相手(メッセージ)
                val layout = layoutInflater.inflate(R.layout.chat_you_row, parent, false)
                return ViewGroupYourHolder(layout)
            }
            2 -> {
                // 自分(画像)
                val layout = layoutInflater.inflate(R.layout.chat_me_row, parent, false)
                return ViewGroupMeImgHolder(layout)
            }
            3 -> {
                // 相手(画像)
                val layout = layoutInflater.inflate(R.layout.chat_you_row, parent, false)
                return ViewGroupYourImgHolder(layout)
            }
            else -> {
                // ログ
                val layout = layoutInflater.inflate(R.layout.chat_log_row, parent, false)
                return ViewGroupLogHolder(layout)
            }
        }
    }

    // ViewTypeを返す(0: 自分(メッセージ)/ 1: 相手(メッセージ)/ 2: 自分(画像)/ 3: 相手(画像)/ 4: ログ)
    override fun getItemViewType(position: Int): Int {

        // logフラグがtrueなら、ログビュー
        if (itemList[position].message.log_flag == true) {
            return 4
        } else {
            // logではない場合は、チャットビュー
            if (itemList[position].message.send_id == me) {
                if (itemList[position].message.img_flag == true) {
                    return 2
                } else {
                    return 0
                }
            } else {
                if (itemList[position].message.img_flag == true) {
                    return 3
                } else {
                    return 1
                }
            }
        }
    }

    // カウント
    override fun getItemCount(): Int = itemList.size

    // 表示
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder.itemViewType) {
            0 -> {
                // 自分(メッセージ)
                val holder = holder as ViewGroupMeHolder
                holder.me_msg.text = itemList[position].message.message
                holder.me_time.text = itemList[position].message.TimestampToDate(itemList[position].message.timestamp)
                db.collection("user").document(itemList[position].message.send_id).get().addOnSuccessListener { document ->
                    if (document != null) {
                        Picasso.get().load(document.getString("img")).into(holder.me_img)
                    } else {
                        // 自分のアイコンが取得できなかった時は、未設定時のアイコンを表示する
                        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/firevasetest-1d5b9.appspot.com/o/user_icon%2Fnoimage.png?alt=media&token=b9ae62b8-8c42-4791-9507-c84c93f6871f")
                    }
                }
            }

            1 -> {
                // 相手(メッセージ)
                val holder = holder as ViewGroupYourHolder
                holder.you_msg.text = itemList[position].message.message
                holder.you_time.text = itemList[position].message.TimestampToDate(itemList[position].message.timestamp)
                db.collection("user").document(itemList[position].message.send_id).get().addOnSuccessListener { document ->
                    if (document != null) {
                        Picasso.get().load(document.getString("img")).into(holder.you_img)
                    } else {
                        // 相手のアイコンが取得できなかった時は、未設定時のアイコンを表示する
                        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/firevasetest-1d5b9.appspot.com/o/user_icon%2Fnoimage.png?alt=media&token=b9ae62b8-8c42-4791-9507-c84c93f6871f")
                    }
                }
            }

            2 -> {
                // 自分(画像)
                val holder = holder as ViewGroupMeImgHolder
                holder.me_time.text = itemList[position].message.TimestampToDate(itemList[position].message.timestamp)
                db.collection("user").document(itemList[position].message.send_id).get().addOnSuccessListener {
                    if (it != null) {
                        Picasso.get().load(it.getString("img")).into(holder.me_img)
                    } else {
                        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/firevasetest-1d5b9.appspot.com/o/user_icon%2Fnoimage.png?alt=media&token=b9ae62b8-8c42-4791-9507-c84c93f6871f")
                    }
                }
                // Viewを表示、非表示
                holder.me_msg.visibility = View.GONE
                holder.me_imgSrc.visibility = View.VISIBLE
                // 画像を読み込む
                Picasso.get().load(itemList[position].message.message).into(holder.me_imgSrc)
            }

            3 -> {
                // 相手(画像)
                val holder = holder as ViewGroupYourImgHolder
                holder.you_time.text = itemList[position].message.TimestampToDate(itemList[position].message.timestamp)
                db.collection("user").document(itemList[position].message.send_id).get().addOnSuccessListener {
                    if (it != null) {
                        Picasso.get().load(it.getString("img")).into(holder.you_img)
                    } else {
                        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/firevasetest-1d5b9.appspot.com/o/user_icon%2Fnoimage.png?alt=media&token=b9ae62b8-8c42-4791-9507-c84c93f6871f")
                    }
                }
                // Viewを表示、非表示
                holder.you_msg.visibility = View.GONE
                holder.you_imgSrc.visibility = View.VISIBLE
                // 画像を読み込む
                Picasso.get().load(itemList[position].message.message).into(holder.you_imgSrc)
            }

            4 -> {
                // ログ
                val holder_log = holder as ViewGroupLogHolder
                holder_log.log_msg.text = itemList[position].message.message
            }
        }
    }
}