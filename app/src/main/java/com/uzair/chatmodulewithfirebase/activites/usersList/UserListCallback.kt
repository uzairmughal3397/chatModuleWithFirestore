package com.uzair.chatmodulewithfirebase.activites.usersList

import com.uzair.chatmodulewithfirebase.dataClasses.UserInfoClass

interface UserListCallback {
    fun onUserSelected(userInfo: UserInfoClass)
}