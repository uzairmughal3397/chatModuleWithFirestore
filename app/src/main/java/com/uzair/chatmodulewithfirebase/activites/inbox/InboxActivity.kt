package com.uzair.chatmodulewithfirebase.activites.inbox

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uzair.chatmodulewithfirebase.R
import com.uzair.chatmodulewithfirebase.SharedPrefHelper
import com.uzair.chatmodulewithfirebase.activites.BaseActivity
import com.uzair.chatmodulewithfirebase.activites.chat.ChatActivity
import com.uzair.chatmodulewithfirebase.activites.groupList.GroupListActivity
import com.uzair.chatmodulewithfirebase.activites.inbox.adapter.InboxAdapter
import com.uzair.chatmodulewithfirebase.activites.inbox.callBack.InboxCallBack
import com.uzair.chatmodulewithfirebase.activites.selectUser.SelectUserActivity
import com.uzair.chatmodulewithfirebase.activites.usersList.UserListsActivity
import com.uzair.chatmodulewithfirebase.dataClasses.ChatMessageDataClass
import com.uzair.chatmodulewithfirebase.dataClasses.GeneralInboxDataClass
import com.uzair.chatmodulewithfirebase.dataClasses.UserInfoClass
import com.uzair.chatmodulewithfirebase.databinding.ActivityInboxBinding


class InboxActivity : BaseActivity(), InboxCallBack {

    lateinit var dataBinding: ActivityInboxBinding

    val inboxArray = arrayListOf<GeneralInboxDataClass>()


    override fun onStart() {
        super.onStart()
        getInboxList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_inbox)

        dataBinding.tvCurrentUser.text =
            SharedPrefHelper.readString(this, SelectUserActivity.CURRENT_USER_NAME_KEY)

        dataBinding.inboxRv.adapter =
            InboxAdapter(inboxArray, UserInfoClass(currentUserName, currentUserId,""), this)

        dataBinding.btnSelectUser.setOnClickListener {
            startActivity(Intent(this, UserListsActivity::class.java))
        }

        dataBinding.btnGroupList.setOnClickListener {
            startActivity(Intent(this, GroupListActivity::class.java))
        }
    }


    private fun getInboxList() {
        val fireStoreObj = FirebaseFirestore.getInstance()

//        fireStoreObj.collection("Chats")
//            .whereArrayContains("usersId", currentUserId)
//            .orderBy("lastMsgTime", Query.Direction.ASCENDING)
//            .whereEqualTo("groupChat", false)
//            .get()
//            .addOnSuccessListener {
//                if (it!!.documents.isNotEmpty()) {
//                    inboxArray.clear()
//                    it.documents.forEach { documentSnapShot ->
//                        val data = documentSnapShot.toObject(GeneralInboxDataClass::class.java)!!
//                        inboxArray.add(0, data)
//                        dataBinding.inboxRv.adapter!!.notifyDataSetChanged()
//                    }
//                }
//            }.addOnFailureListener {
//                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
//            }


        inboxArray.clear()
        fireStoreObj.collection("Chats")
            .whereArrayContains("usersId", currentUserId)
            .orderBy("lastMsgTime", Query.Direction.ASCENDING)
            .whereEqualTo("groupChat", false).get()
            .addOnSuccessListener {
                it.documents.forEach { doc ->
                    val inboxData = doc.toObject(GeneralInboxDataClass::class.java)!!
                    fireStoreObj.collection("Chats")
                        .document(inboxData.id)
                        .collection("Threads")
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener { messageArray ->
                            val filteredMessagesArray = arrayListOf<ChatMessageDataClass>()
                            if (messageArray.documents.isNotEmpty()) {
                                messageArray.documents.forEach { messageDoc ->
                                    val messageData =
                                        messageDoc.toObject(ChatMessageDataClass::class.java)
                                    if (!messageData!!.deletedFor.contains(currentUserId))
                                        filteredMessagesArray.add(messageData)
                                }

                                val lastMessageData = filteredMessagesArray.first()

                                inboxData.senderId = lastMessageData.senderId
                                inboxData.senderName = lastMessageData.senderName
                                inboxData.lastMsg = lastMessageData.message
                                inboxData.lastMsgTime = lastMessageData.createdAt

                                var unreadCount = 0
                                filteredMessagesArray.forEach { messgeDoc ->
                                    if (messgeDoc.seenBy.isEmpty() && messgeDoc.senderId != currentUserId) {
                                        unreadCount++
                                    }
                                }
                                inboxData.unreadMessages = unreadCount
                                inboxArray.add(0, inboxData)
                                dataBinding.inboxRv.adapter!!.notifyDataSetChanged()
                            }
                            else{
                                Toast.makeText(this, "Inbox Is Empty", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { messageArrayExcpetion ->
                            messageArrayExcpetion
                        }
                }
            }.addOnFailureListener { inboxException ->
                inboxException
            }
    }

    override fun onUserSelected(selectedUser: GeneralInboxDataClass) {
        startActivity(
            Intent(this, ChatActivity::class.java)
                .putExtra("name", selectedUser.senderName)
                .putExtra("id", selectedUser.senderId)
                .putExtra("chatKey", selectedUser.id)
        )
    }
}