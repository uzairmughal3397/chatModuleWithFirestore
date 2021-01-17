package com.uzair.chatmodulewithfirebase.activites.usersList

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.uzair.chatmodulewithfirebase.R
import com.uzair.chatmodulewithfirebase.activites.BaseActivity
import com.uzair.chatmodulewithfirebase.activites.chat.ChatActivity
import com.uzair.chatmodulewithfirebase.activites.usersList.adapter.UserListsRvAdapter
import com.uzair.chatmodulewithfirebase.dataClasses.UserInfoClass

class UserListsActivity : BaseActivity(), UserListCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_lists)

        setUpUserLists()
    }

    private fun setUpUserLists() {
        val filterList= getUserList().filter { it.id != currentUserId }.toMutableList()
        findViewById<RecyclerView>(R.id.userListsRv).adapter =
            UserListsRvAdapter(filterList, this)
    }

    override fun onUserSelected(userInfo: UserInfoClass) {
        startActivity(
            Intent(this, ChatActivity::class.java)
                .putExtra("name", userInfo.name)
                .putExtra("id", userInfo.id)
                .putExtra("image", userInfo.imgStr)
        )
    }
}