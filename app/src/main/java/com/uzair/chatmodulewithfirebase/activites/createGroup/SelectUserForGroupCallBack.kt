package com.uzair.chatmodulewithfirebase.activites.createGroup

import com.uzair.chatmodulewithfirebase.dataClasses.UserInfoClass

interface SelectUserForGroupCallBack {

    fun onCheckBoxChange(userInfo: UserInfoClass, isSelected: Boolean)
}