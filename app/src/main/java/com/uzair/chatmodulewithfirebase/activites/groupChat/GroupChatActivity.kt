package com.uzair.chatmodulewithfirebase.activites.groupChat

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.*
import com.uzair.chatmodulewithfirebase.R
import com.uzair.chatmodulewithfirebase.activites.BaseActivity
import com.uzair.chatmodulewithfirebase.activites.chat.callBacks.ChatSelectionCallBack
import com.uzair.chatmodulewithfirebase.activites.groupChat.adapter.GroupChatAdapter
import com.uzair.chatmodulewithfirebase.activites.groupList.GroupInboxDataClass
import com.uzair.chatmodulewithfirebase.dataClasses.GroupChatDataClass
import com.uzair.chatmodulewithfirebase.dataClasses.UserInfoClass
import com.uzair.chatmodulewithfirebase.databinding.ActivityGroupChatBinding
import java.util.*
import kotlin.collections.ArrayList

class GroupChatActivity : BaseActivity(), ChatSelectionCallBack {
    private var isLoadData = false
    lateinit var binding: ActivityGroupChatBinding

    //    var groupId = ""
    var groupName = ""
    var userLists = arrayListOf<UserInfoClass>()
    var groupChatId = ""

    lateinit var firestoreRef: FirebaseFirestore
    var chatMessageArray = arrayListOf<GroupChatDataClass>()
    var layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

    var itemPos = 0
    var mLastKey: String? = ""
    var mPrevKey: String? = ""

    private val totalItemsToLoad = 10
    private var currentPage = 1
    var seenListener: ListenerRegistration? = null
    lateinit var getSeenMsgRef: Query

