package com.example.testfirebase

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class latestChatListAdapter(private val context: Context)
    : RecyclerView.Adapter<latestChatListAdapter.ViewHolder>(){

    private val me = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val LATEST = "LATESTCHAT"

    // 個人
    var itemClickListner : ((User)->Unit)? = null
    fun setOnclickListener(listener:(User)->Unit){
        itemClickListner = listener
    }

    // グループ
    var itemGroupClickListener: ((String)->Unit)? = null
    fun setOnGroupClickListener(listener: (String)->Unit){
        itemGroupClickListener = listener
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val latestChatTitle:TextView = itemView.findViewById(R.id.latest_chat_row_title_textView)
        val latestChatLatst:TextView = itemView.findViewById(R.id.latest_chat_row_chat_textView)
        val latestChatTime:TextView = itemView.findViewById(R.id.latest_chat_row_time_textView)
        val latestChatTImage:ImageView = itemView.findViewById(R.id.latest_chat_row_imageView)
    }

    //現状データがないのでダミーデータを格納するだけの処理になっている
    class latestChatListItem(val message: Message)

    private var itemList = mutableListOf<latestChatListItem>()

    //アダプターに追加
    fun add(message: Message){
        itemList.add(latestChatListItem(message))
    }

    //全削除
    fun latesItemDel(){
        itemList.clear()
    }

    //ビューを生成
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val layout = layoutInflater.inflate(R.layout.latest_chat_row, parent, false)
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {

            // 個人
            var opponentUser: String? = null

            // 自分か相手か判定
            if (itemList[position].message.receive_user != me.uid) {
                opponentUser = itemList[position].message.receive_user
            } else if (itemList[position].message.send_user != me.uid) {
                opponentUser = itemList[position].message.send_user
            }

            Log.d("OPPENTUSER", opponentUser)

            db.collection("user").document(opponentUser!!)
                .get().addOnSuccessListener {
                    val getUser = it.toObject(User::class.java)
                    Log.d("OPPENTUSER", "${getUser}")
                    holder.latestChatLatst.text = itemList[position].message.message
                    holder.latestChatTime.text = itemList[position].message.changeTimeStampToString(itemList[position].message.time)

                    // 個人かグループか判定
                    if (itemList[position].message.group_flag == false){

                        // 個人
                        holder.latestChatTitle.text = getUser?.name
                        Picasso.get().load(getUser?.img).into(holder.latestChatTImage)

                        holder.itemView.setOnClickListener {
                            Log.d(LATEST, "タッチ")
                            FirebaseFirestore.getInstance().collection("user").document(getUser?.uid!!)
                            itemClickListner?.invoke(getUser)
                        }

                    }else{

                        // グループ
                        db.collection("group").document(itemList[position].message.send_user)
                            .get().addOnSuccessListener {
                                val getGroup = it.toObject(Group::class.java)
                                holder.latestChatTitle.text = getGroup?.name
                                Picasso.get().load(getGroup?.icon).into(holder.latestChatTImage)

                                holder.itemView.setOnClickListener {
                                    itemGroupClickListener?.invoke(itemList[position].message.send_user)
                                }

                            }.addOnFailureListener {
                                Log.d("失敗", it.message)
                            }
                    }

                }.addOnFailureListener {
                    Log.d("失敗", it.message)
                }
    }
}