package com.example.testfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_friend_add.*

class FriendAddActivity : AppCompatActivity() {

    private var adapter:FriendAddAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_add)
        setSupportActionBar(friend_add_toolbar)

        friend_add_search_result_recyclerView.layoutManager = GridLayoutManager(this,3)
        adapter = FriendAddAdapter(this)


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

    private fun searchUser(keyword:String){
        FirebaseFirestore.getInstance().collection("user")
            .orderBy("name").startAt(keyword).endAt(keyword + "\uf8ff").get()
            .addOnSuccessListener {
                adapter?.clear()
                it.forEach {
                    var getUser = it.toObject(User::class.java)
                    Log.d("ユーザ取得できたか", getUser.name)
                    adapter?.add(getUser)
                }

                friend_add_search_result_recyclerView.adapter = adapter
            }
    }
}