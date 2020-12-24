package com.uzair.chatmodulewithfirebase.activites.groupChat

import android.os.Bundle
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
import com.uzair.chatmodulewithfirebase.activites.groupChat.adapter.GroupChatAdapter
import com.uzair.chatmodulewithfirebase.activites.selectUser.SelectUserActivity
import com.uzair.chatmodulewithfirebase.dataClasses.ChatMessageDataClass
import com.uzair.chatmodulewithfirebase.dataClasses.GroupChatDataClass
import com.uzair.chatmodulewithfirebase.databinding.ActivityGroupChatBinding

class GroupChatActivity : BaseActivity() {
    private var isLoadData=false
    lateinit var binding: ActivityGroupChatBinding
    var groupId = ""
    var groupName = ""
    lateinit var firestoreRef: FirebaseFirestore
    var chatMessageArray = arrayListOf<GroupChatDataClass>()
    var layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

    var itemPos = 0
    var mLastKey: String? = ""
    var mPrevKey: String? = ""

    private val totalItemsToLoad = 10
    private var currentPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_chat)

        groupId = intent.getStringExtra("groupId")!!
        groupName = intent.getStringExtra("groupName")!!
        firestoreRef = Firebase.firestore

        binding.selectedGroupName.text = groupName

        binding.chatRv.layoutManager = layoutManager
        layoutManager.reverseLayout = true
        binding.chatRv.adapter = GroupChatAdapter(chatMessageArray, currentUserId)
        readGroupMessages()

        binding.btnSend.setOnClickListener {
            sendMessage(binding.edtMessage.text.toString())
        }

        binding.chatRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(v: RecyclerView, dx: Int, scrollY: Int) {
                super.onScrolled(v, dx, scrollY)

                if (v.getChildAt(v.childCount - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.childCount - 1)
                            .measuredHeight - v.measuredHeight)) &&
                        scrollY < 0
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

    private fun getPaginatedMessages() {
        binding.pbLoading.visibility= View.VISIBLE

        val readMsgRef = firestoreRef.collection("GroupChats")
            .document(groupId)
            .collection(groupId)
            .orderBy("timeInMilli", Query.Direction.DESCENDING)
            .startAfter(chatMessageArray.last().timeInMilli)
            .limit((currentPage * totalItemsToLoad).toLong())

        readMsgRef.addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            value!!.documents.forEach {

                val message = it.toObject(GroupChatDataClass::class.java)!!
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

    private fun readGroupMessages() {
        val readMsgRef = firestoreRef.collection("GroupChats")
            .document(groupId)
            .collection(groupId)
            .orderBy("timeInMilli", Query.Direction.DESCENDING)
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
                        val messageKey = message!!.timeInMilli
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
        val sendMsgRef = firestoreRef.collection("GroupChats").document(groupId).collection(groupId)

        val sendMsgHashMap = hashMapOf<String, Any>(
            Pair("message", message),
            Pair("groupId", groupId),
            Pair("senderName", currentUserName),
            Pair("senderId", currentUserId),
            Pair("time", SelectUserActivity.currentTime()),
            Pair("timeInMilli", System.currentTimeMillis().toString()),
        )


        sendMsgRef.add(sendMsgHashMap).addOnSuccessListener {
            Toast.makeText(this, "Send Message", Toast.LENGTH_SHORT).show()
            binding.edtMessage.setText("")
        }

    }
}