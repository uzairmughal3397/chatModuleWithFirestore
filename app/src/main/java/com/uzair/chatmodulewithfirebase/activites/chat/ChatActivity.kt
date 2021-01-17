package com.uzair.chatmodulewithfirebase.activites.chat

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.uzair.chatmodulewithfirebase.R
import com.uzair.chatmodulewithfirebase.activites.BaseActivity
import com.uzair.chatmodulewithfirebase.activites.chat.adapter.ChatAdapter
import com.uzair.chatmodulewithfirebase.activites.chat.callBacks.ChatSelectionCallBack
import com.uzair.chatmodulewithfirebase.dataClasses.ChatMessageDataClass
import com.uzair.chatmodulewithfirebase.dataClasses.GeneralInboxDataClass
import com.uzair.chatmodulewithfirebase.dataClasses.UserInfoClass
import com.uzair.chatmodulewithfirebase.dataClasses.UserStatusDataClass
import com.uzair.chatmodulewithfirebase.databinding.ActivityChatBinding
import java.util.*
import kotlin.collections.ArrayList


class ChatActivity : BaseActivity(), ChatSelectionCallBack {
    private var isLoadData: Boolean = false
    lateinit var binding: ActivityChatBinding
    lateinit var firestoreRef: FirebaseFirestore
    private val chatMessageArray = arrayListOf<ChatMessageDataClass>()
    var layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

    var itemPos = 0
    var mLastKey: String? = ""
    var mLastIndex = 0
    var mPrevKey: String? = ""

    val totalItemsToLoad = 10
    private var currentPage = 1
    private var selectedName = ""
    private var selectedId = ""
    private var selectedImage = ""
    private var chatKey: String? = null
    lateinit var seenListener: ListenerRegistration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)

        firestoreRef = Firebase.firestore

        selectedName = intent.getStringExtra("name")!!
        selectedId = intent.getStringExtra("id")!!
//        selectedImage = intent.getStringExtra("image")!!

        chatKey = if (intent.getStringExtra("chatKey") != null) {
            intent.getStringExtra("chatKey")
        } else {
            val userList = ArrayList<Int>()
            userList.add(currentUserId.toInt())
            userList.add(selectedId.toInt())
            val sortedArray = userList.sorted()
            "${sortedArray[0]}_${sortedArray[1]}"
        }
        binding.selectedUserName.text = selectedName
        binding.btnSend.setOnClickListener {
            val msg = binding.edtMessage.text
            sendMessage(msg.toString())
        }

