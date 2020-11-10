package com.example.testfirebase

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class friendTemporaryRegistrationAdapter(private val context: Context)
    : RecyclerView.Adapter<friendTemporaryRegistrationAdapter.ViewHolder>(){

    var itemClickListner : ((User)->Unit)? = null

    fun setOnClickListener(listener:(User)->Unit){
        itemClickListner = listener
    }



    //１行で使用する各部品（ビュー）を保持したもの
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val user_name: TextView = itemView.findViewById(R.id.user_list_row_user_name_textview)
        val user_pr: TextView = itemView.findViewById(R.id.user_list_row_user_pr_textview)
        val user_image: ImageView = itemView.findViewById(R.id.user_list_row_user_imageView)
    }

    class friendTemporaryListItem(val user: User){}

    fun clear(position: Int){
        itemList.removeAt(position)
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, itemList.size)
    }

    private var itemList = mutableListOf<friendTemporaryListItem>()

    fun add(user: User){
        itemList.add(friendTemporaryListItem(user))
    }

    //セルが必要になるたびに呼び出される。
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val layoutInflater = LayoutInflater.from(context)
        //ビューを生成
        val layout = layoutInflater.inflate(R.layout.user_list_row, parent, false)
        return friendTemporaryRegistrationAdapter.ViewHolder(layout)

    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val uid = FirebaseAuth.getInstance().uid.toString()
        holder.user_name.text = itemList[position].user.name
        holder.user_pr.text = itemList[position].user.pr
        Picasso.get().load(itemList[position].user.img).
        into(holder.user_image)

        //長押ししたときダイアログを表示
        holder.itemView.setOnLongClickListener {
            val builder =  AlertDialog.Builder(context)
            val items =
                arrayOf("承認", "削除")

            builder.setItems(items, DialogInterface.OnClickListener { dialogInterface, i ->
                when(i){
                    //友達承認
                    0 ->{

                        val firendData = hashMapOf(
                            "uid" to itemList[position].user.uid
                        )

                        FirebaseFirestore.getInstance().collection("user-friend").document("get")
                            .collection(uid).document(itemList[position].user.uid).set(firendData).addOnSuccessListener {
                                FirebaseFirestore.getInstance().collection("friend-temporary-registration")
                                    .document("get").collection(uid).document(itemList[position].user.uid).delete().addOnSuccessListener {
                                        clear(position)
                                        Toast.makeText(context,"友だち追加されました",Toast.LENGTH_LONG).show()
                                    }
                            }
                    }
                    //削除
                    1->{
                        FirebaseFirestore.getInstance().collection("friend-temporary-registration")
                            .document("get").collection(uid).document(itemList[position].user.uid).delete()
                            .addOnSuccessListener {

                                FirebaseFirestore.getInstance().collection("user-friend").document("get")
                                    .collection(itemList[position].user.uid).document(uid).delete().addOnSuccessListener {
                                        Log.d("友達承認削除", "aaa")
                                        clear(position)
                                    }
                            }
                    }

                }
            }).show()
            true
        }
    }

}