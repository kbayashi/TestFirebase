package com.example.testfirebase

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.frind_add_dialog.*
import kotlinx.android.synthetic.main.select_dialog.*

class friendAddDialog :DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //ダイアログのインスタンス取得
        val dialog: Dialog = Dialog(context!!)
        var test = mutableListOf<String>()
        var test2 = arrayListOf<String>("大腸癌")
        //タイトルバーなしのダイアログを表示
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        //レイアウト指定
        dialog.setContentView(R.layout.frind_add_dialog)
        dialog.window?.apply {
            //フルスクリーンでダイアログを表示
            setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)

            //背景色を透明
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        //検索
        dialog.friend_add_dialog_search_button.setOnClickListener {
            Log.d("配列", "$test")
            FirebaseFirestore.getInstance().collection("user")
                .get().addOnSuccessListener {
                    it.forEach {
                        var user = it.toObject(User::class.java)
                        Log.d("検索結果", user.name)
                    }
                }.addOnFailureListener{
                    Log.d("test", it.message)
                }
            dialog.cancel()
        }

        //キャンセル
        dialog.friend_add_dialog_cancel_button.setOnClickListener {
            dialog.cancel()
        }

        //病名選択
        dialog.friend_add_dialog_sick_content_textView.setOnClickListener {
            val selectDialog = selectDialogMultiple("病名", dialog.friend_add_dialog_sick_content_textView ,"sick", test)
            selectDialog.show(childFragmentManager, "")
        }

        //年齢選択
        dialog.friend_add_dialog_age_content_textView.setOnClickListener {
//            val selectDialog = selectDialogMultiple("年齢", dialog.friend_add_dialog_age_content_textView ,"age")
//            selectDialog.show(childFragmentManager, "")
        }

        //住所検索
        dialog.friend_add_dialog_live_content_textView.setOnClickListener {
//            val selectDialog = selectDialogMultiple("都道府県",  dialog.friend_add_dialog_live_content_textView,"live")
//            selectDialog.show(childFragmentManager, "")
        }

        //趣味選択
        dialog.friend_add_dialog_hobby_content_textView.setOnClickListener {
//            val selectDialog = selectDialogMultiple("趣味",  friend_add_dialog_hobby_content_textView,"hoby")
//            selectDialog.show(childFragmentManager, "")
        }


        return dialog
    }
}