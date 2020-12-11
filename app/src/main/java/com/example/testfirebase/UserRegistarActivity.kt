package com.example.testfirebase

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_time_line_add.*
import kotlinx.android.synthetic.main.activity_user_registar.*


class UserRegistarActivity : AppCompatActivity() {


    var ragioGender:RadioButton? = null
    //ダイアログに渡す処理
    val listener:((TextView, String)->Unit)  = { TextView, String->
        TextView.text = String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_registar)

        //登録ボタン
        user_registar_button.setOnClickListener {
            ContentInspection()
        }

        //ラジオボタンタップ
        user_registar_gender_radioGroup.setOnCheckedChangeListener { radioGroup, i ->
                ragioGender = findViewById<RadioButton>(i)
                Log.d("ラジオグループ", ragioGender?.text.toString())
            }

        //病名ダイアログ表示
        user_registar_sick_textView.setOnClickListener {
            val dialog = selectDialogRadio("病名",user_registar_sick_textView, "sick")
            dialog.show(supportFragmentManager, "病名")
        }
        //カメラ起動
        user_registar_camera_setting_textView.setOnClickListener {
            // カメラ機能を実装したアプリが存在するかチェック
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(packageManager)?.let {
                if (checkCameraPermission()) {
                    takePicture()
                } else {
                    grantCameraPermission()
                }
            } ?: Toast.makeText(this, "カメラを扱うアプリがありません", Toast.LENGTH_LONG).show()
        }
    }

    //入力検査(まだ適当)と認証
    private fun ContentInspection(){

        val name = user_registar_name_editText.text.toString()
        val email = user_registar_email_editText.text.toString()
        val pass = user_registar_pass_editText.text.toString()
        val checkPass = user_registar_pass_check_editText.text.toString()

        if(email.isEmpty() || pass.isEmpty() || name.isEmpty() || pass != checkPass || ragioGender == null ) {
            Toast.makeText(this, "入力内容が不足しています", Toast.LENGTH_SHORT).show()
            return
        }


        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener
                //認証
                Log.d("認証", "認証成功 ${it.result?.user?.uid}")
                val user = User(it.result?.user?.uid!!, name, "https://firebasestorage.googleapis.com/v0/b/firevasetest-1d5b9.appspot.com/o/user_icon%2Fnoimage.png?alt=media&token=b9ae62b8-8c42-4791-9507-c84c93f6871f", ragioGender?.text.toString(),
                    "非公開", "非公開", "非公開","非公開", "非公開", "非公開")
                UserSaveDB(user)
            }
            .addOnFailureListener {
                Log.d("Main", "作成に失敗しました ${it.message}")
                Toast.makeText(this, "作成に失敗しました ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }


    //ユーザのデータをデータベースに登録
    private fun UserSaveDB(user:User){
        val ref  = FirebaseFirestore.getInstance().collection("user")
            .document(user.uid)

        ref.set(user)
            .addOnSuccessListener {
                Log.d("データベース", "データベースに保存成功 ")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("データベース", "データベースに保存失敗 ${it.message}")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 2 && data?.extras != null){
            Log.d("カメラ", "$data")
            Log.d("カメラ2", "$resultCode")
            val bitmap = data!!.getExtras()!!.get("data") as Bitmap
            user_registar_camera_imageView.visibility = View.VISIBLE
            user_registar_camera_imageView.setImageBitmap(bitmap)
            user_registar_camera_setting_textView.text = "取り直しをするならここをタップ"

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
