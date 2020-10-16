package com.example.testfirebase

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

        //カメラ起動
        time_line_add_camera_imageView.setOnClickListener {
            // カメラ機能を実装したアプリが存在するかチェック
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(packageManager)?.let {
                if (checkCameraPermission()) {
                    takePicture()
                } else {
                    grantCameraPermission()
                }
            } ?: Toast.makeText(this, "カメラを扱うアプリがありません", Toast.LENGTH_LONG).show()
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

    //カメラ起動
    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
        }
        startActivityForResult(intent, 2)
    }

    //カメラ使用権限確認
    private fun checkCameraPermission() = PackageManager.PERMISSION_GRANTED ==
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)

    //権限取得
    private fun grantCameraPermission() =
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.CAMERA),
            3)

    //権限取得シた場合カメラ起動
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == 3) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture()
            }
        }
    }

}