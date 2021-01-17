package com.uzair.chatmodulewithfirebase.activites.inbox.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.uzair.chatmodulewithfirebase.R
import com.uzair.chatmodulewithfirebase.activites.inbox.callBack.InboxCallBack
import com.uzair.chatmodulewithfirebase.dataClasses.GeneralInboxDataClass
import com.uzair.chatmodulewithfirebase.dataClasses.UserInfoClass
import com.uzair.chatmodulewithfirebase.databinding.InboxRvLayoutBinding

class InboxAdapter(
    private val generalInboxArray: ArrayList<GeneralInboxDataClass>,
    val currentUserInfo: UserInfoClass,
    private val callBack: InboxCallBack
) : RecyclerView.Adapter<InboxAdapter.ViewHolder>() {

    inner class ViewHolder(private val itemBinding: InboxRvLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindData(generalInboxDataClass: GeneralInboxDataClass) {
            val remainingList = arrayListOf<UserInfoClass>()
            generalInboxDataClass.users.forEach {
                if (it.id != currentUserInfo.id) {
                    remainingList.add(UserInfoClass(it.name, it.id,""))
                }
            }
            itemBinding.tvName.text = remainingList[0].name
            generalInboxDataClass.senderName = remainingList[0].name
            generalInboxDataClass.senderId = remainingList[0].id
            itemBinding.tvLastMessage.text = generalInboxDataClass.lastMsg
            itemBinding.tvTime.text = generalInboxDataClass.lastMsgTime.toString()
            itemBinding.tvUnreadMessages.text = generalInboxDataClass.unreadMessages.toString()
            itemBinding.root.setOnClickListener {
                callBack.onUserSelected(generalInboxDataClass)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.inbox_rv_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(generalInboxArray[position])
    }

    override fun getItemCount(): Int {
        return generalInboxArray.size
    }
}