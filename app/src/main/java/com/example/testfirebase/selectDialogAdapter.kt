package com.example.testfirebase

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class selectDialogAdapter(private val context: Context)
    : RecyclerView.Adapter<selectDialogAdapter.ViewHolder>() {

    //１行で使用する各部品（ビュー）を保持したもの
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val radioButton: RadioButton = itemView.findViewById(R.id.select_dialog_item_radioButton)
    }

    class selectDialogListItem(val text:String){}

    private var itemList = mutableListOf<selectDialogListItem>()
    private var checkedPosition: Int = 0
    public val checkedText: String
        get() = itemList[checkedPosition].text

    fun add(text: String){
        itemList.add(selectDialogListItem(text))
    }

    //セルが必要になるたびに呼び出される。
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        //ビューを生成
        val layout = layoutInflater.inflate(R.layout.select_dialog_item_row, parent, false)
        return selectDialogAdapter.ViewHolder(layout)
    }

    override fun getItemCount(): Int = itemList.size

    //保持されているビューにデータなどを設定するここでリスナなどを設定する
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.radioButton.text = itemList[position].text
        holder.radioButton.setOnClickListener(null) //一度外さないと, 再利用時に check の更新で前のが動く(ラジオボタンなら害無し?)
        holder.radioButton.isChecked = (position == checkedPosition)
        holder.radioButton.setOnClickListener {
            val oldPosition = checkedPosition
            if (oldPosition != position) {
                checkedPosition = position
                notifyItemChanged(oldPosition)
            }
        }


    }



}