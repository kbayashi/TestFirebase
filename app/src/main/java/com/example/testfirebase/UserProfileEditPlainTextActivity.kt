package com.example.testfirebase

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_profile_edit_plain_text.*


class UserProfileEditPlainTextActivity : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_edit_plain_text)

        val edit = intent.getStringExtra("edit")
        val text = activity_user_profile_edit_plain_text_plainText.length()
        if(edit == "name"){
            val limit = 20
            val editText = EditText(this)
            editText.inputType = InputType.TYPE_CLASS_TEXT
            editText.filters = arrayOf<InputFilter>(LengthFilter(limit))
            activity_user_profile_edit_plain_text_limitTextView.text = text.toString()+"/"+limit.toString()+"までです"
            activity_user_profile_edit_plain_text_plainText.setText(edit)
        }else{
            val limit = 300
            val editText = EditText(this)
            editText.inputType = InputType.TYPE_CLASS_TEXT
            editText.filters = arrayOf<InputFilter>(LengthFilter(limit))
            activity_user_profile_edit_plain_text_limitTextView.text = limit.toString()+"までです"
            activity_user_profile_edit_plain_text_plainText.setText(edit)
        }
        activity_user_profile_edit_plain_text_savebutton.setOnClickListener{
            db
        }
    }
}