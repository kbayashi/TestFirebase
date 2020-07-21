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
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.select_dialog_radio.*
import kotlinx.android.synthetic.main.user_profile_plain_text_edit_dialog.*

class UserProfilePlainTextEditDialog(val title:String,val textView: TextView, val dbRefName: String) : DialogFragment(){

    val SELECT_DIALOG = "SELECT_DIALOG"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //ダイアログのインスタンス取得
        val dialog = Dialog(context!!)
        //ダイアログ表示
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        //レイアウト指定
        dialog.setContentView(R.layout.user_profile_plain_text_edit_dialog)
        dialog.window?.apply {
            //フルスクリーンでダイアログ表示
            setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
            //背景色を透明
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        var adapter = selectDialogAdapter(context!!)
        AdapterSet(adapter)

        dialog.activity_user_profile_edit_title_textView.text = title

        //保存ボタン
        activity_user_profile_edit_save_button.setOnClickListener{
            textView.text = adapter.checkedText
            dialog.cancel()
        }
        //キャンセルボタン
        activity_user_profile_edit_cancel_button.setOnClickListener{
            dialog.cancel()
        }
        return dialog
    }
    //アダプターにデータをセット
    private fun AdapterSet(adapter: selectDialogAdapter){
        val ref = FirebaseFirestore.getInstance().collection(dbRefName)
        ref.get().addOnSuccessListener {
            Log.d(SELECT_DIALOG, "$it" )
            it.forEach{
                Log.d(SELECT_DIALOG,it["title"].toString())
                adapter.add(it["title"].toString())
            }
            dialog?.select_dialog_recyclerView?.adapter = adapter
        }.addOnFailureListener {
            Log.d("取得失敗", it.message)
        }
    }

}