package com.uzair.chatmodulewithfirebase.activites.groupChat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.uzair.chatmodulewithfirebase.R
import com.uzair.chatmodulewithfirebase.dataClasses.GroupChatDataClass
import com.uzair.chatmodulewithfirebase.databinding.GroupChatRvLayoutLeftBinding
import com.uzair.chatmodulewithfirebase.databinding.GroupChatRvLayoutRightBinding

class GroupChatAdapter(
    private val chatArrayList: ArrayList<GroupChatDataClass>,
    private val currentUserId: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val leftSide = 4444
    private val rightSide = 5555


    inner class ViewHolderLeft(private val itemBinding: GroupChatRvLayoutLeftBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindData(chatMessageDataClass: GroupChatDataClass) {
            itemBinding.tvMessage.text=chatMessageDataClass.message
            itemBinding.tvTime.text=chatMessageDataClass.time
            itemBinding.tvSenderName.text=chatMessageDataClass.senderName
        }
    }

    inner class ViewHolderRight(private val itemBinding: GroupChatRvLayoutRightBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindData(chatMessageDataClass: GroupChatDataClass) {
            itemBinding.tvMessage.text=chatMessageDataClass.message
            itemBinding.tvTime.text=chatMessageDataClass.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == leftSide) {
            ViewHolderLeft(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.group_chat_rv_layout_left,
                    parent,
                    false
                )
            )
        } else {
            ViewHolderRight(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.group_chat_rv_layout_right,
                    parent,
                    false
                )
            )
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderLeft)
            holder.bindData(chatArrayList[position])
        else if (holder is ViewHolderRight)
            holder.bindData(chatArrayList[position])
    }

    override fun getItemCount(): Int {
        return chatArrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatArrayList[position].senderId != currentUserId)
            leftSide
        else
            rightSide
    }
}