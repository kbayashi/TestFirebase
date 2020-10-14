package com.example.testfirebase

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_time_line_add.*
import java.util.*

class TimeLineAddActivity : AppCompatActivity() {

    var adapter:imageListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_line_add)

        adapter = imageListAdapter(this)

        time_line_add_select_photo_recyclerView.adapter = adapter

        //ギャラリー起動
        time_line_add_gararry_imageView.setOnClickListener {
            intent = Intent(Intent.ACTION_PICK)
            intent.setType("image/*")
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, 1)
        }

        //投稿
        time_line_add__add_button.setOnClickListener {

            val millis = System.currentTimeMillis()
            val user = FirebaseAuth.getInstance().uid
            val id = UUID.randomUUID()
            val ref = FirebaseFirestore.getInstance().collection("time-line").document(id.toString())
            val setTimeLine = TimeLine(user!!,time_line_add_editTextTextMultiLine.text.toString(),0,null, adapter!!.get(), id.toString(),millis)
            ref.set(setTimeLine)
            finish()
        }

    }

    //ギャラリーまたはカメラ画像を取得
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("requestcode", "${requestCode}")
        Log.d("resulttcode", "${resultCode}")

        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data!!.data != null) {
            adapter?.clear()


            val clipData = data?.clipData

            for (i in 0.. (clipData!!.itemCount-1)) {

                Log.d("count", "$i")
                var item = clipData!!.getItemAt(i)
                var selectPhotUri = item.uri
                var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectPhotUri)
                adapter?.add(bitmap, clipData!!.itemCount,selectPhotUri)
            }
            if(clipData!!.itemCount > 1){
                time_line_add_select_photo_recyclerView.layoutManager = GridLayoutManager(this,2)
            }else{
                time_line_add_select_photo_recyclerView.layoutManager = LinearLayoutManager(this)
            }

            time_line_add_select_photo_recyclerView.adapter = adapter

        }



    }

}