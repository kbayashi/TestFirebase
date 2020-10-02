package com.example.testfirebase

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList

class imageListAdapter(private val context: Context)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //１行で使用する各部品（ビュー）を保持したもの
    class OneViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val ImageView: ImageView = itemView.findViewById(R.id.image_list_row_imageView)
    }

    class MultiViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val MultiImageView:ImageView = itemView.findViewById(R.id.image_list_multi_row_imageView)
    }


    //現状userデータがないのでダミーデータを格納するだけの処理になっている
    class imageListItem(val bitmap: Bitmap, val count: Int,val uri:Uri){}

    private var itemList = mutableListOf<imageListItem>()

    fun add(bitmap: Bitmap, count: Int, uri: Uri){
        itemList.add(imageListItem(bitmap,count, uri))
    }

    fun get(): ArrayList<String>?{
        var imgList:MutableList<String>? = null
        itemList.forEach {
            var filename = UUID.randomUUID().toString()
            var ref = FirebaseStorage.getInstance().getReference("/time_line/$filename")
            ref.putFile(it.uri).addOnSuccessListener {
                Log.d("アップロード成功", "成功")
                imgList?.add(it.toString())
                val uid = FirebaseAuth.getInstance().uid
                val dbRef = FirebaseFirestore.getInstance().collection("time-line-img").document(uid!!).collection("test").document()
                ref.downloadUrl.addOnSuccessListener {
                    var hashMap = hashMapOf(
                        "test" to it.toString()
                    )
                    dbRef.set(hashMap)
                }
            }.addOnFailureListener {
                Log.d("アップロード失敗", it.message)
            }
        }
        Log.d("リストの中身", "$imgList")
        return imgList as ArrayList<String>?
    }

    fun clear(){
        itemList.clear()
    }


    //セルが必要になるたびに呼び出される。
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        //ビューを生成
        if(viewType == 0) {
            val layout = layoutInflater.inflate(R.layout.image_list_multi_row, parent, false)
            return imageListAdapter.MultiViewHolder(layout)
        }else{
            val layout = layoutInflater.inflate(R.layout.image_list_row, parent, false)
            return imageListAdapter.OneViewHolder(layout)
        }
    }


    //複数画像がある場合レイアウトを切り替える
    override fun getItemViewType(position: Int): Int {
        Log.d("adapter", "${itemList[position].count}")
        if(itemList[position].count > 1){
            return 0
        }else{
            return 1
        }
    }

    override fun getItemCount(): Int = itemList.size

    //保持されているビューにデータなどを設定するここでリスナなどを設定する
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType) {
            0->{
                val holder = holder as MultiViewHolder
                holder.MultiImageView.setImageBitmap(itemList[position].bitmap)
            }
            1->{
                val holder = holder as OneViewHolder
                holder.ImageView.setImageBitmap(itemList[position].bitmap)
            }
        }

    }
}