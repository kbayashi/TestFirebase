package com.example.testfirebase

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.squareup.picasso.Picasso
import com.example.testfirebase.UserListFragment.Companion.SELECT_USER
import kotlinx.android.synthetic.main.activity_user_profile_edit.*

class UserProfileEditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_edit)
        val intent = getIntent()
        val get_user = intent.getParcelableExtra<User>(SELECT_USER)

        user_profile_edit_name_textView.text = "名前:"+get_user.name
        user_profile_edit_pr_textView.text = "自己紹介:"+get_user.pr
        user_profile_edit_gender_textView.text = "性別:"+get_user.gender
        user_profile_edit_old_textView.text = "年齢:"+get_user.birthday
        user_profile_edit_prefectures_textView.text = "都道府県:"+get_user.live
        user_profile_edit_hobby_textView.text = "趣味:非公開"
        user_profile_edit_sick_textView.text = "病名:"+get_user.sick
        user_profile_edit_lifeexpectancy_textView.text = "余命:"+get_user.life_expectancy
        Picasso.get().load(get_user.img).into(user_profile_edit_imageview)

        user_profile_edit_name_textView.setOnClickListener{
            val intent = Intent(this,UserProfileEditPlainTextActivity::class.java)
            intent.putExtra("table", "name")
                .putExtra("edit",get_user.name)
            startActivity(intent)
        }
        user_profile_edit_pr_textView.setOnClickListener{
            val intent = Intent(this,UserProfileEditPlainTextActivity::class.java)
            intent.putExtra("table","pr")
                .putExtra("edit",get_user.pr)
            startActivity(intent)
        }
        user_profile_edit_gender_textView.setOnClickListener{
            val intent = Intent(this,UserProfileEditRadioButtonActivity::class.java)
            intent.putExtra("table","gender")
                .putExtra("edit",get_user.gender)
            startActivity(intent)
        }
        user_profile_edit_old_textView.setOnClickListener{
            val intent = Intent(this,UserProfileEditSpinnerActivity::class.java)
            intent.putExtra("table","birthday")
                .putExtra("edit",get_user.birthday)
            startActivity(intent)
        }
        user_profile_edit_prefectures_textView.setOnClickListener{
            val intent = Intent(this,UserProfileEditSpinnerActivity::class.java)
            intent.putExtra("table","live")
                .putExtra("edit",get_user.live)
            startActivity(intent)
        }
        user_profile_edit_sick_textView.setOnClickListener {
            val intent = Intent(this,UserProfileEditSpinnerActivity::class.java)
            intent.putExtra("table","sick")
                .putExtra("edit",get_user.sick)
            startActivity(intent)
        }
        user_profile_edit_lifeexpectancy_textView.setOnClickListener {
            val intent = Intent(this,UserProfileEditSpinnerActivity::class.java)
            intent.putExtra("table","lifeExpectancy")
                .putExtra("edit",get_user.life_expectancy)
            startActivity(intent)
        }

        //アイコン
        user_profile_edit_imageview.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    /*
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
            user_registar_select_photo.setImageBitmap(bitmap)
            user_registar_select_photo_button.alpha = 0f
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
                    saveUserToFirebaseDatabase(it.toString())
                    Log.d(USER_REGISTAR, "File 場所$it")
                }
            }
            .addOnFailureListener{
                //do same logging here
                Log.d(USER_REGISTAR, "作成に失敗しました ${it.message}")
                Toast.makeText(this, "作成に失敗しました ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
    */
}