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
import kotlinx.android.synthetic.main.select_dialog.*

class selectDialogMultiple(val title:String ,val textView: TextView, val dbRefName: String, val mutableList: MutableList<String>):DialogFragment() {

    val SELECT_DIALOG_MULTIPLE = "SELECT_DIALOG_MULTIPLE"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //ダイアログのインスタンス取得
        val dialog: Dialog = Dialog(context!!)
        //タイトルバーなしのダイアログを表示
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        //レイアウト指定
        dialog.setContentView(R.layout.select_dialog)
        dialog.window?.apply {
            //フルスクリーンでダイアログを表示
            setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)

            //背景色を透明
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        dialog.select_dialog_title_textView.text = title
        var adapter = selectDialogMultipleAdapter(context!!)
        AdapterSet(adapter)

        //キャンセル
        dialog.select_dialog_cansel_button.setOnClickListener {
            dialog.cancel()
        }

        //選択完了
        dialog.select_dialog_select_button.setOnClickListener {
            adapter.setTextView(textView, mutableList)
            dialog.cancel()
        }

        return dialog
    }

    //アダプターにデータをセット
    private fun AdapterSet(adapter: selectDialogMultipleAdapter){
        val ref = FirebaseFirestore.getInstance().collection(dbRefName)
        ref.get().addOnSuccessListener {
            Log.d(SELECT_DIALOG_MULTIPLE, "$it" )
            it.forEach{
                Log.d(SELECT_DIALOG_MULTIPLE,it["title"].toString())
                adapter.add(it["title"].toString())
            }
            dialog?.select_dialog_recyclerView?.adapter = adapter
        }.addOnFailureListener {
            Log.d("取得失敗", it.message)
        }
    }


}