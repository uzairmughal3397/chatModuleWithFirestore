package com.uzair.chatmodulewithfirebase.activites.selectUser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.uzair.chatmodulewithfirebase.R
import com.uzair.chatmodulewithfirebase.SharedPrefHelper
import com.uzair.chatmodulewithfirebase.activites.inbox.InboxActivity
import com.uzair.chatmodulewithfirebase.activites.usersList.UserListCallback
import com.uzair.chatmodulewithfirebase.activites.groupList.adapter.GroupListRvAdapter
import com.uzair.chatmodulewithfirebase.activites.usersList.adapter.UserListsRvAdapter
import com.uzair.chatmodulewithfirebase.dataClasses.UserInfoDataClass
import java.text.SimpleDateFormat
import java.util.*

class SelectUserActivity : AppCompatActivity(), UserListCallback {

    companion object {
        const val CURRENT_USER_ID_kEY= "key1"
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
        val dummyUserList= arrayListOf(
            UserInfoDataClass("adnan","456"),
            UserInfoDataClass("uzair","123"),
            UserInfoDataClass("daniyal","789"),
            UserInfoDataClass("minhaj","0123"),
            UserInfoDataClass("usman","346")
        )
        findViewById<RecyclerView>(R.id.userListsRv).adapter= UserListsRvAdapter(dummyUserList,this)
    }

    override fun onUserSelected(userInfo: UserInfoDataClass) {
        SharedPrefHelper.writeString(this, CURRENT_USER_ID_kEY,userInfo.id)
        SharedPrefHelper.writeString(this, CURRENT_USER_NAME_KEY,userInfo.name)

        startActivity(Intent(this,InboxActivity::class.java))

    }

}