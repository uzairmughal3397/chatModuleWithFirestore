package com.uzair.chatmodulewithfirebase.dataClasses

import java.util.*

class GroupChatDataClass {
//    var groupId :String?=null
    var id :String?=null
    var message :String?=null
    var senderName :String?=null
    var senderId :String?=null
    var createdAt =Date()
    var seenBy= arrayListOf<String>()
    var deletedFor= arrayListOf<String>()
}