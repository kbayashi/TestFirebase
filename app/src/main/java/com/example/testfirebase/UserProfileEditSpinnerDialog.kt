package com.example.testfirebase

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_profile_edit_spinner.*

class UserProfileEditSpinnerDialog(val title:String, val textView: TextView, val dbRefName: String, var dbEditName: String): DialogFragment() {
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
        dialog.setContentView(R.layout.activity_user_profile_edit_spinner)
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
        // タイトル
        dialog.activity_user_profile_edit_radio_button_title_text.text = title
        if (dbRefName == "birthday") {
            val adapter = ArrayAdapter.createFromResource(
                context!!,
                R.array.age,
                android.R.layout.simple_spinner_item
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialog.activity_user_profile_edit_spinner_spinner.adapter = adapter
        } else if (dbRefName == "live") {
            val adapter = ArrayAdapter.createFromResource(
                context!!,
                R.array.live,
                android.R.layout.simple_spinner_item
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialog.activity_user_profile_edit_spinner_spinner.adapter = adapter
        }else if(dbRefName == "hobby"){
            val adapter = ArrayAdapter.createFromResource(context!!,R.array.hobby,android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialog.activity_user_profile_edit_spinner_spinner.adapter = adapter
        }else if(dbRefName == "sick"){
            val adapter = ArrayAdapter.createFromResource(context!!,R.array.sick,android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialog.activity_user_profile_edit_spinner_spinner.adapter = adapter
        }else{
            val adapter = ArrayAdapter.createFromResource(context!!,R.array.life_expectancy,android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialog.activity_user_profile_edit_spinner_spinner.adapter = adapter
        }
        dialog.activity_user_profile_edit_spinner_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long

            ) {
                val spinnerParent = parent as Spinner
                val item = spinnerParent.selectedItem as String
                dbEditName = item
            }

            //アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        //保存ボタン
        dialog.activity_user_profile_edit_spinner_savebutton.setOnClickListener {

            if(dbRefName == "old"){
                //更新可能
                val ref = db.collection("user").document(me!!.uid).update(
                    dbRefName,
                    dialog.activity_user_profile_edit_spinner_spinner.selectedItem.toString()
                )
                    .addOnSuccessListener {
                        Log.d("DB", "更新できました")
                    }
                    .addOnFailureListener {
                        Log.d("DB", "更新できません ${it.message}")
                    }
            }else if(dbRefName == "live"){
                //更新可能
                val ref = db.collection("user").document(me!!.uid).update(
                    dbRefName,
                    dialog.activity_user_profile_edit_spinner_spinner.selectedItem.toString()
                )
                    .addOnSuccessListener {
                        Log.d("DB", "更新できました")
                    }
                    .addOnFailureListener {
                        Log.d("DB", "更新できません ${it.message}")
                    }
            }else if(dbRefName == "hobby") {
                //更新可能
                val ref = db.collection("user").document(me!!.uid).update(
                    dbRefName,
                    dialog.activity_user_profile_edit_spinner_spinner.selectedItem.toString()
                )
                    .addOnSuccessListener {
                        Log.d("DB", "更新できました")
                    }
                    .addOnFailureListener {
                        Log.d("DB", "更新できません ${it.message}")
                    }
            }else if(dbRefName == "sick"){
                //更新可能
                val ref = db.collection("user").document(me!!.uid).update(
                    dbRefName,
                    dialog.activity_user_profile_edit_spinner_spinner.selectedItem.toString()
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
                    dbRefName,
                    dialog.activity_user_profile_edit_spinner_spinner.selectedItem.toString()
                )
                    .addOnSuccessListener {
                        Log.d("DB", "更新できました")
                    }
                    .addOnFailureListener {
                        Log.d("DB", "更新できません ${it.message}")
                    }
            }
            textView.text = dialog.activity_user_profile_edit_spinner_spinner.selectedItem.toString()
            dialog.cancel()
        }
        //キャンセルボタン
        dialog.activity_user_profile_edit_spinner_cansel_button.setOnClickListener {
            dialog.cancel()
        }
        return dialog
    }
}