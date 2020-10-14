package com.example.testfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_time_line_comment.*
import java.util.*

class TimeLineCommentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_line_comment)

        val timeLineData = intent.getParcelableExtra<TimeLine>("TimeLine")

        FirebaseFirestore.getInstance().collection("time-line").document(timeLineData.id).set(timeLineData)

        Log.d("TimeLineComment","$timeLineData")

        //コメントを投稿
        time_line_send_button_mageView.setOnClickListener {
            if(time_line_comment_editTextTextPersonName.text != null){
                val id = UUID.randomUUID()
                val setData = hashMapOf(
                    "id" to id,
                    "uid" to FirebaseAuth.getInstance().uid.toString()
                )
                FirebaseFirestore.getInstance().collection("time-line-comment").document(id.toString()).collection("get").document().set(setData).addOnSuccessListener {
                    timeLineData.comment = setData["uid"].toString()
                    FirebaseFirestore.getInstance().collection("time-line").document(timeLineData.id).set(timeLineData)

                }
            }
        }
    }



}