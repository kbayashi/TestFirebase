package com.example.testfirebase

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NavUtils
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_user_profile_edit.*
import java.util.*

class UserProfileEditActivity : AppCompatActivity() {

    //companion：static変数、メソッドの代わり
    companion object {
        val USER_REGISTAR = "Register Activity"
    }
    private var my_user:User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_edit)

        // 自分のユーザ情報を取得
        val auth = FirebaseAuth.getInstance()
        val me = auth.currentUser
        // データベースオブジェクト
        val db = FirebaseFirestore.getInstance()

        // 取得
        val docRef = db.collection("user").document(me!!.uid)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            my_user = documentSnapshot.toObject(User::class.java)
            // タイトル
            setTitle(my_user?.name)
            // プロパティ
            user_profile_edit_user_name_textView.text = my_user?.name
            user_profile_edit_user_pr_textView.text = my_user?.pr
            user_profile_edit_user_gender_textView.text = my_user?.gender
            user_profile_edit_user_old_textView.text = my_user?.birthday
            user_profile_edit_user_prefectures_textView.text = my_user?.live
            user_profile_edit_user_hobby_textView.text = my_user?.hoby
            user_profile_edit_user_sick_textView.text = my_user?.sick
            user_profile_edit_user_lifeexpectancy_textView.text = my_user?.life_expectancy
            Picasso.get().load(my_user?.img).into(user_profile_edit_imageview)
        }
        
        // 名前
        name_relativelayout.setOnClickListener{
            val intent = Intent(this,UserProfileEditPlainTextActivity::class.java)
            intent.putExtra("table", "name")
                .putExtra("edit",user_profile_edit_user_name_textView.text)
            startActivity(intent)
        }

        // 自己紹介
        pr_relativelayout.setOnClickListener{
            val intent = Intent(this,UserProfileEditPlainTextActivity::class.java)
            intent.putExtra("table","pr")
                .putExtra("edit",user_profile_edit_user_pr_textView.text)
            startActivity(intent)
        }

        // 性別
        gender_relativelayout.setOnClickListener{
            val intent = Intent(this,UserProfileEditRadioButtonActivity::class.java)
            intent.putExtra("table","gender")
                .putExtra("edit",user_profile_edit_gender_textView.text)
            startActivity(intent)
        }

        // 生年月日
        old_relativelayout.setOnClickListener{
            val intent = Intent(this,UserProfileEditSpinnerActivity::class.java)
            intent.putExtra("table","birthday")
                .putExtra("edit",user_profile_edit_old_textView.text)
            startActivity(intent)
        }

        // 住所
        prefecture_relativelayout.setOnClickListener{
            val intent = Intent(this,UserProfileEditSpinnerActivity::class.java)
            intent.putExtra("table","live")
                .putExtra("edit",user_profile_edit_prefectures_textView.text)
            startActivity(intent)
        }

        // 病名
        sick_relativelayout.setOnClickListener {
            val intent = Intent(this,UserProfileEditSpinnerActivity::class.java)
            intent.putExtra("table","sick")
                .putExtra("edit",user_profile_edit_sick_textView.text)
            startActivity(intent)
        }

        // 余命
        lifeexpectancy_relativelayout.setOnClickListener {
            val intent = Intent(this,UserProfileEditSpinnerActivity::class.java)
            intent.putExtra("table","lifeExpectancy")
                .putExtra("edit",user_profile_edit_lifeexpectancy_textView.text)
            startActivity(intent)
        }
        //アイコン
        imageView3.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

    }

    //戻るボタン押下時はこの画面を削除する
    override fun onBackPressed() {
        //自アプリに戻る
        val upIntent = NavUtils.getParentActivityIntent(this)
        if (upIntent != null) {
            NavUtils.navigateUpTo(this, upIntent)
        }
    }

    var selectedPhotoUri: Uri? = null
    //写真アプリで写真を選択後に呼び出される
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //写真選択アプリが呼び出され、ちゃんと操作して、データが入っていたら
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            //写真をボタンの背景に設定
            Log.e("UserRegistarActivity","Photo select")
            Log.d("UserRegistarActivity","${data.data}")
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            user_profile_edit_imageview.setImageBitmap(bitmap)
            uploadImageFirebaseStorage()
        }
    }
    private fun uploadImageFirebaseStorage(){

        if(selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(USER_REGISTAR, "アップロード成功${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    my_user?.img = it.toString()
                    Log.d(USER_REGISTAR, "File 場所$it")
                    FirebaseFirestore.getInstance().collection("user").document(my_user!!.uid).set(my_user!!)
                }
            }
            .addOnFailureListener{
                //do same logging here
                Log.d(USER_REGISTAR, "作成に失敗しました ${it.message}")
                Toast.makeText(this, "作成に失敗しました ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

}