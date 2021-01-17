package com.uzair.chatmodulewithfirebase.activites.groupList.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uzair.chatmodulewithfirebase.R
import com.uzair.chatmodulewithfirebase.activites.groupList.GroupInboxDataClass
import com.uzair.chatmodulewithfirebase.activites.groupList.GroupListCallback
import com.uzair.chatmodulewithfirebase.dataClasses.GroupInfoDataClass

class GroupListRvAdapter(
    private val arrayOfUsers: ArrayList<GroupInboxDataClass>,
    val callBack: GroupListCallback
) : RecyclerView.Adapter<GroupListRvAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(groupInfoDataClass: GroupInboxDataClass) {
            itemView.findViewById<TextView>(R.id.tvGroupName).text = groupInfoDataClass.groupName
            itemView.findViewById<TextView>(R.id.tvSenderName).text = groupInfoDataClass.senderName
            itemView.findViewById<TextView>(R.id.tvLastMessage).text = groupInfoDataClass.lastMsg
            itemView.findViewById<TextView>(R.id.tvUnreadMessages).text = groupInfoDataClass.unreadMessages.toString()
            itemView.findViewById<TextView>(R.id.tvTime).text = groupInfoDataClass.createdAt.toString()
            itemView.setOnClickListener {
                callBack.onGroupSelected(groupInfoDataClass)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.group_list_rv_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(arrayOfUsers[position])
    }

    override fun getItemCount(): Int {
        return arrayOfUsers.size
    }

}