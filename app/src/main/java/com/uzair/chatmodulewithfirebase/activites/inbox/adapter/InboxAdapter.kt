package com.uzair.chatmodulewithfirebase.activites.inbox.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.uzair.chatmodulewithfirebase.R
import com.uzair.chatmodulewithfirebase.activites.inbox.callBack.InboxCallBack
import com.uzair.chatmodulewithfirebase.dataClasses.InboxDataClass
import com.uzair.chatmodulewithfirebase.databinding.InboxRvLayoutBinding

class InboxAdapter(private val inboxArray: ArrayList<InboxDataClass>, private val callBack:InboxCallBack) :RecyclerView.Adapter<InboxAdapter.ViewHolder>() {

    inner class ViewHolder(private val itemBinding: InboxRvLayoutBinding):RecyclerView.ViewHolder(itemBinding.root) {
        fun bindData(inboxDataClass: InboxDataClass) {
            itemBinding.tvLastMessage.text=inboxDataClass.last_message
            itemBinding.tvName.text=inboxDataClass.receiverName
            itemBinding.tvLastMessage.text=inboxDataClass.last_message
            itemBinding.tvTime.text=inboxDataClass.date
            itemBinding.tvUnreadMessages.text=inboxDataClass.unreadMessages
            itemBinding.root.setOnClickListener {
                callBack.onUserSelected(inboxDataClass)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.inbox_rv_layout,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(inboxArray[position])
    }

    override fun getItemCount(): Int {
        return inboxArray.size
    }
}