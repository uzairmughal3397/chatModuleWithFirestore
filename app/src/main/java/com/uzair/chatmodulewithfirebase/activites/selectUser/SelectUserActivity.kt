package com.uzair.chatmodulewithfirebase.activites.selectUser

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.uzair.chatmodulewithfirebase.R
import com.uzair.chatmodulewithfirebase.SharedPrefHelper
import com.uzair.chatmodulewithfirebase.activites.BaseActivity
import com.uzair.chatmodulewithfirebase.activites.inbox.InboxActivity
import com.uzair.chatmodulewithfirebase.activites.usersList.UserListCallback
import com.uzair.chatmodulewithfirebase.activites.usersList.adapter.UserListsRvAdapter
import com.uzair.chatmodulewithfirebase.dataClasses.UserInfoClass
import java.text.SimpleDateFormat
import java.util.*

class SelectUserActivity : BaseActivity(), UserListCallback {

    companion object {
        const val CURRENT_USER_ID_kEY = "key1"
        const val CURRENT_USER_NAME_KEY = "Key2"

        fun currentTime(): String {
            val sdf = SimpleDateFormat("dd/MM hh:mm a", Locale.getDefault())
            return sdf.format(Date())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_user)

        setUpUserLists()
    }

    private fun setUpUserLists() {

        findViewById<RecyclerView>(R.id.userListsRv).adapter =
            UserListsRvAdapter(getUserList(), this)
    }

    override fun onUserSelected(userInfo: UserInfoClass) {
        SharedPrefHelper.writeString(this, CURRENT_USER_ID_kEY, userInfo.id.toString())
        SharedPrefHelper.writeString(this, CURRENT_USER_NAME_KEY, userInfo.name.toString())

        startActivity(Intent(this, InboxActivity::class.java))

    }

}