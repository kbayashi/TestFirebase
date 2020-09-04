package com.example.testfirebase

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class selectDialogMultipleAdapter(private val context: Context)
    : RecyclerView.Adapter<selectDialogMultipleAdapter.ViewHolder>(){


    //１行で使用する各部品（ビュー）を保持したもの
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val checkBox: CheckBox = itemView.findViewById(R.id.select_dialog_item_checkBox)
    }

    class selectDialogListItem(val text:String, var check:Boolean){}

    private var itemList = mutableListOf<selectDialogListItem>()

    fun add(text: String){
        itemList.add(selectDialogListItem(text, false))
    }

    fun clear(){
        itemList.clear()
    }

    fun setTextView(textView: TextView, selectList: MutableMap<String,MutableList<String>>, key:String){
        var text:String = ""
        var item = mutableListOf<String>()
        itemList.forEach {
            if(it.check == true) {
                text += it.text + " "
                Log.d("チェックされた項目", it.text)
                item.add(it.text)
            }
        }
        textView.text = text
        selectList.put(key, item)
    }

    //セルが必要になるたびに呼び出される。
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        //ビューを生成
        val layout = layoutInflater.inflate(R.layout.select_dialog_check_row, parent, false)
        return selectDialogMultipleAdapter.ViewHolder(layout)
    }

    override fun getItemCount(): Int = itemList.size

    //保持されているビューにデータなどを設定するここでリスナなどを設定する
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.checkBox.text = itemList[position].text
        holder.checkBox.isChecked = itemList[position].check
        holder.checkBox.setOnClickListener(null) //一度外さないと, 再利用時に check の更新で前のが動く

        //チェックonOff
        holder.checkBox.setOnClickListener {
            itemList[position].check = holder.checkBox.isChecked
            notifyItemChanged(position)
        }

    }

}