package com.example.testfirebase

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.user_list_fragment.view.*

class UserListFragment: Fragment() {

    companion object{
        val SELECT_USER = "SELECT_USER"
    }

    var userListAdapter:userListAdapter? = null
    var groupListAdapter:groupListAdapter? = null
    var friendTemporaryRegistrationAdapter:friendTemporaryRegistrationAdapter? = null
    var friendDisplayFlg = false
    var groupDisplayFlg = false
    var friendTemporaryRegistrationFlg = false

    val uid = FirebaseAuth.getInstance().uid
    val db = FirebaseFirestore.getInstance()

    val friendRef = db.collection("user-friend").document("get")
    val FTPRef = db.collection("friend-temporary-registration").document("get")

    // フラグメントにレイアウトを設定
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // グループリストを表示
        grouplist(groupListAdapter!!)

        // ユーザ取り出して表示
        fetchUsers(view)

        // 友達リストを表示・非表示
        view.user_list_friend_constraintLayout.setOnClickListener {
            friendDisplaySwitching(view)
        }
        // グループリストを表示・非表示
        view.user_list_group_constraintLayout.setOnClickListener {
            groupDisplaySwitching(view)
        }
        // 友達申請表示・非表示
        view.user_list_temporary_registration_constraintLayout.setOnClickListener {
            temporaryRegistrationSwitching(view)
        }

        // ユーザプロフィール画面に飛ばしたい
        userListAdapter?.setOnclickListener {user->
            val intent = Intent(context, UserProfileActivity::class.java)
            intent.putExtra(SELECT_USER, user)
            Log.d(SELECT_USER, "${user.name}")
            startActivity(intent)
        }

        // グループチャット画面に遷移する
        groupListAdapter?.setOnclickListener {
            val intent = Intent(context, GroupChatActivity::class.java)
            intent.putExtra("GroupId", it)
            startActivity(intent)
        }
          
