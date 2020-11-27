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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.select_dialog.*

class UserProfileEditCheckDialog(val title:String,val textView: TextView, val dbRefName: String,val dbEditName: String): DialogFragment() {
    val SELECT_DIALOG = "SELECT_DIALOG"

    // 自分のユーザインスタンスを生成
    private val auth = FirebaseAuth.getInstance()
    private val me = auth.currentUser

    // DB
    val db = FirebaseFirestore.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //ダイアログのインスタンス取得
        val dialog: Dialog = Dialog(context!!)
        //タイトルバーなしのダイアログを表示
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        //レイアウト指定
        dialog.setContentView(R.layout.select_dialog)
        dialog.window?.apply {
            //フルスクリーンでダイアログを表示
            setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)

            //背景色を透明
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        var Radapter = selectDialogRadioAdapter(context!!)
        var Cadapter = selectDialogMultipleAdapter(context!!)
        if(dbRefName == "sick"){
            RAdapterSet(Radapter)
            dialog.select_dialog_recyclerView.adapter = Radapter
            dialog.select_dialog_title_textView.text = title
        }else if(dbRefName == "hoby"){
            CAdapterSet(Cadapter)
            dialog.select_dialog_recyclerView.adapter = Cadapter
            getResources().getStringArray(R.array.hobby).forEach {
                Cadapter.add(it)
            }
            dialog.select_dialog_title_textView.text = title
        }
        //選択ボタン
        dialog.select_dialog_select_button.setOnClickListener {
            if(dbRefName == "sick"){
                //更新可能
                val ref = db.collection("user").document(me!!.uid).update(
                    dbRefName,
                    Radapter.checkedText
                )
                    .addOnSuccessListener {
                        Log.d("DB", "更新できました")
                    }
                    .addOnFailureListener {
                        Log.d("DB", "更新できません ${it.message}")
                    }
                textView.text = Radapter.checkedText
            }else if(dbRefName == "hoby"){
                //更新可能
                val ref = db.collection("user").document(me!!.uid).update(
                    dbRefName,
                    Cadapter.setDB()
                )
                    .addOnSuccessListener {
                        Log.d("DB", "更新できました")
                    }
                    .addOnFailureListener {
                        Log.d("DB", "更新できません ${it.message}")
                    }
                textView.text = Cadapter.setDB()
            }
            dialog.cancel()
        }
        //キャンセルにデータをセット
        dialog.select_dialog_cansel_button.setOnClickListener {
            dialog.cancel()
        }
        return dialog
    }

    //アダプターにデータをセット
    private fun RAdapterSet(adapter: selectDialogRadioAdapter){
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

    //アダプターにデータをセット
    private fun CAdapterSet(adapter: selectDialogMultipleAdapter){
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