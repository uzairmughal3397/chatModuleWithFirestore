package com.uzair.chatmodulewithfirebase.activites.groupList

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.uzair.chatmodulewithfirebase.R
import com.uzair.chatmodulewithfirebase.activites.BaseActivity
import com.uzair.chatmodulewithfirebase.activites.groupChat.GroupChatActivity
import com.uzair.chatmodulewithfirebase.activites.groupList.adapter.GroupListRvAdapter
import com.uzair.chatmodulewithfirebase.dataClasses.GroupInfoDataClass
import com.uzair.chatmodulewithfirebase.databinding.ActivityGroupListBinding

class GroupListActivity : BaseActivity(), GroupListCallback {
    lateinit var binding:ActivityGroupListBinding
    private val allowedGroups= arrayListOf<GroupInfoDataClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_group_list)

        val groupList= arrayListOf(
                GroupInfoDataClass("Test Group 1","1", arrayListOf("123","456")),
                GroupInfoDataClass("Test Group 2","2", arrayListOf("789","0123","346")),
        )


        groupList.forEach { groupInfoDataClass ->
            if (groupInfoDataClass.listOfEmployees.contains(currentUserId))
                allowedGroups.add(groupInfoDataClass)
        }

        binding.groupList.adapter=GroupListRvAdapter(allowedGroups,this)

    }

    override fun onGroupSelected(groupInfo: GroupInfoDataClass) {
        startActivity(Intent(this,GroupChatActivity::class.java)
            .putExtra("groupName",groupInfo.name)
            .putExtra("groupId",groupInfo.id)
        )
    }
}