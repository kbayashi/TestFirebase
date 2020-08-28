package com.example.testfirebase

import android.app.Activity
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

class friendAddDialog :DialogFragment(){

    var test:Activity? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //ダイアログのインスタンス取得
        val dialog: Dialog = Dialog(activity!!)
        val getUser = mutableListOf<User>()
        var selectedList = mutableMapOf<String, MutableList<String>>()
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
            Log.d("配列", "$selectedList")
            selectedList.forEach { t, u ->
                u.forEach{
                    Log.d("foreEach", it.toString())
                    FirebaseFirestore.getInstance().collection("user")
                        .whereEqualTo(t.toString(), it).get().addOnSuccessListener {
                            it.forEach {
                                getUser.add(it.toObject(User::class.java))
                                Log.d("検索結果", it["name"].toString())
                            }
                            Log.d("getUser", getUser.toString())
                            callActivity(getUser)
                        }.addOnFailureListener {
                            Log.d("test", it.message)
                        }
                }

            }
            dialog.cancel()
        }
        //キャンセル
        dialog.friend_add_dialog_cancel_button.setOnClickListener {
            dialog.cancel()
        }

        //病名選択
        dialog.friend_add_dialog_sick_content_textView.setOnClickListener {
            val selectDialog = selectDialogMultiple("病名",
                dialog.friend_add_dialog_sick_content_textView ,"sick", selectedList)
            selectDialog.show(childFragmentManager, "")
        }

        //年齢選択
        dialog.friend_add_dialog_age_content_textView.setOnClickListener {
        /*   val selectDialog = selectDialogMultiple("年齢",
               dialog.friend_add_dialog_age_content_textView ,"age", selectedList)
            selectDialog.show(childFragmentManager, "")*/
        }

        //住所検索
        dialog.friend_add_dialog_live_content_textView.setOnClickListener {
//            val selectDialog = selectDialogMultiple("都道府県",  dialog.friend_add_dialog_live_content_textView,"live")
//            selectDialog.show(childFragmentManager, "")
        }

        //趣味選択
        dialog.friend_add_dialog_hobby_content_textView.setOnClickListener {
            val selectDialog = selectDialogMultiple("趣味",  dialog.friend_add_dialog_hobby_content_textView,"hoby", selectedList)
            selectDialog.show(childFragmentManager, "")
        }

        return dialog
    }

    private fun callActivity(mutableList: MutableList<User>){
        val callingActivity = test as FriendAddActivity
        callingActivity!!.returnDialog(mutableList)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        test = activity

    }
}