//        if (chatKey != null)

        getUserStatus(selectedId)

        seenMessage()
        readMessages(chatKey!!)

        binding.chatRv.layoutManager = layoutManager
        layoutManager.reverseLayout = true
        binding.chatRv.adapter = ChatAdapter(chatMessageArray, currentUserId, this)


        binding.chatRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(v: RecyclerView, dx: Int, scrollY: Int) {
                super.onScrolled(v, dx, scrollY)

                if (v.getChildAt(v.childCount - 1) != null) {
                    if (
                        (scrollY >= (v.getChildAt(v.childCount - 1)
                            .measuredHeight - v.measuredHeight)) &&
                        scrollY < 0
                    ) {
                        val visibleItemCount = binding.chatRv.childCount
                        val totalItemCount = layoutManager.itemCount
                        val firstVisibleItem =
                            layoutManager.findFirstCompletelyVisibleItemPosition()

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

    private fun getPaginatedMessages() {

        binding.pbLoading.visibility = View.VISIBLE
        val readMessageRef = firestoreRef.collection("Chats")
            .document(chatKey!!).collection("Threads")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .startAfter(chatMessageArray.last().createdAt)
            .limit((totalItemsToLoad).toLong())

        readMessageRef.addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            value!!.documents.forEach {
                val message = it.toObject(ChatMessageDataClass::class.java)!!
                if (!message.deletedFor.contains(currentUserId)) {
                    val messageKey = message.id
                    if (mPrevKey != messageKey) {
                        chatMessageArray.add(message)
                    } else {
                        mPrevKey = mLastKey
                    }
                    if (itemPos == 1) {
                        mLastKey = messageKey
                    }
                    isLoadData = true
                    binding.chatRv.adapter!!.notifyDataSetChanged()
                    binding.pbLoading.visibility = View.GONE
                }
            }
        }

    }

    private fun getUserStatus(selectedId: String) {
        val userStatusRef = firestoreRef.collection("UserStatus").document(selectedId)
        userStatusRef.addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            val userStatus = value!!.toObject(UserStatusDataClass::class.java)
            if (userStatus != null) {
                if (userStatus.state == "offline") binding.userStatus.text =
                    "Last Seen:${userStatus.lastSeen}"
                else if (userStatus.state == "Online") binding.userStatus.text = userStatus.state
            }
        }
    }

    private fun seenMessage() {
        val currentUserList = listOf(currentUserId)
        val getReceiverSentMsgRef =
            firestoreRef.collection("Chats").document(chatKey!!)
                .collection("Threads")
                .whereEqualTo("senderId", selectedId)

        seenListener = getReceiverSentMsgRef.addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            if (value?.documents != null && value.documents.isNotEmpty()) {
                value.documents.forEach {
                    val message = it.toObject(ChatMessageDataClass::class.java)!!
                    if (!message.seenBy.contains(currentUserId)
                        &&
                        !message.deletedFor.contains(currentUserId)
                    ) {
                        firestoreRef.collection("Chats").document(chatKey!!)
                            .collection("Threads").document(message.id)
                            .update("seenBy", currentUserList).addOnSuccessListener {
                                it
                            }.addOnFailureListener { exception ->
                                exception
                            }
                    }
                }
            }
        }
    }

    private fun readMessages(selectedId: String) {
        val readMessageRef = firestoreRef.collection("Chats")
            .document(selectedId).collection("Threads")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit((currentPage * totalItemsToLoad).toLong())

        readMessageRef.addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            if (value != null && value.documents.isNotEmpty()) {
                chatMessageArray.clear()
                value.documents.forEach {
                    val message = it.toObject(ChatMessageDataClass::class.java)!!
                    if (!message.deletedFor.contains(currentUserId)) {
                        itemPos++
                        if (itemPos == 1) {
                            val messageKey = message.id
                            mLastKey = messageKey
                            mPrevKey = messageKey
                        }
                        chatMessageArray.add(message)
                    }
                    isLoadData = true
                    binding.chatRv.adapter!!.notifyDataSetChanged()
                }
            }
        }

    }

    private fun sendMessage(msg: String) {

        val chatRef = firestoreRef.collection("Chats").document(chatKey!!)
        val msgRef = chatRef.collection("Threads").document()
        val secondKey = msgRef.id
        val currentDate = Date()
        val messageObj = ChatMessageDataClass()
        messageObj.message = msg
        messageObj.senderId = currentUserId
        messageObj.senderName = currentUserName
        messageObj.id = secondKey
        messageObj.createdAt = currentDate

        chatRef.collection("Threads").document(secondKey)
            .set(messageObj).addOnSuccessListener {
                Toast.makeText(this, "Messages Sent", Toast.LENGTH_LONG).show()
                binding.edtMessage.setText("")
                updateInbox(chatKey!!, chatRef, msg, currentDate)
            }.addOnFailureListener {
                Toast.makeText(this, "Thread Errror:$it", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onPause() {
        super.onPause()
        seenListener.remove()
    }


    private fun updateInbox(
        firstKey: String,
        chatRef: DocumentReference,
        msg: String,
        currentDate: Date
    ) {

        val inboxDataClass = GeneralInboxDataClass()
        inboxDataClass.users = arrayListOf(
            UserInfoClass(selectedName, selectedId, ""),
            UserInfoClass(currentUserName, currentUserId, "")
        )

        inboxDataClass.usersId = arrayListOf(currentUserId, selectedId)
        inboxDataClass.lastMsg = null
        inboxDataClass.id = firstKey
        inboxDataClass.lastMsgTime = Date()
        inboxDataClass.senderId = ""
        inboxDataClass.senderName = ""
        inboxDataClass.isGroupChat = false

//        try {
        chatRef.set(inboxDataClass).addOnSuccessListener {

        }.addOnFailureListener {
            Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
        }
//        }catch (e:Exception){
//            e
//        }
    }

    override fun onChatSelection(chatData: Any) {
        if (chatData is ChatMessageDataClass) {
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

    private fun deleteMessageForAll(chatData: ChatMessageDataClass) {
        firestoreRef.collection("Chats")
            .document(chatKey!!)
            .collection("Threads")
            .document(chatData.id).delete()
    }

    private fun deleteMessageForMe(chatData: ChatMessageDataClass) {
        chatData.deletedFor.add(currentUserId)
        val deleteForList = chatData.deletedFor
        firestoreRef.collection("Chats")
            .document(chatKey!!)
            .collection("Threads")
            .document(chatData.id)
            .update("deletedFor", deleteForList)
    }
}