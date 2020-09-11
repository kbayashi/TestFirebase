package com.example.testfirebase

import android.content.Context
import android.content.Intent
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

    companion object{
        val SELECT_USER = "SELECT_USER"
    }

    var userListAdapter:userListAdapter? = null
    var groupListAdapter:groupListAdapter? = null
    var friendDisplayFlg = false
    var groupDisplayFlg = false

    val uid = FirebaseAuth.getInstance().uid

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
        fetchUsers(view)

        //友達リストを表示・非表示
        view.user_list_friend_constraintLayout.setOnClickListener {
            friendDisplaySwitching(view)
        }
        //グループリストを表示・非表示
        view.user_list_group_constraintLayout.setOnClickListener {
            groupDisplaySwitching(view)
        }

        //ユーザプロフィール画面に飛ばしたい
        userListAdapter?.setOnclickListener {user->
            val intent = Intent(context, UserProfileActivity::class.java)
            intent.putExtra(SELECT_USER, user)
            Log.d(SELECT_USER, "${user.name}")
            startActivity(intent)
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
    private fun fetchUsers(view: View){
        val db = FirebaseFirestore.getInstance()
        var loginUser:User? = null
        val loginUserRef = db.collection("user").document(uid!!)

        loginUserRef.get().addOnSuccessListener {
            Log.d("ユーザ取得", "${it.data}")
            loginUser = it.toObject(User::class.java)
            Log.d("ユーザ取得", "ログインしているユーザ名${loginUser?.name}")

            //初期設定
            setUp(view,loginUser!!)

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
            //自分のプロフィール画面に飛ばしたい
            view.user_list_my_profile_constraintLayout.setOnClickListener {
                val intent = Intent(context, UserMyProfileActivity::class.java)
                intent.putExtra(SELECT_USER, loginUser)
                startActivity(intent)
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
        /*view.user_list_user_recyclerView.addItemDecoration(DividerItemDecoration(activity,
            DividerItemDecoration.VERTICAL))*/

        view.user_list_my_name_textView.text = user.name
        view.user_list_my_pr_textView.text = user.pr

        //自分のアイコン表示
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("user").document(uid!!)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    //読み込み
                    Picasso.get().load(document.getString("img")).into(view.user_list_my_circleimageView)
                } else {
                    //画像が読み込めないとき
                    Picasso.get().load("https://cv.tipsfound.com/windows10/02014/8.png").into(view.user_list_my_circleimageView)
                }
            }

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