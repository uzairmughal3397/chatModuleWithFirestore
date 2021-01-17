package com.uzair.chatmodulewithfirebase.activites.groupList

import com.uzair.chatmodulewithfirebase.dataClasses.UserInfoClass
import java.util.*
import kotlin.collections.ArrayList

class GroupInboxDataClass {
    var createdAt = Date()
    var id = ""
    var lastMsg = ""
    var senderId = ""
    var senderName = ""
    var groupImgStr = ""
    var users = ArrayList<UserInfoClass>()
    var usersId = ArrayList<String>()
    var isGroupChat = true
    var groupName = ""
    var unreadMessages = 0
}