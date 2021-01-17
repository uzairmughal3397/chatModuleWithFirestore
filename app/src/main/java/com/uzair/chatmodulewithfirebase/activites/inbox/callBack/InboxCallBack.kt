package com.uzair.chatmodulewithfirebase.activites.inbox.callBack

import com.uzair.chatmodulewithfirebase.dataClasses.GeneralInboxDataClass

interface InboxCallBack {
    fun onUserSelected(selectedUser: GeneralInboxDataClass)
}