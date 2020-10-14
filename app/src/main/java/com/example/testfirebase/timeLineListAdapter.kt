package com.example.testfirebase

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import timeLineImageListAdapter

class timeLineListAdapter(private val context: Context)
    : RecyclerView.Adapter<timeLineListAdapter.ViewHolder>() {

    var itemClickListner : ((TimeLine)->Unit)? = null

    fun setOnclickListener(listener:(TimeLine)->Unit){
        itemClickListner = listener
    }

    //１行で使用する各部品（ビュー）を保持したもの
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.time_line_recyclerview_row_name_textView)
        val ImageView: ImageView = itemView.findViewById(R.id.time_line_recyclerview_row_imageView)
        val editTextMutable:EditText = itemView.findViewById(R.id.time_line_recyclerview_row_editTextTextMultiLine)
        val goodCount:TextView = itemView.findViewById(R.id.time_line_recyclerview_row_goodCount_textView)
        val RecyclerView:RecyclerView = itemView.findViewById(R.id.time_line_recyclerview_row_image_recyclerView)
        val GoodButton:Button = itemView.findViewById(R.id.time_line_recyclerview_row_good_textView)
        val comment: ImageView = itemView.findViewById(R.id.time_line_recycletview_row_comment_imageView)
        val time:TextView = itemView.findViewById(R.id.time_line_recyclerview_row_time_textView)
    }


    //現状userデータがないのでダミーデータを格納するだけの処理になっている
    class timeLineListItem(val timeLine: TimeLine){}

    private var itemList = mutableListOf<timeLineListItem>()

    fun add(timeLine: TimeLine){
        itemList.add(timeLineListItem(timeLine))
    }

    fun clear(){
        itemList.clear()
    }


    //セルが必要になるたびに呼び出される。
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        //ビューを生成
        val layout = layoutInflater.inflate(R.layout.time_line_recyclerview_row, parent, false)
        return timeLineListAdapter.ViewHolder(layout)
    }

    override fun getItemCount(): Int = itemList.size

    //保持されているビューにデータなどを設定するここでリスナなどを設定する
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.editTextMutable.setText(itemList[position].timeLine.text)

        holder.time.text = itemList[position].timeLine.getTime(itemList[position].timeLine.time)
        holder.goodCount.text = itemList[position].timeLine.good.toString()

        //コメント画面へ移動
        holder.comment.setOnClickListener {
            itemClickListner?.invoke(itemList[position].timeLine)
        }

        Log.d("document",itemList[position].timeLine.id)


        val adapter = timeLineImageListAdapter(context)

        //カウントアップ
        holder.GoodButton.setOnClickListener {
            val ref = FirebaseFirestore.getInstance().collection("time-line").document(itemList[position].timeLine.id)
            itemList[position].timeLine.good += 1
            ref.set(itemList[position].timeLine)
            holder.GoodButton.isEnabled = false
            holder.goodCount.text = itemList[position].timeLine.good.toString()
        }

        FirebaseFirestore.getInstance().collection("user").document(FirebaseAuth.getInstance().uid!!).get().addOnSuccessListener {
            Picasso.get().load(it["img"].toString()).
            into(holder.ImageView)
        }


        //画像の設定
        if(itemList[position].timeLine.imgRef != null) {
            FirebaseFirestore.getInstance().collection("time-line-img").document("get")
                .collection(itemList[position].timeLine.imgRef!!).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    querySnapshot?.forEach {
                        if(querySnapshot.size() > 1){
                            holder.RecyclerView.layoutManager = GridLayoutManager(context,2)
                        }else{
                            holder.RecyclerView.layoutManager = LinearLayoutManager(context)
                        }
                        adapter.add(querySnapshot.size(),it["test"].toString())
                    }
                    holder.RecyclerView.adapter = adapter
                }
        }



    }



}