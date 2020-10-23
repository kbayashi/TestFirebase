package com.example.testfirebase

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_create_group.*
import java.util.*

class CreateGroupActivity : AppCompatActivity() {

    // 画像アップロード
    private var selectedPhotoUri: Uri? = null

    // 画面生成
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        // 各種コンポーネント
        val icon = findViewById<CircleImageView>(R.id.group_icon_imageView)
        val edit = findViewById<EditText>(R.id.group_name_editText)
        val cont = findViewById<TextView>(R.id.group_name_count_textView)
        val recy = findViewById<RecyclerView>(R.id.user_list_recyclerView)
        val subb = findViewById<Button>(R.id.submit_button)

        // アクションバー表記変更
        supportActionBar?.title = "グループを作成"

        // グループアイコン変更の処理
        icon.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        // 文字数関係の処理
        edit!!.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 文字数をカウント
                if (edit.length() > 20) {
                    // リミットになった時は赤色
                    cont.setTextColor(Color.MAGENTA)
                } else {
                    // それ以外は黒色
                    cont.setTextColor(Color.GRAY)
                }
                cont.text = edit.length().toString() + "/20"
            }
            override fun afterTextChanged(p0: Editable?) {}

        })

        // ボタン押下時のアクション
        subb.setOnClickListener {

            if(edit.text.isEmpty()){
                AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage("グループ名を入力してください")
                    .setPositiveButton("OK"){ dialog, which -> }
                    .show()
            }else if (edit.length() <= 20){
                AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage("WELCOME TO UNDERGROUND")
                    .setPositiveButton("OK"){ dialog, which -> }
                    .show()
            }else{
                AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage("文字数が多いです（"+edit.length()+"/20）")
                    .setPositiveButton("OK"){ dialog, which -> }
                    .show()
            }

            // メンバーが誰も指定されていないとき
            // Toast.makeText(applicationContext, "メンバーを最低1人以上は指定してください", Toast.LENGTH_SHORT).show()
        }
    }

    // ギャラリーから画像を選択するメソッド
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //写真選択アプリが呼び出され、ちゃんと操作して、データが入っていたら
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            //写真をボタンの背景に設定
            Log.e("GroupRegistarActivity","Photo select")
            Log.d("GroupRegistarActivity","${data.data}")
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            group_icon_imageView.setImageBitmap(bitmap)
            uploadImageFirebaseStorage()
        }
    }

    // Firebaseに画像をアップロードするメソッド
    private fun uploadImageFirebaseStorage(){

        if(selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/group_icon/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(UserProfileEditActivity.USER_REGISTAR, "アップロード成功${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    // my_user?.img = it.toString()
                    Log.d(UserProfileEditActivity.USER_REGISTAR, "File 場所$it")
                    // FirebaseFirestore.getInstance().collection("user").document(my_user!!.uid).set(my_user!!)
                }
            }
            .addOnFailureListener{
                //do same logging here
                Log.d(UserProfileEditActivity.USER_REGISTAR, "作成に失敗しました ${it.message}")
                Toast.makeText(this, "作成に失敗しました ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}