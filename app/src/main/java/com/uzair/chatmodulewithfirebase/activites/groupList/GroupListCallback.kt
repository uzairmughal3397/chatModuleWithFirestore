package com.uzair.chatmodulewithfirebase.activites.groupList

import com.uzair.chatmodulewithfirebase.dataClasses.GroupInfoDataClass

interface GroupListCallback {
    fun onGroupSelected(groupInfo:GroupInboxDataClass)
}