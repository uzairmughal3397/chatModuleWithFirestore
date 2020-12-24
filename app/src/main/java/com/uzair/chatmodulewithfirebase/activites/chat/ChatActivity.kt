package com.uzair.chatmodulewithfirebase.activites.chat

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.uzair.chatmodulewithfirebase.R
import com.uzair.chatmodulewithfirebase.activites.BaseActivity
import com.uzair.chatmodulewithfirebase.activites.chat.adapter.ChatAdapter
import com.uzair.chatmodulewithfirebase.activites.selectUser.SelectUserActivity
import com.uzair.chatmodulewithfirebase.dataClasses.ChatMessageDataClass
import com.uzair.chatmodulewithfirebase.dataClasses.InboxDataClass
import com.uzair.chatmodulewithfirebase.dataClasses.UserStatusDataClass
import com.uzair.chatmodulewithfirebase.databinding.ActivityChatBinding
import java.util.*
import kotlin.math.log


class ChatActivity : BaseActivity() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)

        firestoreRef = Firebase.firestore

        if (intent.getParcelableExtra<InboxDataClass>("inboxInfo") != null) {
            val inboxInfo = intent.getParcelableExtra<InboxDataClass>("inboxInfo")
            selectedName = inboxInfo!!.receiverName
            selectedId = inboxInfo.receiverId
        }
        else {
            selectedName = intent.getStringExtra("name")!!
            selectedId = intent.getStringExtra("id")!!
        }

        binding.selectedUserName.text = selectedName
        binding.btnSend.setOnClickListener {
            val msg = binding.edtMessage.text
            sendMessage(msg.toString(), selectedName, selectedId)
        }


        readMessages(selectedId)
        getUserStatus(selectedId)


        seenMessage(selectedId)
        binding.chatRv.layoutManager = layoutManager
        layoutManager.reverseLayout = true
        binding.chatRv.adapter = ChatAdapter(chatMessageArray, currentUserId)







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
                        val firstVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()

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

        binding.pbLoading.visibility=View.VISIBLE
        val readMessageRef = firestoreRef.collection("GeneralChats")
            .document(currentUserId).collection(selectedId)
            .orderBy("timeInMilli", Query.Direction.DESCENDING)
            .startAfter(chatMessageArray.last().timeInMilli)
            .limit((totalItemsToLoad).toLong())

        readMessageRef.addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            value!!.documents.forEach {

                val message = it.toObject(ChatMessageDataClass::class.java)!!
                val messageKey = message.timeInMilli
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
            binding.pbLoading.visibility=View.GONE
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

            if (userStatus!!.state == "offline") binding.userStatus.text =
                "Last Seen:${userStatus.lastSeen}"
            else if (userStatus.state == "Online") binding.userStatus.text = userStatus.state


        }
    }

    private fun seenMessage(selectedId: String) {
        val getReceiverSentMsgRef =
            firestoreRef.collection("GeneralChats").document(selectedId)
                .collection(currentUserId)
                .whereEqualTo("status", "sent")

        /*Getting Lists of message of other users which are not seen*/

        getReceiverSentMsgRef.addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            if (value?.documents != null && value.documents.isNotEmpty()) {

                value.documents.forEach {

                    /*Updateing the documents with status seen*/
                    if (it.data!!["receiverId"] == currentUserId)
                        firestoreRef.collection("GeneralChats").document(selectedId)
                            .collection(currentUserId).document(it.id)
                            .update("status", "seen")
                }
            }
        }
    }

    private fun readMessages(selectedId: String) {
        val readMessageRef = firestoreRef.collection("GeneralChats")
            .document(currentUserId).collection(selectedId)
            .orderBy("timeInMilli", Query.Direction.DESCENDING)
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
                    itemPos++
                    if (itemPos == 1) {
                        val messageKey = message.timeInMilli
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

    private fun sendMessage(msg: String, selectedName: String?, selectedId: String?) {

        val senderRef =
            firestoreRef.collection("GeneralChats").document(currentUserId).collection(selectedId!!)
                .document()

        val refKey = senderRef.id

        val senderHashMap = HashMap<String, Any>()
        senderHashMap["senderId"] = currentUserId
        senderHashMap["senderName"] = currentUserName
        senderHashMap["receiverName"] = selectedName!!
        senderHashMap["receiverId"] = selectedId
        senderHashMap["status"] = "sent"
        senderHashMap["message"] = msg
        senderHashMap["time"] = SelectUserActivity.currentTime()
        senderHashMap["timeInMilli"] = System.currentTimeMillis().toString()

        /*Sending Message*/

        senderRef.set(senderHashMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show()
                binding.edtMessage.setText("")
            }
            .addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }


        val receiverRef =
            firestoreRef.collection("GeneralChats").document(selectedId)
                .collection(currentUserId).document(refKey)

        val receiverHashMap = HashMap<String, Any>()
        receiverHashMap["senderId"] = currentUserId
        receiverHashMap["senderName"] = currentUserName
        receiverHashMap["receiverName"] = selectedName
        receiverHashMap["receiverId"] = selectedId
        receiverHashMap["message"] = msg
        receiverHashMap["time"] = SelectUserActivity.currentTime()
        receiverHashMap["timeInMilli"] = System.currentTimeMillis().toString()

        receiverRef.set(receiverHashMap)




        val inboxHashMap = HashMap<String, String?>()
        inboxHashMap["last_message"] = msg
        inboxHashMap["date"] = SelectUserActivity.currentTime()
        inboxHashMap["timeInMilli"] = System.currentTimeMillis().toString()

        /*Updating Inbox of sender*/

        inboxHashMap["senderName"] = currentUserName
        inboxHashMap["senderId"] = currentUserId
        inboxHashMap["receiverName"] = selectedName
        inboxHashMap["receiverId"] = selectedId

        /*Update Sender Inbox*/
//            .document("lastMessage")


        /*1st way*/

        val senderInboxRef = firestoreRef.collection("generalChatInbox").document(currentUserId)
            .collection(currentUserId).document(selectedId)
        senderInboxRef.set(inboxHashMap)
            .addOnSuccessListener {}
            .addOnFailureListener {}


        /*Updating Inbox of receiver*/
        inboxHashMap["senderName"] = selectedName
        inboxHashMap["senderId"] = selectedId
        inboxHashMap["receiverName"] = currentUserName
        inboxHashMap["receiverId"] = currentUserId


        /*Update receiver Inbox*/

        val receiverInboxRef = firestoreRef.collection("generalChatInbox")
            .document(selectedId).collection(selectedId).document(currentUserId)

        receiverInboxRef.set(inboxHashMap)
            .addOnSuccessListener {}
            .addOnFailureListener { }

    }
}