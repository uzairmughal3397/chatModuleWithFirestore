package com.uzair.chatmodulewithfirebase.dataClasses

import java.util.*

class ChatMessageDataClass {
    var createdAt=Date()
    var message=""
    var senderId=""
    var senderName=""
    var id=""
    var seenBy= arrayListOf<String>()
    var deletedFor= arrayListOf<String>()
}