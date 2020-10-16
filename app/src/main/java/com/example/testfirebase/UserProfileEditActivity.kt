package com.example.testfirebase

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.example.testfirebase.UserListFragment.Companion.SELECT_USER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_user_profile_edit.*
import java.util.*

class UserProfileEditActivity : AppCompatActivity() {
    //companion：static変数、メソッドの代わり
    companion object {
        val USER_REGISTAR = "Register Activity"
    }
    var get_user:User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_edit)
        val intent = getIntent()
        get_user = intent.getParcelableExtra<User>(SELECT_USER)

        user_profile_edit_user_name_textView.text = get_user?.name
        user_profile_edit_name_textView.text = "名前"
        user_profile_edit_user_pr_textView.text = get_user?.pr
        user_profile_edit_pr_textView.text = "自己紹介"
        user_profile_edit_user_gender_textView.text = get_user?.gender
        user_profile_edit_gender_textView.text = "性別"
        user_profile_edit_user_old_textView.text = get_user?.birthday
        user_profile_edit_old_textView.text = "生年月日"
        user_profile_edit_user_prefectures_textView.text = get_user?.live
        user_profile_edit_prefectures_textView.text = "都道府県"
        user_profile_edit_user_hobby_textView.text = "非公開"
        user_profile_edit_hobby_textView.text = "趣味"
        user_profile_edit_user_sick_textView.text = get_user?.sick
        user_profile_edit_sick_textView.text = "病名"
        user_profile_edit_user_lifeexpectancy_textView.text = get_user?.life_expectancy
        user_profile_edit_lifeexpectancy_textView.text = "余命"
        Picasso.get().load(get_user?.img).into(user_profile_edit_imageview)

        name_relativelayout.setOnClickListener{
            val intent = Intent(this,UserProfileEditPlainTextActivity::class.java)
            intent.putExtra("table", "name")
                .putExtra("edit",get_user?.name)
            startActivity(intent)
        }
        pr_relativelayout.setOnClickListener{
            val intent = Intent(this,UserProfileEditPlainTextActivity::class.java)
            intent.putExtra("table","pr")
                .putExtra("edit",get_user?.pr)
            startActivity(intent)
        }
        gender_relativelayout.setOnClickListener{
            val intent = Intent(this,UserProfileEditRadioButtonActivity::class.java)
            intent.putExtra("table","gender")
                .putExtra("edit",get_user?.gender)
            startActivity(intent)
        }
        old_relativelayout.setOnClickListener{
            val intent = Intent(this,UserProfileEditSpinnerActivity::class.java)
            intent.putExtra("table","birthday")
                .putExtra("edit",get_user?.birthday)
            startActivity(intent)
        }
        prefecture_relativelayout.setOnClickListener{
            val intent = Intent(this,UserProfileEditSpinnerActivity::class.java)
            intent.putExtra("table","live")
                .putExtra("edit",get_user?.live)
            startActivity(intent)
        }
        sick_relativelayout.setOnClickListener {
            val intent = Intent(this,UserProfileEditSpinnerActivity::class.java)
            intent.putExtra("table","sick")
                .putExtra("edit",get_user?.sick)
            startActivity(intent)
        }
        lifeexpectancy_relativelayout.setOnClickListener {
            val intent = Intent(this,UserProfileEditSpinnerActivity::class.java)
            intent.putExtra("table","lifeExpectancy")
                .putExtra("edit",get_user?.life_expectancy)
            startActivity(intent)
        }
        //アイコン
        imageView3.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
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
                    get_user?.img = it.toString()
                    Log.d(USER_REGISTAR, "File 場所$it")
                    FirebaseFirestore.getInstance().collection("user").document(get_user!!.uid).set(get_user!!)
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