package com.example.testfirebase

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NavUtils
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_profile_edit_plain_text.*
import kotlinx.android.synthetic.main.activity_user_profile_edit_radio_button.*

class UserProfileEditRadioButtonDialog(val title:String, val textView: TextView, val dbRefName: String, val dbEditName: String): DialogFragment() {
    //自分のユーザインスタンスを生成
    private val auth = FirebaseAuth.getInstance()
    private val me = auth.currentUser
    //DB
    val db = FirebaseFirestore.getInstance()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //ダイアログのインスタンス取得
        val dialog: Dialog = Dialog(context!!)
        //タイトルバーなしのダイアログを表示
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        //レイアウト指定
        dialog.setContentView(R.layout.activity_user_profile_edit_radio_button)
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

        dialog.activity_user_profile_edit_radio_button_title_text.text = title

        if(dbEditName == "男性"){
            dialog.radioGroup.check(R.id.activity_user_profile_edit_radio_button_man_radioButton)
        }else{
            dialog.radioGroup.check(R.id.activity_user_profile_edit_radio_button_woman_radioButton)
        }

        dialog.activity_user_profile_edit_radio_button_savebutton.setOnClickListener{
            if(dialog.activity_user_profile_edit_radio_button_man_radioButton.isChecked == true){
                //更新可能
                val ref = db.collection("user").document(me!!.uid).update(dbRefName, dialog.activity_user_profile_edit_radio_button_man_radioButton.text.toString())
                    .addOnSuccessListener {
                        Log.d("DB", "更新できました")
                    }
                    .addOnFailureListener {
                        Log.d("DB", "更新できません ${it.message}")
                    }
                textView.text = dialog.activity_user_profile_edit_radio_button_man_radioButton.text
                dialog.cancel()
            }else if(dialog.activity_user_profile_edit_radio_button_woman_radioButton.isChecked == true){
                //更新可能
                val ref = db.collection("user").document(me!!.uid).update(dbRefName, dialog.activity_user_profile_edit_radio_button_woman_radioButton.text.toString())
                    .addOnSuccessListener {
                        Log.d("DB", "更新できました")
                    }
                    .addOnFailureListener {
                        Log.d("DB", "更新できません ${it.message}")
                    }
                textView.text = dialog.activity_user_profile_edit_radio_button_woman_radioButton.text
                dialog.cancel()
            }else{
                //Toast.makeText(, "性別を選択してください", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.activity_user_profile_edit_radio_button_cansel_button.setOnClickListener {
            dialog.cancel()
        }
        return dialog
    }
}