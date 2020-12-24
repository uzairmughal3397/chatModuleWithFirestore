package com.uzair.chatmodulewithfirebase.activites.usersList

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.uzair.chatmodulewithfirebase.R
import com.uzair.chatmodulewithfirebase.activites.BaseActivity
import com.uzair.chatmodulewithfirebase.activites.chat.ChatActivity
import com.uzair.chatmodulewithfirebase.activites.groupList.adapter.GroupListRvAdapter
import com.uzair.chatmodulewithfirebase.activites.usersList.adapter.UserListsRvAdapter
import com.uzair.chatmodulewithfirebase.dataClasses.UserInfoDataClass

class UserListsActivity : BaseActivity(), UserListCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_lists)

        setUpUserLists()

    }

    private fun setUpUserLists() {
        val dummyUserList = arrayListOf(
            UserInfoDataClass("adnan", "456"),
            UserInfoDataClass("uzair", "123"),
            UserInfoDataClass("daniyal", "789"),
            UserInfoDataClass("minhaj", "0123"),
            UserInfoDataClass("usman", "346")
        )

        dummyUserList.remove(UserInfoDataClass(currentUserName, currentUserId))
        findViewById<RecyclerView>(R.id.userListsRv).adapter =
            UserListsRvAdapter(dummyUserList, this)
    }

    override fun onUserSelected(userInfo: UserInfoDataClass) {
        startActivity(
            Intent(this, ChatActivity::class.java)
                .putExtra("name", userInfo.name)
                .putExtra("id", userInfo.id)
        )
    }
}