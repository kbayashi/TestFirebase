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
import kotlinx.android.synthetic.main.activity_user_registar.*
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
            val dialog = UserProfileEditPlainTextDialog("名前を変更",user_profile_edit_user_name_textView, "name", user_profile_edit_user_name_textView.text.toString())
            dialog.show(supportFragmentManager, "名前")
        }

        // 自己紹介
        pr_relativelayout.setOnClickListener{
            val dialog = UserProfileEditPlainTextDialog("自己紹介を変更",user_profile_edit_user_pr_textView, "pr", user_profile_edit_user_pr_textView.text.toString())
            dialog.show(supportFragmentManager, "名前")
        }

        // 性別
        gender_relativelayout.setOnClickListener{
            val dialog = UserProfileEditRadioButtonDialog("性別を変更",user_profile_edit_user_gender_textView, "gender", user_profile_edit_user_gender_textView.text.toString())
            dialog.show(supportFragmentManager, "性別")
        }

        // 生年月日
        old_relativelayout.setOnClickListener{
            val dialog = UserProfileEditSpinnerDialog("生年月日を変更",user_profile_edit_user_old_textView, "birthday", user_profile_edit_user_old_textView.text.toString())
            dialog.show(supportFragmentManager, "生年月日")
        }

        // 住所
        prefecture_relativelayout.setOnClickListener{
            val dialog = UserProfileEditSpinnerDialog("都道府県を変更",user_profile_edit_user_prefectures_textView, "live", user_profile_edit_user_prefectures_textView.text.toString())
            dialog.show(supportFragmentManager, "都道府県")
        }
        //趣味
        hobby_relativelayout.setOnClickListener{
            val dialog = UserProfileEditCheckDialog("趣味を変更",user_profile_edit_user_hobby_textView, "hoby",user_profile_edit_user_hobby_textView.text.toString())
            dialog.show(supportFragmentManager, "趣味")
        }
        // 病名
        sick_relativelayout.setOnClickListener {
            val dialog = UserProfileEditCheckDialog("病名を変更",user_profile_edit_user_sick_textView, "sick",user_profile_edit_user_sick_textView.text.toString())
            dialog.show(supportFragmentManager, "病名")
        }

        // 余命
        lifeexpectancy_relativelayout.setOnClickListener {
            val dialog = UserProfileEditSpinnerDialog("余命を変更",user_profile_edit_user_old_textView, "life_expectancy", user_profile_edit_user_old_textView.text.toString())
            dialog.show(supportFragmentManager, "余命")
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