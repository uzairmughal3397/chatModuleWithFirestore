package com.uzair.chatmodulewithfirebase.activites.inbox.callBack

import com.uzair.chatmodulewithfirebase.dataClasses.InboxDataClass

interface InboxCallBack {
    fun onUserSelected(selectedUser: InboxDataClass)
}