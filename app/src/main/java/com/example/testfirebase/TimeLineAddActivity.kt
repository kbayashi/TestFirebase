package com.example.testfirebase

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_time_line_add.*

class TimeLineAddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_line_add)

        button.setOnClickListener {
            intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("select ", "${data.data}")
            //単一選択
            val selectPhotUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectPhotUri)
            imageView.setImageBitmap(bitmap)
            //複数選択
        }else{
            val clipData = data?.clipData

            var item = clipData!!.getItemAt(0)
            var selectPhotUri = item.uri
            var bitmap = MediaStore.
                Images.Media.getBitmap(contentResolver, selectPhotUri)
            imageView.setImageBitmap(bitmap)

            item = clipData!!.getItemAt(1)
            selectPhotUri = item.uri
            bitmap = MediaStore.
            Images.Media.getBitmap(contentResolver, selectPhotUri)
            imageView2.setImageBitmap(bitmap)

        }



    }
}