        // チャット画面に飛ばす
        userListAdapter?.setTalkTransitionListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(SELECT_USER, it)
            // Log.d(SELECT_USER, "${user.name}")
            startActivity(intent)
        }

        // 相手のプロフィール画面に飛ばす
        friendTemporaryRegistrationAdapter?.setOnClickListener {
            val intent = Intent(context, UserProfileActivity::class.java)
            intent.putExtra(SELECT_USER, it)
            startActivity(intent)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userListAdapter = userListAdapter(context)
        groupListAdapter = groupListAdapter(context)
        friendTemporaryRegistrationAdapter = friendTemporaryRegistrationAdapter(context)
    }

    // グループリストを表示
    fun grouplist(groupListAdapter: groupListAdapter){

        // 所属するグループ全てを表示
        db.collection("group-status").document(uid!!).collection("join").get()
            .addOnSuccessListener {
                for (item in it) {
                    // デバッグ
                    Log.d("join", "${item.id} => ${item.data}")

                    // 取得したIDからさらに問い合わせ
                    db.collection("group").document(item.id).get()
                        .addOnSuccessListener {
                            val groupData = it.toObject(Group::class.java)
                            groupListAdapter.add(groupData!!)
                        }
                        .addOnFailureListener {
                            // 失敗
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("join", "Error getting documents: ", exception)
            }

        // 一旦DBにあるグループすべて表示
        /* val group = db.collection("group-join").document(uid!!).collection("join")
        group.get()
            .addOnSuccessListener {
                it.forEach {
                    var groupData = it.toObject(Group::class.java)
                    groupListAdapter.add(groupData)
                }
            }
            .addOnFailureListener {
                // グループの取得に失敗してます
            }*/
    }

    // 友達を取り出す
    private fun fetchUsers(view: View){

        var loginUser:User? = null
        val loginUserRef = db.collection("user").document(uid!!)


        loginUserRef.get().addOnSuccessListener {
            Log.d("ユーザ取得", "${it.data}")
            loginUser = it.toObject(User::class.java)
            Log.d("ユーザ取得", "ログインしているユーザ名${loginUser?.name}")

            // 初期設定
            setUp(view,loginUser!!)

            // 友達を取り出す
            val users = db.collection("user")
            friendRef.collection(loginUser!!.uid).
                addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    userListAdapter?.clear()
                    querySnapshot?.forEach {
                        db.collection("user").document(it.id).get().addOnSuccessListener {
                            var user = it.toObject(User::class.java)
                            db.collection("block-user").document("get")
                                .collection(uid).document(user!!.uid).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                                    if(documentSnapshot!!["uid"] != user.uid){
                                        userListAdapter?.add(user!!)
                                    }
                                    view.user_list_user_recyclerView.adapter = userListAdapter
                                }
                        }
                    }
                    view.user_list_user_recyclerView.adapter = userListAdapter
                }

            // 自分のプロフィール画面に飛ばしたい
            view.user_list_my_profile_constraintLayout.setOnClickListener {
                val intent = Intent(context, UserMyProfileActivity::class.java)
                startActivity(intent)
            }
        }

        // 仮登録された人を取り出す
        FTPRef.collection(uid).get().addOnSuccessListener {
            it.forEach {id ->
                FirebaseFirestore.getInstance().collection("user").document(id.id)
                    .get().addOnSuccessListener { item->
                        var user = item.toObject(User::class.java)
                        Log.d("仮登録ユーザ","${user?.name}")
                        Log.d("karitouroku","${user?.name}")
                        friendTemporaryRegistrationAdapter?.add(user!!)
                        if(friendTemporaryRegistrationAdapter!!.itemCount > 0) {
                            view.user_list_temporary_registration_recyclerView.visibility = View.VISIBLE
                            view.user_list_temporary_registration_constraintLayout.visibility = View.VISIBLE
                            view.user_list_temporary_registration_recyclerView.adapter =
                                friendTemporaryRegistrationAdapter

                        }else{
                            view.user_list_temporary_registration_recyclerView.visibility = View.GONE
                            view.user_list_temporary_registration_constraintLayout.visibility = View.GONE
                        }
                }
            }
        }
    }

    // ビューの初期化
    private fun setUp(view: View, user: User){

        view.user_list_user_recyclerView.adapter = userListAdapter
        view.user_list_user_recyclerView.visibility = View.GONE

        view.user_list_group_list_recyclerView.adapter = groupListAdapter
        view.user_list_group_list_recyclerView.visibility = View.GONE

        view.user_list_temporary_registration_recyclerView.visibility = View.GONE
        view.user_list_temporary_registration_recyclerView.adapter = friendTemporaryRegistrationAdapter
        // recyclerviewに下線を足す
        /*
            view.user_list_user_recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        */

        view.user_list_my_name_textView.text = user.name
        view.user_list_my_pr_textView.text = user.pr

        // 自分のアイコン表示
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("user").document(uid!!)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // 読み込み
                    Picasso.get().load(document.getString("img")).into(view.user_list_my_circleimageView)
                } else {
                    // 画像が読み込めないとき
                    Picasso.get().load("https://cv.tipsfound.com/windows10/02014/8.png").into(view.user_list_my_circleimageView)
                }
            }
    }

    // 友達表示・非表示
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

    // グループ表示・非表示
    private fun groupDisplaySwitching(view: View){

        if(groupDisplayFlg){
            groupDisplayFlg = false
            view.user_list_group_list_recyclerView.visibility = View.GONE
            view.user_list_group_title_arrow_imageView.setImageResource(R.drawable.ic_expand_less_24dp)
        }else{
            groupDisplayFlg = true
            view.user_list_group_list_recyclerView.visibility = View.VISIBLE
            view.user_list_group_title_arrow_imageView.setImageResource(R.drawable.ic_expand_more_24dp)
        }
    }

    // 友達仮登録表示・非表示
    private fun temporaryRegistrationSwitching(view: View){
        if(friendTemporaryRegistrationFlg == true){
            friendTemporaryRegistrationFlg = false
            view.user_list_temporary_registration_recyclerView.visibility = View.GONE
            view.user_list_temporary_registration_imageView.setImageResource(R.drawable.ic_expand_less_24dp)
        }else{
            friendTemporaryRegistrationFlg = true
            view.user_list_temporary_registration_recyclerView.visibility = View.VISIBLE
            view.user_list_temporary_registration_imageView.setImageResource(R.drawable.ic_expand_more_24dp)
        }
    }
}