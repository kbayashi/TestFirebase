package com.example.testfirebase

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_list_fragment.*
import kotlinx.android.synthetic.main.user_list_fragment.view.*

class UserListFragment: Fragment() {

    var userListAdapter:userListAdapter? = null
    var groupListAdapter:groupListAdapter? = null
    var friendDisplayFlg = false
    var groupDisplayFlg = false

    //フラグメントにレイアウトを設定
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_list_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //ダミー
        dummydata(groupListAdapter!!)

        //ユーザ取り出して表示
        fetchUsers()

        //友達リストを表示・非表示
        view.user_list_friend_constraintLayout.setOnClickListener {
            friendDisplaySwitching(view)
        }
        //グループリストを表示・非表示
        view.user_list_group_constraintLayout.setOnClickListener {
            groupDisplaySwitching(view)
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userListAdapter = userListAdapter(context)
        groupListAdapter = groupListAdapter(context)
    }

    //ダミーデータ格納
    fun dummydata(groupListAdapter: groupListAdapter){
        groupListAdapter.add()
        groupListAdapter.add()
        groupListAdapter.add()
        groupListAdapter.add()
        groupListAdapter.add()

    }
    //ユーザ取り出す
    private fun fetchUsers(){
        val db = FirebaseFirestore.getInstance()
        var loginUser:User? = null
        val uid = FirebaseAuth.getInstance().uid
        val loginUserRef = db.collection("user").document(uid!!)

        loginUserRef.get().addOnSuccessListener {
            Log.d("ユーザ取得", "${it.data}")
            loginUser = it.toObject(User::class.java)
            Log.d("ユーザ取得", "ログインしているユーザ名${loginUser?.name}")

            //初期設定
            setUp(view!!,loginUser!!)

            val users = db.collection("user")
            users.get().addOnSuccessListener {
                it.forEach {
                    Log.d("ユーザ取得","${it.toObject(User::class.java)}")
                    var getUser = it.toObject(User::class.java)
                    Log.d("ユーザ取得","${getUser.name}")
                    if(!(loginUser?.name == getUser.name)) {
                        Log.d("ユーザ" ,"${loginUser}")
                        Log.d("ユーザ", "${getUser}")
                        userListAdapter?.add(getUser)
                    }
                }
            }.addOnFailureListener {
                Log.d("ユーザ取得失敗", it.message)
            }
        }

    }

    //ビューの初期化
    private fun setUp(view: View, user: User){

        view.user_list_user_recyclerView.adapter = userListAdapter
        view.user_list_user_recyclerView.visibility = View.GONE

        view.user_list_group_list_recyclerView.adapter = groupListAdapter
        view.user_list_group_list_recyclerView.visibility = View.GONE

        //recyclerviewに下線を足す
        view.user_list_user_recyclerView.addItemDecoration(DividerItemDecoration(activity,
            DividerItemDecoration.VERTICAL))

        view.user_list_my_name_textView.text = user.name
        view.user_list_my_pr_textView.text = user.pr
        //テストでネット上の画像を表示できるか試した
        view.user_list_my_imageView.visibility = View.INVISIBLE
        Picasso.get().load("https://www.jalan.net/news/img/2020/02/20200213_neko-break_1.jpg").into(view.user_list_my_circleimageView)

    }

    //友達表示・非表示
    private fun friendDisplaySwitching(view: View){

        if(friendDisplayFlg == true){
            friendDisplayFlg = false
            view.user_list_user_recyclerView.visibility = View.GONE
            view.user_list_friend_title_arrow_imageView.setImageResource(R.drawable.ic_expand_less_24dp)
        }else{
            friendDisplayFlg = true
            view.user_list_user_recyclerView.visibility = View.VISIBLE
            view.user_list_friend_title_arrow_imageView.setImageResource(R.drawable.ic_expand_more_24dp)
        }
    }

    //グループ表示・非表示
    private fun groupDisplaySwitching(view: View){

        if(groupDisplayFlg == true){
            groupDisplayFlg = false
            view.user_list_group_list_recyclerView.visibility = View.GONE
            view.user_list_group_title_arrow_imageView.setImageResource(R.drawable.ic_expand_less_24dp)
        }else{
            groupDisplayFlg = true
            view.user_list_group_list_recyclerView.visibility = View.VISIBLE
            view.user_list_group_title_arrow_imageView.setImageResource(R.drawable.ic_expand_more_24dp)
        }
    }

}