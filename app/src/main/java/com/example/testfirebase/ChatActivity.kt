package com.example.testfirebase

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testfirebase.UserListFragment.Companion.SELECT_USER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_chat.*
import messageAdapter

private lateinit var auth: FirebaseAuth

class ChatActivity : AppCompatActivity() {

    //自分のユーザ情報を取得
    val auth = FirebaseAuth.getInstance()
    val me = auth.currentUser
    //データベースオブジェクト
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //相手のオブジェクト情報(相手のUIDを)
        val intent = getIntent()
        val get_you = intent.getParcelableExtra<User>(SELECT_USER)
        //アクションバーに相手の名前を表記
        supportActionBar?.title = get_you.name
        //アダプターの設定
        var messageListAdapter = messageAdapter(this)

        //メッセージの監視
        val docRef = db.collection("user-message").document(me!!.uid).collection(get_you.uid).orderBy("time")
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

        //メッセージ送信(MessageDBに登録)
        send_button.setOnClickListener {
            if(message_editText.text.isEmpty()){
                Toast.makeText(applicationContext, "メッセージを入力してください", Toast.LENGTH_SHORT).show()
            }else{
                sendMessage(get_you,message_editText.text.toString())
            }
        }
    }

    // Firebaseの「user-message」と「user-latest」コレクションにオブジェクトを登録する関数
    // 引数: you(チャット相手のユーザオブジェクト) / msg(送信するメッセージ内容)
    // 戻値: なし
    private fun sendMessage(you:User,msg:String){

        //送信時間を確定する
        val millis = System.currentTimeMillis()
        //送信内容をクラスに送る
        val message = Message(msg,me!!.uid,you.uid,millis)

        //データベースにメッセージを登録(自分Ver)
        val ref1 = db.collection("user-message").document(me!!.uid).collection(you.uid).add(message)
            .addOnSuccessListener{
                Log.d("DB", "データベースにメッセージを登録しました")
            }
            .addOnFailureListener{
                Log.d("DB", "データベースにメッセージの登録が失敗しました ${it.message}")
            }
        //データベースにメッセージを登録(相手Ver)
        val ref2 = db.collection("user-message").document(you.uid).collection(me!!.uid).add(message)
            .addOnSuccessListener{
                Log.d("DB", "データベースにメッセージを登録しました")
            }
            .addOnFailureListener{
                Log.d("DB", "データベースにメッセージの登録が失敗しました ${it.message}")
            }

        //最新トーク一覧のデータ
        val ref3 = db.collection("user-latest").document("les").collection(me!!.uid).document(you.uid).set(message)
            .addOnSuccessListener{
                Log.d("DB", "データベースにメッセージを登録しました")
            }
            .addOnFailureListener{
                Log.d("DB", "データベースにメッセージの登録が失敗しました ${it.message}")
            }
        val ref4 = db.collection("user-latest").document("les").collection(you.uid).document(me!!.uid).set(message)
            .addOnSuccessListener{
                Log.d("DB", "データベースにメッセージを登録しました")
            }
            .addOnFailureListener{
                Log.d("DB", "データベースにメッセージの登録が失敗しました ${it.message}")
            }

        //テキストボックスの内容をすべて削除
        message_editText.text = null
    }
}