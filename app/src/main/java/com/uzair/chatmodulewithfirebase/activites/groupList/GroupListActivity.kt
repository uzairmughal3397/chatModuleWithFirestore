package com.uzair.chatmodulewithfirebase.activites.groupList

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uzair.chatmodulewithfirebase.R
import com.uzair.chatmodulewithfirebase.activites.BaseActivity
import com.uzair.chatmodulewithfirebase.activites.createGroup.SelectUserForGroupCallBack
import com.uzair.chatmodulewithfirebase.activites.createGroup.UserListsForGroupRvAdapter
import com.uzair.chatmodulewithfirebase.activites.groupChat.GroupChatActivity
import com.uzair.chatmodulewithfirebase.activites.groupList.adapter.GroupListRvAdapter
import com.uzair.chatmodulewithfirebase.dataClasses.*
import com.uzair.chatmodulewithfirebase.databinding.ActivityGroupListBinding

class GroupListActivity : BaseActivity(), GroupListCallback, SelectUserForGroupCallBack {
    lateinit var binding: ActivityGroupListBinding
    private val allowedGroups = arrayListOf<GroupInfoDataClass>()
    val selectedUser = ArrayList<UserInfoClass>()
    val inboxArray = arrayListOf<GroupInboxDataClass>()

    override fun onStart() {
        super.onStart()
        getLists()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_list)



        binding.btnCreateGroup.setOnClickListener {
            val createGroupDialog = Dialog(this)
            createGroupDialog.setContentView(R.layout.create_group_dialog)
            createGroupDialog.window!!.setLayout(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            val userRecyclerView = createGroupDialog.findViewById<RecyclerView>(R.id.rvUserName)
            val groupName = createGroupDialog.findViewById<EditText>(R.id.edtGroupName)
            val btnAddGroup = createGroupDialog.findViewById<MaterialButton>(R.id.btnAddGroup)


            val filterArray = getUserList().filter { it.id != currentUserId }.toMutableList()
            userRecyclerView.adapter = UserListsForGroupRvAdapter(filterArray, this)
            btnAddGroup.setOnClickListener {
                val groupNameText = groupName.text.toString()
                if (groupNameText.isNotEmpty() || selectedUser.isNotEmpty()) {
                    startActivity(
                        Intent(this, GroupChatActivity::class.java)
                            .putExtra("usersList", selectedUser)
                            .putExtra("groupChatId", "")
                            .putExtra("groupName", groupNameText)
                    )
                }
            }

            createGroupDialog.show()

        }

        binding.groupList.adapter = GroupListRvAdapter(inboxArray, this)


    }

    private fun getLists() {
        val fireStoreObj = FirebaseFirestore.getInstance()
        fireStoreObj.collection("Chats")
            .whereArrayContains("usersId", currentUserId)
            .whereEqualTo("groupChat", true).get()
            .addOnSuccessListener {
                if (it!!.documents.isNotEmpty()) {
                    inboxArray.clear()

//                    it.documents.forEach { documentSnapShot ->
//                        val data = documentSnapShot.toObject(GroupInboxDataClass::class.java)!!
//                        inboxArray.add(0, data)
//                        binding.groupList.adapter!!.notifyDataSetChanged()
//                    }

                    it.documents.forEach { doc ->
                        val inboxData = doc.toObject(GroupInboxDataClass::class.java)!!
                        fireStoreObj.collection("Chats")
                            .document(inboxData.id)
                            .collection("Threads")
                            .orderBy("createdAt", Query.Direction.DESCENDING)
                            .get()
                            .addOnSuccessListener { messageArray ->
                                val filteredMessagesArray = arrayListOf<GroupChatDataClass>()
                                messageArray.documents.forEach {messageDoc->
                                    val messageData =
                                        messageDoc.toObject(GroupChatDataClass::class.java)
                                    if (!messageData!!.deletedFor.contains(currentUserId))
                                        filteredMessagesArray.add(messageData)
                                }

                                val lastMessageData = filteredMessagesArray.first()

                                inboxData.senderId = lastMessageData.senderId!!
                                inboxData.senderName = lastMessageData.senderName!!
                                inboxData.lastMsg = lastMessageData.message!!
                                inboxData.createdAt = lastMessageData.createdAt
                                var unreadCount = 0
                                filteredMessagesArray.forEach { messgeDoc ->
                                    if (!messgeDoc.seenBy.contains(currentUserId) && messgeDoc.senderId != currentUserId) {
                                        unreadCount++
                                    }
                                }
                                inboxData.unreadMessages = unreadCount
                                inboxArray.add(0, inboxData)
                                binding.groupList.adapter!!.notifyDataSetChanged()
                            }
                            .addOnFailureListener { messageArrayExcpetion ->
                                messageArrayExcpetion
                            }
                    }

                }
            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }

    }

    override fun onGroupSelected(groupInfo: GroupInboxDataClass) {
        startActivity(
            Intent(this, GroupChatActivity::class.java)
                .putExtra("groupName", groupInfo.groupName)
                .putExtra("groupChatId", groupInfo.id)
                .putExtra("usersList", groupInfo.users)
        )
    }

    override fun onCheckBoxChange(userInfo: UserInfoClass, isSelected: Boolean) {
        if (isSelected) {
            selectedUser.add(userInfo)
        } else
            selectedUser.remove(userInfo)


        Toast.makeText(this, "Selected UserList+${selectedUser.size}", Toast.LENGTH_SHORT).show()
    }
}