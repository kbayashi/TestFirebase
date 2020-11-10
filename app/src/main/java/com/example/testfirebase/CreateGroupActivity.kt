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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_create_group.*
import java.util.*
import kotlin.collections.ArrayList

class CreateGroupActivity : AppCompatActivity() {

    // 画像選択
    private var selectedPhotoUri: Uri? = null
    // グループアイコン保存パス
    private var gIcon = "none"

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

        // Firebase
        val auth = FirebaseAuth.getInstance()
        val me = auth.currentUser
        val db = FirebaseFirestore.getInstance()

        // ユーザオブジェクト
        var loginUser: User?
        var getUser: User?

        // アダプタ
        var createUserListAdapter = createGroupAdapter(this)

        // ユーザ選択配列 and ユーザID配列
        val user_select_array: ArrayList<Boolean> = ArrayList()
        val user_id_array: ArrayList<String> = ArrayList()

        // アクションバー表記変更
        supportActionBar?.title = "グループを作成"

        // DBから取得してきたデータをアダプタに格納
        val loginUserRef = db.collection("user").document(me!!.uid)
        loginUserRef.get().addOnSuccessListener {
            loginUser = it.toObject(User::class.java)
            Log.d("LOGIN_USER", "${it.data}")
            Log.d("LOGIN_USER", "${loginUser?.name}")

            val users = db.collection("user")
            users.get().addOnSuccessListener {
                it.forEach {
                    // 自分のユーザオブジェクトを取得
                    getUser = it.toObject(User::class.java)
                    Log.d("GET_USER","${it.toObject(User::class.java)}")
                    Log.d("GET_USER","${getUser!!.name}")

                    if(!(loginUser?.name == getUser!!.name)) {
                        createUserListAdapter?.add(getUser!!)
                        Log.d("ADD_USER", "${getUser}")
                        user_select_array.add(false)
                        user_id_array.add(getUser!!.uid)
                    }
                }
                // アダプタに関連付け
                recy.adapter = createUserListAdapter
                // ボタンを有効化
                subb.setEnabled(true)

            }.addOnFailureListener {
                Log.d("GET_FAILED", it.message)
            }

        }.addOnFailureListener {
            Log.d("GET_FAILED", it.message)
        }

        // ユーザ選択処理
        createUserListAdapter.setOnclickListener { position: Int, bool: Boolean ->
            user_select_array[position] = bool
        }

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

            // グループ名規定の分岐
            if(edit.text.isEmpty()){
                AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage("グループ名を入力してください")
                    .setPositiveButton("OK"){ dialog, which -> }
                    .show()
            }else if (edit.length() > 20){
                AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage("文字数が多いです（"+edit.length()+"/20）")
                    .setPositiveButton("OK"){ dialog, which -> }
                    .show()
            }else{

                // ユーザが一人以上選択されているか判定
                var flag = false
                for (i in 0 .. user_select_array.size-1 ){
                    if (user_select_array[i]) flag = true
                }

                // ユーザ選択数の分岐
                if (flag == true){

                    // Firebaseにグループの型を作成(一意なグループIDも同時に生成)
                    val cGroup = db.collection("group").document()

                    // グループ名
                    val cInfo = hashMapOf(
                        "name" to edit.text.toString(),
                        "icon" to gIcon,
                        "topic" to "グループの活用目的を決めよう"
                    )
                    cGroup.set(cInfo)

                    // メンバー
                    data class cUser(
                        val uid: String? = null
                    )
                    // グループに自分を加入する
                    cGroup.collection("member").add(cUser(me.uid))

                    // グループに自分以外のユーザを加入する
                    for (i in 0 .. user_select_array.size-1){
                        if (user_select_array[i] == true){
                            cGroup.collection("member").add(cUser(user_id_array[i]))
                        }
                    }

                    // 作成ダイアログ
                    // 現状、このダイアログは確認のために表示しています。グループチャット画面完成時に
                    // このダイアログは削除します
                    AlertDialog.Builder(this)
                        .setTitle(R.string.app_name)
                        .setMessage("グループを作成しました！")
                        .setPositiveButton("OK", { dialog, which ->
                            // グループチャット画面へ遷移するが、まだ作成していないのでとりあえず画面削除
                            finish()
                        })
                        .show()

                    // グループチャット画面へ遷移
                    val intent = Intent(this, GroupChatActivity::class.java)
                    startActivity(intent)

                }else{

                    AlertDialog.Builder(this)
                        .setTitle(R.string.app_name)
                        .setMessage("最低1人以上ユーザを選択してください")
                        .setPositiveButton("OK"){ dialog, which -> }
                        .show()
                }

            }
        }
    }

    // ギャラリーから画像を選択するメソッド
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 写真選択アプリが呼び出され、ちゃんと操作して、データが入っていたら
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            // 写真をボタンの背景に設定
            Log.e("GroupIcon","Photo select")
            Log.d("GroupIcon","${data.data}")
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
                    gIcon = it.toString()
                    Log.d(UserProfileEditActivity.USER_REGISTAR, "File 場所$it")
                    // FirebaseFirestore.getInstance().collection("user").document(my_user!!.uid).set(my_user!!)
                }
            }
            .addOnFailureListener{
                //do same logging here
                Log.d(UserProfileEditActivity.USER_REGISTAR, "作成に失敗しました ${it.message}")
                Toast.makeText(this, "作成に失敗しました ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}