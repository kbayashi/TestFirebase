package com.example.testfirebase

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_profile_edit_plain_text.*
import kotlinx.android.synthetic.main.activity_user_profile_edit_spinner.*

class UserProfileEditSpinnerActivity : AppCompatActivity() {

    //自分のユーザインスタンスを生成
    private val auth = FirebaseAuth.getInstance()
    private val me = auth.currentUser
    //DB
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_edit_spinner)
        val spinner = findViewById<Spinner>(R.id.activity_user_profile_edit_spinner_spinner)
        val table = intent.getStringExtra("table")
        var edit = intent.getStringExtra("edit")

        if(table == "birthday"){
            val adapter = ArrayAdapter.createFromResource(this,R.array.age,android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }else if(table == "live") {
            val adapter = ArrayAdapter.createFromResource(this, R.array.live, android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }else if(table == "sick"){
            val adapter = ArrayAdapter.createFromResource(this,R.array.sick,android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }else{
            val adapter = ArrayAdapter.createFromResource(this,R.array.life_expectancy,android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long

                ) {
                    val spinnerParent = parent as Spinner
                    val item = spinnerParent.selectedItem as String
                    edit = item
                }

                //アイテムが選択されなかった
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
        }

        //保存ボタン
        activity_user_profile_edit_spinner_save_button.setOnClickListener {

            if(table == "old"){
                //更新可能
                val ref = db.collection("user").document(me!!.uid).update(
                    table,
                    activity_user_profile_edit_spinner_spinner.selectedItem.toString()
                )
                    .addOnSuccessListener {
                        Log.d("DB", "更新できました")
                    }
                    .addOnFailureListener {
                        Log.d("DB", "更新できません ${it.message}")
                    }
            }else if(table == "live"){
                //更新可能
                val ref = db.collection("user").document(me!!.uid).update(
                    table,
                    activity_user_profile_edit_spinner_spinner.selectedItem.toString()
                )
                    .addOnSuccessListener {
                        Log.d("DB", "更新できました")
                    }
                    .addOnFailureListener {
                        Log.d("DB", "更新できません ${it.message}")
                    }
            }else if(table == "sick"){
                //更新可能
                val ref = db.collection("user").document(me!!.uid).update(
                    table,
                    activity_user_profile_edit_spinner_spinner.selectedItem.toString()
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
                    activity_user_profile_edit_spinner_spinner.selectedItem.toString()
                )
                    .addOnSuccessListener {
                        Log.d("DB", "更新できました")
                    }
                    .addOnFailureListener {
                        Log.d("DB", "更新できません ${it.message}")
                    }
            }

            finish()
        }
    }
}