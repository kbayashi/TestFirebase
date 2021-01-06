package com.example.testfirebase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testfirebase.UserListFragment.Companion.SELECT_USER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_chat.*
import messageAdapter
import java.util.*

class ChatActivity : AppCompatActivity() {

    // Firebase
    private val auth = FirebaseAuth.getInstance()
    private val me = auth.currentUser
    private val db = FirebaseFirestore.getInstance()
    // 画像選択
    private var selectedPhotoUri: Uri? = null
    // ユーザオブジェクト
    private var get_you: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // 相手のオブジェクト情報(相手のUIDを)
        val intent = getIntent()
        get_you = intent.getParcelableExtra(SELECT_USER)

        // アクションバーに相手の名前を表記
        title = get_you!!.name

        // アダプターの設定
        var messageListAdapter = messageAdapter(this)

        //メッセージの監視
        val docRef = db.collection("user-message").document(me!!.uid).collection(get_you!!.uid).orderBy("time")

        //ブロックされているならメッセージを表示しない
        db.collection("block-user").document("get")
            .collection(me.uid).document(get_you!!.uid).get().addOnSuccessListener {
                if(it["uid"] != get_you!!.uid){
                    docRef.addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            //空ではない = 何らか文字が入力されているとき
                            Log.w("TAG", "Listen failed.", e)
                            return@addSnapshotListener
                        }

                        //アダプターに関連付け
                        chat_recyclerView.adapter = messageListAdapter

                        //DBに格納されているデータを取り出す
                        snapshot?.documentChanges?.forEach {
                            var messagedata = it.document.toObject(Message::class.java)
                            Log.d("documentChange", messagedata.message)

                            //サイクルビューに自分のメッセージ内容を追加する
                            messageListAdapter?.add(messagedata)

                            //Firebaseに更新があった時は一番下にスクロールする
                            chat_recyclerView.scrollToPosition(messageListAdapter.itemCount -1)
                        }
                    }
                }else{
                    message_editText.setText("ブロック中")
                    message_editText.isEnabled = false
                    send_button.isEnabled = false
                }
            }

        //メッセージ送信(MessageDBに登録)
        send_button.setOnClickListener {
            if(message_editText.text.isBlank()){
                Toast.makeText(applicationContext, "メッセージを入力してください", Toast.LENGTH_SHORT).show()
            }else{
                sendMessage(get_you!!, message_editText.text.toString(),false)
            }
        }

        // 画像送信
        image_send_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    // Firebaseの「user-message」と「user-latest」コレクションにオブジェクトを登録する関数
    // 引数: you(チャット相手のユーザオブジェクト) / msg(送信するメッセージ内容)
    // 戻値: なし
    private fun sendMessage(you:User, msg:String, img_flag: Boolean){

        //送信時間を確定する
        val millis = System.currentTimeMillis()
        //送信内容をクラスに送る
        val message = Message(msg, me!!.uid, you.uid, millis,img_flag,false)

        //DBにメッセージを登録(自分Ver)
        db.collection("user-message").document(me!!.uid).collection(you.uid).add(message)
        db.collection("user-message").document(you.uid).collection(me!!.uid).add(message)

        //最新トーク一覧のデータ更新
        if (img_flag == true) {
            // 画像の場合
            val message_img = Message("画像を送信しました", me!!.uid, you.uid, millis, img_flag, false)
            db.collection("user-latest").document("les").collection(me!!.uid).document(you.uid).set(message_img)
            db.collection("user-latest").document("les").collection(you.uid).document(me!!.uid).set(message_img)
        } else {
            // メッセージの場合
            db.collection("user-latest").document("les").collection(me!!.uid).document(you.uid).set(message)
            db.collection("user-latest").document("les").collection(you.uid).document(me!!.uid).set(message)
        }

        //テキストボックスの内容をすべて削除
        message_editText.text = null
    }

    // ギャラリーから画像を選択するメソッド
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 写真選択アプリが呼び出され、ちゃんと操作して、データが入っていたら
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            // 写真をボタンの背景に設定
            // Log.e("GroupIcon","Photo select")
            // Log.d("GroupIcon","${data.data}")
            selectedPhotoUri = data.data
            uploadImageFirebaseStorage()
        }
    }

    // Firebaseに画像をアップロードするメソッド
    private fun uploadImageFirebaseStorage() {

        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/chat_img/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(UserProfileEditActivity.USER_REGISTAR, "アップロード成功${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    sendMessage(get_you!!, it.toString(), true)
                    Log.d(UserProfileEditActivity.USER_REGISTAR, "File 場所$it")
                    // FirebaseFirestore.getInstance().collection("user").document(my_user!!.uid).set(my_user!!)
                }
            }
            .addOnFailureListener {
                //do same logging here
                Log.d(UserProfileEditActivity.USER_REGISTAR, "作成に失敗しました ${it.message}")
                Toast.makeText(this, "作成に失敗しました ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}