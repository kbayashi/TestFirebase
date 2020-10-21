package com.example.testfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_time_line_comment.*
import java.util.*

class TimeLineCommentActivity : AppCompatActivity() {

    val timeLineCommentAdapter = timeLineCommentAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_line_comment)

        val timeLineData = intent.getParcelableExtra<TimeLine>("TimeLine")

        time_line_comment_recyclerView.adapter = timeLineCommentAdapter

        Log.d("Comment", timeLineData.id)

            FirebaseFirestore.getInstance().collection("time-line-comment")
                .document(timeLineData.id).collection("get").orderBy("time", Query.Direction.DESCENDING).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    timeLineCommentAdapter.clear()
                    querySnapshot?.forEach {
                        Log.d("Comment", "${it["comment"]}")
                        timeLineCommentAdapter.add(it.toObject(Comment::class.java))
                    }
                    time_line_comment_recyclerView.adapter = timeLineCommentAdapter
                }


        //getData(timeLineData)
        Log.d("TimeLineComment","$timeLineData")

        //コメントを投稿
        time_line_send_button_imageView.setOnClickListener {
            if(time_line_comment_editTextTextPersonName.text != null){
                val id = UUID.randomUUID()
                val comment = Comment(id.toString(),FirebaseAuth.getInstance().uid.toString(), time_line_comment_editTextTextPersonName.text.toString(),  System.currentTimeMillis())
                FirebaseFirestore.getInstance().collection("time-line-comment").document(timeLineData.id).collection("get")
                    .document(id.toString()).set(comment)
                time_line_comment_editTextTextPersonName.text.clear()
            }
        }
    }

    fun getData(timeLine: TimeLine){
        FirebaseFirestore.getInstance().collection("time-line-comment").document(timeLine.uid).
        collection("get").get().addOnSuccessListener {
            it.forEach {
                Log.d("Comment", "$it")
                timeLineCommentAdapter.add(it.toObject(Comment::class.java))
            }
            time_line_comment_recyclerView.adapter = timeLineCommentAdapter
        }
    }

}