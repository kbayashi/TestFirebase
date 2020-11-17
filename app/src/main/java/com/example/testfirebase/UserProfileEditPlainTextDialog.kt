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
import kotlinx.android.synthetic.main.activity_user_profile_edit_plain_text.activity_user_profile_edit_plain_text_savebutton
import kotlinx.android.synthetic.main.activity_user_profile_edit_plain_text.activity_user_profile_edit_plain_text_title_text
import kotlinx.android.synthetic.main.select_dialog.*

class UserProfileEditPlainTextDialog(val title:String,val textView: TextView, val dbRefName: String, val dbEditName: String): DialogFragment() {

    // 自分のユーザインスタンスを生成
    private val auth = FirebaseAuth.getInstance()
    private val me = auth.currentUser

    // DB
    val db = FirebaseFirestore.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d(dbRefName,"table")
        Log.d(dbEditName,"edit")
        //ダイアログのインスタンス取得
        val dialog: Dialog = Dialog(context!!)
        //タイトルバーなしのダイアログを表示
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        //レイアウト指定
        dialog.setContentView(R.layout.activity_user_profile_edit_plain_text)
        dialog.window?.apply {
            //フルスクリーンでダイアログを表示
            setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            )

            //背景色を透明
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        // 限界文字数
        var limit: Int? = null

        // テーブル判定
        if (dbRefName == "name") {
            // タイトル
            dialog.activity_user_profile_edit_plain_text_title_text.text = title
            limit = 20
        }else{
            // タイトル
            dialog.activity_user_profile_edit_plain_text_title_text.text = title
            limit = 300
        }
        dialog.activity_user_profile_edit_plain_text_plainText.inputType = InputType.TYPE_CLASS_TEXT
        dialog.activity_user_profile_edit_plain_text_plainText.filters =
            arrayOf<InputFilter>(LengthFilter(limit))

        // エディットテキストに先ほど取得した文字列を格納
        dialog.activity_user_profile_edit_plain_text_plainText.setText(dbEditName)
        dialog.activity_user_profile_edit_plain_text_limitTextView.text =
            dialog.activity_user_profile_edit_plain_text_plainText.length()
                .toString() + "/" + limit.toString()

        dialog.activity_user_profile_edit_plain_text_plainText!!.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 今回は実装しない
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 文字数をカウント
                if (dialog.activity_user_profile_edit_plain_text_plainText.length() == limit) {
                    // リミットになった時は赤色
                    dialog.activity_user_profile_edit_plain_text_limitTextView.setTextColor(Color.RED)
                } else {
                    // それ以外は黒色
                    dialog.activity_user_profile_edit_plain_text_limitTextView.setTextColor(Color.GRAY)
                }
                dialog.activity_user_profile_edit_plain_text_limitTextView.text =
                    dialog.activity_user_profile_edit_plain_text_plainText.length()
                        .toString() + "/" + limit.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
                // 今回は実装しない
            }
        })
        //保存ボタン
        dialog.activity_user_profile_edit_plain_text_savebutton.setOnClickListener {

            //文字列が格納されているか判定
            if (dialog.activity_user_profile_edit_plain_text_plainText.text.length > 0) {

                if (dbRefName == "name") {
                    //更新可能
                    val ref = db.collection("user").document(me!!.uid).update(
                        dbRefName,
                        dialog.activity_user_profile_edit_plain_text_plainText.text.toString()
                    )
                        .addOnSuccessListener {
                            Log.d("DB", "更新できました")
                        }
                        .addOnFailureListener {
                            Log.d("DB", "更新できません ${it.message}")
                        }
                } else {
                    //更新可能
                    val ref = db.collection("user").document(me!!.uid).update(
                        dbRefName,
                        dialog.activity_user_profile_edit_plain_text_plainText.text.toString()
                    )
                        .addOnSuccessListener {
                            Log.d("DB", "更新できました")
                        }
                        .addOnFailureListener {
                            Log.d("DB", "更新できません ${it.message}")
                        }
                }
            } else {
                if (dbRefName == "name") {
                    //非公開
                    val ref = db.collection("user").document(me!!.uid).update(dbRefName, "非公開")
                        .addOnSuccessListener {
                            Log.d("DB", "更新できました")
                        }
                        .addOnFailureListener {
                            Log.d("DB", "更新できません ${it.message}")
                        }
                } else {
                    //非公開
                    val ref = db.collection("user").document(me!!.uid).update(dbRefName, "非公開")
                        .addOnSuccessListener {
                            Log.d("DB", "更新できました")
                        }
                        .addOnFailureListener {
                            Log.d("DB", "更新できません ${it.message}")
                        }
                }
            }
            textView.text = dialog.activity_user_profile_edit_plain_text_plainText.text.toString()
            dialog.cancel()
        }
        //保存ボタン
        dialog.activity_user_profile_edit_plain_text_cansel_button.setOnClickListener {
            dialog.cancel()
        }
        return dialog
    }
}