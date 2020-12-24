package com.uzair.chatmodulewithfirebase.activites.inbox

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.uzair.chatmodulewithfirebase.dataClasses.InboxDataClass
import com.uzair.chatmodulewithfirebase.databinding.ActivityInboxBinding
import java.util.*


class InboxActivity : BaseActivity(), InboxCallBack {

    lateinit var dataBinding: ActivityInboxBinding

    val inboxArray = arrayListOf<InboxDataClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_inbox)

        dataBinding.tvCurrentUser.text =
            SharedPrefHelper.readString(this, SelectUserActivity.CURRENT_USER_NAME_KEY)

        dataBinding.inboxRv.adapter = InboxAdapter(inboxArray, this)

        getInboxList()

        selectUserToChat()

        dataBinding.btnGroupList.setOnClickListener {
            startActivity(Intent(this,GroupListActivity::class.java))
        }
    }

    private fun selectUserToChat() {
        dataBinding.btnSelectUser.setOnClickListener {
            startActivity(Intent(this, UserListsActivity::class.java))
        }
    }

    private fun getInboxList() {
        val fireStoreObj = FirebaseFirestore.getInstance()
        fireStoreObj.collection("generalChatInbox")
            .document(currentUserId).collection(currentUserId)
            .orderBy("timeInMilli", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                if (value!!.documents.isNotEmpty()) {
                    inboxArray.clear()
                    value.documents.forEach { documentSnapShot ->

                        /*Getting Numbers of unread messages*/
                        fireStoreObj.collection("GeneralChats")
                            .document(documentSnapShot.data!!["receiverId"].toString())
                            .collection(currentUserId)
                            .whereEqualTo("status", "sent")
                            .get()
                            .addOnSuccessListener { querySnapShot ->
                                val data = documentSnapShot.toObject(InboxDataClass::class.java)!!
                                data.unreadMessages = querySnapShot.documents.size.toString()
                                inboxArray.add(data)
                                dataBinding.inboxRv.adapter!!.notifyDataSetChanged()
                            }.addOnFailureListener{
                                it.localizedMessage
                            }
                    }
                }
            }

    }

    override fun onUserSelected(selectedUser: InboxDataClass) {
        startActivity(
            Intent(this, ChatActivity::class.java)
                .putExtra("inboxInfo", selectedUser)
        )
    }
}