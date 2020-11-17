package com.example.testfirebase

import android.app.AppComponentFactory
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_profile_edit_plain_text.*

class UserProfileEditPlainTextActivity:AppCompatActivity(){

    // 自分のユーザインスタンスを生成
    private val auth = FirebaseAuth.getInstance()
    private val me = auth.currentUser
    // DB
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_edit_plain_text)
        // 前の画面から何のデータを取得してきたか、種類を取得する( name or pr )
        val table = intent.getStringExtra("table")
        // 前の画面から name か pr を取得する
        val edit = intent.getStringExtra("edit")
        // 限界文字数
        var limit:Int? = null

        // テーブル判定
        if(table == "name"){
            // タイトル
            activity_user_profile_edit_plain_text_title_text.text = "名前を変更"
            limit = 20
        }else{
            // タイトル
            activity_user_profile_edit_plain_text_title_text.text = "自己紹介を変更"
            limit = 300
        }

        // 各種プロパティ
        val textCountView = findViewById<TextView>(R.id.activity_user_profile_edit_plain_text_limitTextView)
        val textEdit = findViewById<EditText>(R.id.activity_user_profile_edit_plain_text_plainText)
        val subBtn = findViewById<Button>(R.id.activity_user_profile_edit_plain_text_savebutton)

        activity_user_profile_edit_plain_text_plainText.inputType = InputType.TYPE_CLASS_TEXT
        activity_user_profile_edit_plain_text_plainText.filters = arrayOf<InputFilter>(LengthFilter(limit))

        // エディットテキストに先ほど取得した文字列を格納
        activity_user_profile_edit_plain_text_plainText.setText(edit)
        activity_user_profile_edit_plain_text_limitTextView.text = activity_user_profile_edit_plain_text_plainText.length().toString() + "/" + limit.toString()

        activity_user_profile_edit_plain_text_plainText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 今回は実装しない
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 文字数をカウント
                if (activity_user_profile_edit_plain_text_plainText.length() == limit){
                    // リミットになった時は赤色
                    activity_user_profile_edit_plain_text_limitTextView.setTextColor(Color.RED)
                }else{
                    // それ以外は黒色
                    activity_user_profile_edit_plain_text_limitTextView.setTextColor(Color.GRAY)
                }
                activity_user_profile_edit_plain_text_limitTextView.text = activity_user_profile_edit_plain_text_plainText.length().toString() + "/" + limit.toString()
            }
            override fun afterTextChanged(p0: Editable?) {
                // 今回は実装しない
            }
        })

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