    override fun onStart() {
        super.onStart()
//        if (seenListener == null)
//            seenMessage()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_chat)

        groupChatId = intent.getStringExtra("groupChatId")!!
        groupName = intent.getStringExtra("groupName")!!
        userLists = intent.getParcelableArrayListExtra("usersList")!!
        firestoreRef = FirebaseFirestore.getInstance()

        if (groupChatId.isEmpty()) {
            groupChatId = firestoreRef.collection("Chats").document().id
        }

        binding.selectedGroupName.text = groupName

        binding.chatRv.layoutManager = layoutManager
        layoutManager.reverseLayout = true
        binding.chatRv.adapter = GroupChatAdapter(userLists,chatMessageArray, currentUserId, this)

        seenMessage()
        readGroupMessages()

        binding.btnSend.setOnClickListener {
            sendMessage(binding.edtMessage.text.toString())
            seenMessage()
        }

        binding.chatRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(v: RecyclerView, dx: Int, scrollY: Int) {
                super.onScrolled(v, dx, scrollY)
                if (v.getChildAt(v.childCount - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.childCount - 1)
                            .measuredHeight - v.measuredHeight)) && scrollY < 0
                    ) {
                        val visibleItemCount = binding.chatRv.childCount
                        val totalItemCount = layoutManager.itemCount
                        val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                        if (isLoadData) {
                            if ((visibleItemCount + firstVisibleItem) >= totalItemCount) {
                                currentPage += 1
                                getPaginatedMessages()
                                isLoadData = false
                            }
                        }
                    }
                }
            }
        })

    }

    private fun seenMessage() {
        getSeenMsgRef = firestoreRef.collection("Chats").document(groupChatId)
            .collection("Threads")
            .whereNotEqualTo("senderId", currentUserId)

        getSeenMsgRef.get().addOnSuccessListener { value ->
            if (value?.documents != null && value.documents.isNotEmpty()) {
                value.documents.forEach {
                    val message = it.toObject(GroupChatDataClass::class.java)!!
                    if (!message.seenBy.contains(currentUserId)
                        &&
                        !message.deletedFor.contains(currentUserId)
                    ) {
                        message.seenBy.add(currentUserId)
                        firestoreRef.collection("Chats").document(groupChatId)
                            .collection("Threads").document(message.id!!)
                            .update("seenBy", message.seenBy).addOnSuccessListener {

                            }.addOnFailureListener { exception ->
                                Toast.makeText(this, "seen exception:${exception}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
        }
//            .addSnapshotListener{ value, error ->
//            if (error != null) {
//                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
//                return@addSnapshotListener
//            }
//
//            if (value?.documents != null && value.documents.isNotEmpty()) {
//                value.documents.forEach {
//                    val message = it.toObject(GroupChatDataClass::class.java)!!
//                    message.seenBy.add(currentUserId)
//                    if (!message.seenBy.contains(currentUserId)
//                        &&
//                        !message.deletedFor.contains(currentUserId)
//                    ) {
//                        firestoreRef.collection("Chats").document(groupChatId)
//                            .collection("Threads").document(message.messageId!!)
//                            .update("seenBy", message.seenBy).addOnSuccessListener {
//                                it
//                            }.addOnFailureListener { exception ->
//                                exception
//                            }
//                    }
//                }
//            }
//        }
    }


    private fun getPaginatedMessages() {
        binding.pbLoading.visibility = View.VISIBLE

        val readMsgRef = firestoreRef.collection("Chats")
            .document(groupChatId)
            .collection("Threads")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .startAfter(chatMessageArray.last().createdAt)
            .limit((currentPage * totalItemsToLoad).toLong())

        readMsgRef.addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            value!!.documents.forEach {
                val message = it.toObject(GroupChatDataClass::class.java)!!
                val messageKey = message.id
                if (mPrevKey != messageKey) {
                    chatMessageArray.add(message)
                } else {
                    mPrevKey = mLastKey
                }
                if (itemPos == 1) {
                    mLastKey = messageKey
                }
            }
            isLoadData = true
            binding.chatRv.adapter!!.notifyDataSetChanged()
            binding.pbLoading.visibility = View.GONE
        }

    }

    private fun readGroupMessages() {
        val readMsgRef = firestoreRef.collection("Chats")
            .document(groupChatId)
            .collection("Threads")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit((currentPage * totalItemsToLoad).toLong())

        readMsgRef.addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            if (value != null && value.documents.isNotEmpty()) {
                chatMessageArray.clear()
                value.documents.forEach {
                    val message = it.toObject(GroupChatDataClass::class.java)
                    itemPos++
                    if (itemPos == 1) {
                        val messageKey = message!!.id
                        mLastKey = messageKey
                        mPrevKey = messageKey
                    }

                    chatMessageArray.add(message!!)
                }
                isLoadData = true
                binding.chatRv.adapter!!.notifyDataSetChanged()
            }
        }
    }

    private fun sendMessage(message: String) {

        val groupChatDataClass = GroupChatDataClass()
        groupChatDataClass.createdAt = Date()
//        groupChatDataClass.groupId = groupChatId
        groupChatDataClass.message = message
        groupChatDataClass.senderId = currentUserId
        groupChatDataClass.senderName = currentUserName

        val messageId = firestoreRef.collection("Chats").document(groupChatId).collection("Threads").document().id

        groupChatDataClass.id = messageId

        firestoreRef
            .collection("Chats")
            .document(groupChatId)
            .collection("Threads")
            .document(messageId).set(groupChatDataClass).addOnSuccessListener {
                updateInbox(message)
            }.addOnFailureListener {
                Toast.makeText(
                    this,
                    "Message Sending Failed+${it.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    private fun updateInbox(msg: String) {

        val groupInboxDataClass = GroupInboxDataClass()
        groupInboxDataClass.groupName = groupName
        groupInboxDataClass.id = groupChatId
        groupInboxDataClass.lastMsg = ""
        groupInboxDataClass.senderId = ""
        groupInboxDataClass.senderName = ""
        groupInboxDataClass.createdAt = Date()


        val userIdList = arrayListOf(UserInfoClass(currentUserName, currentUserId,""))
        userLists.forEach { userIdList.add(UserInfoClass(it.name, it.id,"")) }
//        val userIdList2= userIdList.distinctBy { it.id }.toMutableList()
        groupInboxDataClass.users = userIdList.distinctBy { it.id } as ArrayList<UserInfoClass>

        val userIds = arrayListOf<String>()
        userIdList.forEach { userIds.add(it.id) }
        groupInboxDataClass.usersId = userIds.distinctBy { it } as ArrayList<String>


        firestoreRef
            .collection("Chats")
            .document(groupChatId)
            .set(groupInboxDataClass).addOnCompleteListener {
                Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

    override fun onChatSelection(chatData: Any) {
        if (chatData is GroupChatDataClass) {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.delete_dialog_layout)
            dialog.window!!.setLayout(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            val btnDeleteForMe = dialog.findViewById<MaterialButton>(R.id.btnDeleteMe)
            val btnDeleteForAll = dialog.findViewById<MaterialButton>(R.id.btnDeleteAll)
            val btnCancel = dialog.findViewById<MaterialButton>(R.id.btnCancel)
            if (chatData.senderId != currentUserId) {
                btnDeleteForAll.visibility = View.GONE
            }
            btnDeleteForMe.setOnClickListener {
                deleteMessageForMe(chatData)
                dialog.dismiss()
            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            btnDeleteForAll.setOnClickListener {
                deleteMessageForAll(chatData)
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    private fun deleteMessageForAll(chatData: GroupChatDataClass) {
        firestoreRef.collection("Chats")
            .document(groupChatId)
            .collection("Threads")
            .document(chatData.id!!).delete()
    }

    override fun onPause() {
        super.onPause()
//        seenListener?.remove()
    }

    private fun deleteMessageForMe(chatData: GroupChatDataClass) {
        chatData.deletedFor.add(currentUserId)
        val deleteForList = chatData.deletedFor
        firestoreRef.collection("Chats")
            .document(groupChatId)
            .collection("Threads")
            .document(chatData.id!!)
            .update("deletedFor", deleteForList)
    }
}