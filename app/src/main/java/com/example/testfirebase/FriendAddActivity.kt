package com.example.testfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_friend_add.*

class FriendAddActivity : AppCompatActivity() {

    private var adapter:FriendAddAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_add)
        friend_add_search_result_recyclerView.layoutManager = GridLayoutManager(this,3)
        adapter = FriendAddAdapter(this)

        setAdapterDate()
        setSupportActionBar(friend_add_toolbar)

        //条件検索ダイアログ表示
        friend_add_search_button.setOnClickListener {
            val dialog = friendAddDialog()
            dialog.show(supportFragmentManager, "dialog")
        }

        adapter?.setOnclickListener { user ->
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra(UserListFragment.SELECT_USER, user)
            Log.d(UserListFragment.SELECT_USER, "${user.name}")
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchView = menu?.findItem(R.id.search_menu_friend_search)?.actionView as SearchView
        searchView.apply {
            isIconified = false
            queryHint = "山田達郎"
            clearFocus()
            setOnQueryTextListener(object : SearchView.OnQueryTextListener{

                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.d("Search", query)
                    return true
                }

                override fun onQueryTextChange(text: String?): Boolean {
                    Log.d("Search", text)
                    if(text != null)
                    searchUser(text)
                    return true
                }

            })
        }

        return true
    }

    //初期設定
    private fun setAdapterDate(){
        FirebaseFirestore.getInstance().collection("user").
        limit(5L).get().addOnSuccessListener {
            it.forEach {
                Log.d("friendAdd", it.toObject(User::class.java).name)
                var getUser = it.toObject(User::class.java)
                if(getUser.uid != FirebaseAuth.getInstance().uid) {
                    adapter?.add(getUser)
                }
            }
            friend_add_search_result_recyclerView.adapter = adapter
        }
    }

    private fun searchUser(keyword:String){
        FirebaseFirestore.getInstance().collection("user")
            .orderBy("name").startAt(keyword).endAt(keyword + "\uf8ff").get()
            .addOnSuccessListener {
                adapter?.clear()
                it.forEach {
                    var getUser = it.toObject(User::class.java)
                    if(getUser.uid != FirebaseAuth.getInstance().uid) {
                        Log.d("ユーザ取得できたか", getUser.name)
                        adapter?.add(getUser)
                    }
                }
                friend_add_search_result_recyclerView.adapter = adapter
            }
    }

    //検索結果を表示
    public fun returnDialog(mutableList: MutableList<User>){
        adapter?.clear()
        mutableList.forEach {
            adapter?.add(it)
        }

        friend_add_search_result_recyclerView.adapter = adapter
    }
}