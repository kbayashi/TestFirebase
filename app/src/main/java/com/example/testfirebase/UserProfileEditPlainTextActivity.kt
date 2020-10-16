package com.example.testfirebase

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_profile_edit_plain_text.*


class UserProfileEditPlainTextActivity : AppCompatActivity() {

    //自分のユーザインスタンスを生成
    private val auth = FirebaseAuth.getInstance()
    private val me = auth.currentUser
    //DB
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_edit_plain_text)

        val table = intent.getStringExtra("table")
        val edit = intent.getStringExtra("edit")
        val text = activity_user_profile_edit_plain_text_plainText.length()
        val editText = EditText(this)

        if(table == "name"){
            // タイトル
            setTitle("名前を変更")
            val limit = 20
            editText.inputType = InputType.TYPE_CLASS_TEXT
            editText.filters = arrayOf<InputFilter>(LengthFilter(limit))
            activity_user_profile_edit_plain_text_limitTextView.text = text.toString()+"/"+limit.toString()+"までです"
            activity_user_profile_edit_plain_text_plainText.setText(edit)
        }else{
            // タイトル
            setTitle("自己紹介文を変更")
            val limit = 300
            editText.inputType = InputType.TYPE_CLASS_TEXT
            editText.filters = arrayOf<InputFilter>(LengthFilter(limit))
            activity_user_profile_edit_plain_text_limitTextView.text = limit.toString()+"までです"
            activity_user_profile_edit_plain_text_plainText.setText(edit)
        }

        //保存ボタン
        activity_user_profile_edit_plain_text_savebutton.setOnClickListener{

            //文字列が格納されているか判定
            if (activity_user_profile_edit_plain_text_plainText.text.length > 0){

                if(table == "name") {
                    //更新可能
                    val ref = db.collection("user").document(me!!.uid).update(
                        table,
                        activity_user_profile_edit_plain_text_plainText.text.toString()
                    )
                        .addOnSuccessListener {
                            Log.d("DB", "更新できました")
                        }
                        .addOnFailureListener {
                            Log.d("DB", "更新できません ${it.message}")
                        }
                }else{
                    //更新可能
                    val ref = db.collection("user").document(me!!.uid).update(
                        table,
                        activity_user_profile_edit_plain_text_plainText.text.toString()
                    )
                        .addOnSuccessListener {
                            Log.d("DB", "更新できました")
                        }
                        .addOnFailureListener {
                            Log.d("DB", "更新できません ${it.message}")
                        }
                }
            }else{
                if(table == "name") {
                    //非公開
                    val ref = db.collection("user").document(me!!.uid).update(table, "非公開")
                        .addOnSuccessListener {
                            Log.d("DB", "更新できました")
                        }
                        .addOnFailureListener {
                            Log.d("DB", "更新できません ${it.message}")
                        }
                }else{
                    //非公開
                    val ref = db.collection("user").document(me!!.uid).update(table, "非公開")
                        .addOnSuccessListener {
                            Log.d("DB", "更新できました")
                        }
                        .addOnFailureListener {
                            Log.d("DB", "更新できません ${it.message}")
                        }
                }
            }

            //自アプリに戻る
            val upIntent = NavUtils.getParentActivityIntent(this)
            if (upIntent != null) {
                NavUtils.navigateUpTo(this, upIntent)
            }
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

}