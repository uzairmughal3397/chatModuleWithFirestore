package com.uzair.chatmodulewithfirebase.dataClasses

import java.util.*
import kotlin.collections.ArrayList

class GeneralInboxDataClass {
    lateinit var lastMsgTime: Date
    lateinit var id: String
    var lastMsg: String? = null
    lateinit var senderId: String
    lateinit var senderName: String
    var users = ArrayList<UserInfoClass>()
    var usersId = ArrayList<String>()
    var isGroupChat :Boolean = false
    var unreadMessages = 0
}
