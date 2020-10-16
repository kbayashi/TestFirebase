//package com.example.testfirebase

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.testfirebase.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*

//タイムラインに表示する画像
class timeLineImageListAdapter (private val context: Context):
    RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    //１行で使用する各部品（ビュー）を保持したもの
    class OneViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val ImageView: ImageView = itemView.findViewById(R.id.image_list_row_imageView)
    }

    class MultiViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val MultiImageView: ImageView = itemView.findViewById(R.id.image_list_multi_row_imageView)
    }

    class imageListItem(val count: Int, val string: String){}

    private var itemList = mutableListOf<imageListItem>()

    fun add(count: Int, string: String){
        itemList.add(imageListItem(count, string))
    }

    fun cler(){
        itemList.clear()
    }


    //セルが必要になるたびに呼び出される。
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        //ビューを生成
        if(viewType == 0) {
            val layout = layoutInflater.inflate(R.layout.image_list_multi_row, parent, false)
            return timeLineImageListAdapter.MultiViewHolder(layout)
        }else{
            val layout = layoutInflater.inflate(R.layout.image_list_row, parent, false)
            return timeLineImageListAdapter.OneViewHolder(layout)
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
                val holder = holder as timeLineImageListAdapter.MultiViewHolder
                Picasso.get().load(itemList[position].string).
                into(holder.MultiImageView)
            }
            1->{
                val holder = holder as timeLineImageListAdapter.OneViewHolder
                Picasso.get().load(itemList[position].string).
                into(holder.ImageView)
            }
        }

    }


}
