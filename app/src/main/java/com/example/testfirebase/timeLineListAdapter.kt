package com.example.testfirebase

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import timeLineImageListAdapter


class timeLineListAdapter(private val context: Context)
    : RecyclerView.Adapter<timeLineListAdapter.ViewHolder>() {

    companion object{
        val TIME_LINE_EDIT = "TIME_LINE_EDIT"
    }

    //コメント表示画面に画面遷移
    var commentButtonClickListner : ((TimeLine)->Unit)? = null

    fun setOnCommentClickListener(listener:(TimeLine)->Unit){
        commentButtonClickListner = listener
    }
    //タイムライン編集画面に遷移
    var timeLineEditClickListner:((TimeLine)->Unit)? = null

    fun setOnTimeLineEditListner(listener:(TimeLine)->Unit){
        timeLineEditClickListner = listener
    }

    val uid = FirebaseAuth.getInstance().uid.toString()

    //１行で使用する各部品（ビュー）を保持したもの
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.time_line_recyclerview_row_name_textView)
        val ImageView: ImageView = itemView.findViewById(R.id.time_line_recyclerview_row_imageView)
        val editTextMutable:EditText = itemView.findViewById(R.id.time_line_recyclerview_row_editTextTextMultiLine)
        val goodCount:TextView = itemView.findViewById(R.id.time_line_recyclerview_row_goodCount_textView)
        val RecyclerView:RecyclerView = itemView.findViewById(R.id.time_line_recyclerview_row_image_recyclerView)
        val GoodButton:ImageView = itemView.findViewById(R.id.time_line_recyclerview_row_good_textView)
        val comment: ImageView = itemView.findViewById(R.id.time_line_recycletview_row_comment_imageView)
        val time:TextView = itemView.findViewById(R.id.time_line_recyclerview_row_time_textView)
        val setting:ImageView = itemView.findViewById(R.id.time_line_recyclerview_row_setting_imageView)
        val commentCount:TextView = itemView.findViewById(R.id.time_line_recyclerview_comment_count_textView)
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

    /*fun remove(position: Int){
        itemList.removeAt(position)
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, itemList.size)
    }*/


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

        val uid = FirebaseAuth.getInstance().uid.toString()
        val goodRef = FirebaseFirestore.getInstance().collection("time-line-good").document(itemList[position].timeLine.id)
            .collection("get")

        holder.editTextMutable.setText(itemList[position].timeLine.text)
        holder.time.text = itemList[position].timeLine.getTime(itemList[position].timeLine.time)
        if(uid != itemList[position].timeLine.uid){
            holder.setting.visibility = View.GONE
        }

        //設定ダイアログを表示
        holder.setting.setOnClickListener {

            val builder =  AlertDialog.Builder(context)
            val items =
                arrayOf("タイムライン編集", "削除")

            builder.setItems(items, DialogInterface.OnClickListener { dialogInterface, i ->
                when(i){
                    //編集
                    0 ->{
                        timeLineEditClickListner?.invoke(itemList[position].timeLine)
                    }
                    //削除
                    1->{

                        deleteTimeLine(position)
                        Log.d("タイムライン自体削除", "aaaaa")

                    }

                }
            }).show()

        }

        //カウント取得
        getCount(goodRef,holder)
        getCount(holder,position)

        //コメント画面へ移動
        holder.comment.setOnClickListener {
            commentButtonClickListner?.invoke(itemList[position].timeLine)
        }

        Log.d("document",itemList[position].timeLine.id)

        val adapter = timeLineImageListAdapter(context)

        //カウントアップ
        holder.GoodButton.setOnClickListener {
            countUp(position,uid,holder.GoodButton)
        }

        FirebaseFirestore.getInstance().collection("user").document(itemList[position].timeLine.uid).get().addOnSuccessListener {
            Picasso.get().load(it["img"].toString()).
            into(holder.ImageView)
            holder.name.text = it["name"].toString()
        }


        //画像の設定
        if(itemList[position].timeLine.imgRef != null) {
            setImage(position,adapter,holder)
        }

    }



    //タイムラインに画像を設定
    private fun setImage(position: Int, adapter: timeLineImageListAdapter, holder: ViewHolder){
        var count = 0
        FirebaseFirestore.getInstance().collection("time-line-img").document("get")
            .collection(itemList[position].timeLine.imgRef!!).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                adapter.cler()
                //Log.d("colection", "$querySnapshot")
                querySnapshot?.forEach {

                    Log.d("ドキュメンt","$it")
                    Log.d("size", "${querySnapshot.size()}")
                    if(querySnapshot.size() > 1){
                        holder.RecyclerView.layoutManager = GridLayoutManager(context,2)
                    }else{
                        holder.RecyclerView.layoutManager = LinearLayoutManager(context)
                    }

                    count++
                    adapter.add(querySnapshot.size(),it["test"].toString())
                }
                Log.d("count", "$count")
                holder.RecyclerView.adapter = adapter
            }
    }

    //カウント取得
    private fun getCount(goodRef:CollectionReference, holder: ViewHolder){

        goodRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if(querySnapshot!!.size() > 0) {
                holder.goodCount.text = querySnapshot.size().toString()
                querySnapshot.forEach {
                    Log.d("goodID", it.id)
                    if(it.id == uid){
                        holder.GoodButton.setImageResource(R.drawable.ic_baseline_yellow_thumb_up_24)
                    }
                }
            }else{
                holder.goodCount.text = "0"
            }

        }

    }

    private fun getCount(holder: ViewHolder,position: Int){
        FirebaseFirestore.getInstance().collection("time-line-comment").document(itemList[position].timeLine.id)
            .collection("get").get().addOnSuccessListener {
                holder.commentCount.text = it.size().toString()
            }

    }
    //グッドを増やしたり減らしたり
    private fun countUp(position: Int,uid:String , imageView: ImageView){
        val ref = FirebaseFirestore.getInstance().collection("time-line-good").document(itemList[position].timeLine.id)
            .collection("get").document(uid)
        ref.get().addOnSuccessListener {
            if(it.toObject(Good::class.java) == null){
                var hashMap = hashMapOf(
                    "goood" to "good"
                )
                ref.set(hashMap)
                imageView.setImageResource(R.drawable.ic_baseline_yellow_thumb_up_24)
            }else{

                ref.delete()
                imageView.setImageResource(R.drawable.ic_baseline_thumb_up_24)
            }

        }.addOnFailureListener {

        }
    }

    //タイムライン削除
    private fun deleteTimeLine(position: Int) {

        //good
        FirebaseFirestore.getInstance().collection("time-line-good")
            .document(itemList[position].timeLine.id)
            .collection("get").get().addOnSuccessListener {
                //Log.d("document????", "$it")
                it.forEach {
                    //Log.d("なにこれ?", it.id.toString())
                    FirebaseFirestore.getInstance().collection("time-line-good")
                        .document(itemList[position].timeLine.id)
                        .collection("get").document(it.id).delete()

                }
                //コメント
                FirebaseFirestore.getInstance().collection("time-line-comment")
                    .document(itemList[position].timeLine.id)
                    .collection("get").get().addOnSuccessListener {
                        it.forEach { item ->
                            //Log.d("湖面t－", item.id)
                            FirebaseFirestore.getInstance().collection("time-line-comment")
                                .document(itemList[position].timeLine.id)
                                .collection("get").document(item.id).delete()
                        }

                        //Log.d("米mンと削除チュ", "こめんと")


                        //イメージ
                        if (itemList[position].timeLine.imgRef != null){
                            FirebaseFirestore.getInstance().collection("time-line-img")
                                .document("get").collection(itemList[position].timeLine.imgRef!!)
                                .get().addOnSuccessListener {
                                    for (i in 0..(it.size() - 1)) {
                                        FirebaseFirestore.getInstance().collection("time-line-img")
                                            .document("get")
                                            .collection(itemList[position].timeLine.imgRef!!)
                                            .document(i.toString()).delete()
                                    }
                                    FirebaseFirestore.getInstance().collection("time-line").document(itemList[position].timeLine.id).delete().addOnSuccessListener {
                                    }

                                }

                        }else{
                            FirebaseFirestore.getInstance().collection("time-line").document(itemList[position].timeLine.id).delete().addOnSuccessListener {
                            }
                        }
                    }
            }





        //タイムライン

    }
}