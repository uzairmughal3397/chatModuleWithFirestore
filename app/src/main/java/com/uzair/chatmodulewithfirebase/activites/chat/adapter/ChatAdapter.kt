package com.uzair.chatmodulewithfirebase.activites.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.uzair.chatmodulewithfirebase.R
import com.uzair.chatmodulewithfirebase.activites.inbox.InboxActivity
import com.uzair.chatmodulewithfirebase.dataClasses.ChatMessageDataClass
import com.uzair.chatmodulewithfirebase.databinding.ChatRvLayoutLeftBinding
import com.uzair.chatmodulewithfirebase.databinding.ChatRvLayoutRightBinding

class ChatAdapter(private val chatArrayList: ArrayList<ChatMessageDataClass>, private val currentUserId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val leftSide=4444
    private val rightSide=5555



    inner class ViewHolderLeft(private val itemBinding: ChatRvLayoutLeftBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindData(chatMessageDataClass: ChatMessageDataClass) {
                itemBinding.tvMessage.text=chatMessageDataClass.message
            itemBinding.tvTime.text=chatMessageDataClass.time
        }
    }

    inner class ViewHolderRight(private val itemBinding: ChatRvLayoutRightBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindData(chatMessageDataClass: ChatMessageDataClass) {
            itemBinding.tvMessage.text=chatMessageDataClass.message
            itemBinding.tvTime.text=chatMessageDataClass.time
            itemBinding.tvStatus.text=chatMessageDataClass.status
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType==leftSide){
            ViewHolderLeft(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.chat_rv_layout_left,parent,false))
        }
        else{
            ViewHolderRight(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.chat_rv_layout_right,parent,false))
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
        return if (chatArrayList[position].receiverId==currentUserId)
            leftSide
        else
            rightSide

    }
}