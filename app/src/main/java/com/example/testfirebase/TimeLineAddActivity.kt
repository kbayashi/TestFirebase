package com.example.testfirebase

import android.Manifest
import android.R
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
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
import java.io.InputStream
import java.net.URL
import java.util.*


class TimeLineAddActivity : AppCompatActivity() {



    companion object{
        var editFlg = false

    }

    var adapter:imageListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.testfirebase.R.layout.activity_time_line_add)

        adapter = imageListAdapter(this)

        val intent = getIntent()
        time_line_add_select_photo_recyclerView.adapter = adapter
        val EditTimeLine:TimeLine? = intent.getParcelableExtra<TimeLine>("TIME_LINE_EDIT")

        if(EditTimeLine != null){
                time_line_add_editTextTextMultiLine.setText(EditTimeLine?.text)
                if(EditTimeLine?.imgRef != null){
                    FirebaseFirestore.getInstance().collection("time-line-img")
                        .document("get").collection(EditTimeLine?.imgRef!!)
                        .get().addOnSuccessListener {items->
                            items.forEach {

                                object : AsyncTask<String?, Void?, Bitmap?>() {
                                    // UI スレッド処理
                                    override fun onPostExecute(image: Bitmap?) {
                                        super.onPostExecute(image)

                                        if (image != null) {
                                            /*val imageView: ImageView =
                                                this@MainActivity.findViewById(R.id.imageView) as ImageView
                                            imageView.setImageBitmap(image)*/
                                            adapter?.add(image,items.size(),null)
                                            this@TimeLineAddActivity.time_line_add_select_photo_recyclerView.adapter = adapter
                                        }
                                    }

                                    override fun doInBackground(vararg p0: String?): Bitmap? {
                                        var image: Bitmap? = null
                                        val options: BitmapFactory.Options
                                        try {

                                            val url = URL(p0[0])
                                            options = BitmapFactory.Options()
                                            // 実際に読み込む
                                            options.inJustDecodeBounds = false
                                            val stream =
                                                url.content as InputStream
                                            image = BitmapFactory.decodeStream(stream , null, options)
                                            stream.close()
                                        } catch (e: Exception) {
                                            Log.i("button", e.message)
                                        }
                                        return image
                                    }
                                }.execute(it["test"].toString()) // JPEG 画像の 短縮 URL


                            }

                            time_line_add_select_photo_recyclerView.adapter = adapter
                        }
                }


        }

        //ギャラリー起動
        time_line_add_gararry_imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
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
            val setTimeLine = TimeLine(user!!,time_line_add_editTextTextMultiLine.text.toString(),null,null, adapter!!.get(), id.toString(),millis)
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
            if (requestCode == 1) {
                val clipData = data?.clipData
                //画像の枚数が6枚以上ならキャンセル
                if(clipData!!.itemCount > 6) {
                    Toast.makeText(this,"投稿する写真は写真は６枚以下でお願いします。", Toast.LENGTH_LONG).show()
                    return
                }
                for (i in 0..(clipData!!.itemCount - 1)) {

                    Log.d("count", "$i")
                    var item = clipData!!.getItemAt(i)
                    var selectPhotUri = item.uri
                    var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectPhotUri)
                    adapter?.add(bitmap, clipData!!.itemCount, selectPhotUri)
                }
                if (clipData!!.itemCount > 1) {
                    time_line_add_select_photo_recyclerView.layoutManager =
                        GridLayoutManager(this, 2)
                } else {
                    time_line_add_select_photo_recyclerView.layoutManager =
                        LinearLayoutManager(this)
                }

                time_line_add_select_photo_recyclerView.adapter = adapter

            }
        }else if(requestCode == 2 ){
            adapter?.clear()
            val bitmap = data!!.getExtras()!!.get("data") as Bitmap

            adapter?.add( bitmap,1, null)
            time_line_add_select_photo_recyclerView.layoutManager =
                LinearLayoutManager(this)
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

