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
import kotlinx.android.synthetic.main.select_dialog_radio.*

class selectDialogRadio(val title:String): DialogFragment() {

    val SELECT_DIALOG = "SELECT_DIALOG"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //ダイアログのインスタンス取得
        val dialog: Dialog = Dialog(context!!)
        //タイトルバーなしのダイアログを表示
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        //レイアウト指定
        dialog.setContentView(R.layout.select_dialog_radio)
        dialog.window?.apply {
            //フルスクリーンでダイアログを表示
            setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)

            //背景色を透明
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        dialog.select_dialog_title_textView.text = title
        var adapter = selectDialogAdapter(context!!)
        AdapterSet(adapter)



        return dialog
    }

    private fun AdapterSet(adapter: selectDialogAdapter){
        val ref = FirebaseFirestore.getInstance().collection("sick")
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