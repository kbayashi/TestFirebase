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

    val LATEST = "LATESTCHAT"

    var itemClickListner : ((User)->Unit)? = null

    fun setOnclickListener(listener:(User)->Unit){
        itemClickListner = listener
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val latestChatTitle:TextView = itemView.findViewById(R.id.latest_chat_row_title_textView)
        val latestChatLatst:TextView = itemView.findViewById(R.id.latest_chat_row_chat_textView)
        val latestChatTime:TextView = itemView.findViewById(R.id.latest_chat_row_time_textView)
        val latestChatTImage:ImageView = itemView.findViewById(R.id.latest_chat_row_imageView)
    }

    //現状データがないのでダミーデータを格納するだけの処理になっている
    class latestChatListItem(val message: Message){}

    private var itemList = mutableListOf<latestChatListItem>()

    //アダプターに追加
    fun add(message: Message){
        itemList.add(latestChatListItem(message))
    }
    //全削除
    fun latesItemDel(){
        itemList.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        //ビューを生成
        val layout = layoutInflater.inflate(R.layout.latest_chat_row, parent, false)
        return latestChatListAdapter.ViewHolder(layout)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {

        val loginUid = FirebaseAuth.getInstance().uid.toString()
        var opponentUser :String? = null
        //var getUser:User? = null

        if(itemList[position].message.receive_user != loginUid){
            opponentUser = itemList[position].message.receive_user
        }else if(itemList[position].message.send_user != loginUid){
            opponentUser = itemList[position].message.send_user
        }

        Log.d("OPPENTUSER", opponentUser)

        val ref = FirebaseFirestore.getInstance().collection("user").document(opponentUser!!)
            .get().addOnSuccessListener {
                val getUser = it.toObject(User::class.java)
                Log.d("OPPENTUSER", "${getUser}")
                holder.latestChatLatst.text = itemList[position].message.message
                holder.latestChatTitle.text = getUser?.name
                Picasso.get().load(getUser?.img).into(holder.latestChatTImage)
                holder.latestChatTime.text = itemList[position].message.changeTimeStampToString(itemList[position].message.time)

                holder.itemView.setOnClickListener {
                    Log.d(LATEST,"タッチ")
                    FirebaseFirestore.getInstance().collection("user").document(getUser?.uid!!)
                    itemClickListner?.invoke(getUser)
                }

            }.addOnFailureListener {
                Log.d("失敗", it.message)
            }

        

    }

}