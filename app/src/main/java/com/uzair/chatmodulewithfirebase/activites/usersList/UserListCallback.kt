package com.uzair.chatmodulewithfirebase.activites.usersList

import com.uzair.chatmodulewithfirebase.dataClasses.UserInfoDataClass

interface UserListCallback {
    fun onUserSelected(userInfo:UserInfoDataClass)
}