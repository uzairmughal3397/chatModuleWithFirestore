package com.uzair.chatmodulewithfirebase.activites.groupList.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uzair.chatmodulewithfirebase.R
import com.uzair.chatmodulewithfirebase.activites.groupList.GroupListActivity
import com.uzair.chatmodulewithfirebase.activites.groupList.GroupListCallback
import com.uzair.chatmodulewithfirebase.activites.usersList.UserListCallback
import com.uzair.chatmodulewithfirebase.activites.usersList.adapter.UserListsRvAdapter
import com.uzair.chatmodulewithfirebase.dataClasses.GroupInfoDataClass
import com.uzair.chatmodulewithfirebase.dataClasses.UserInfoDataClass

class GroupListRvAdapter(private val arrayOfUsers:ArrayList<GroupInfoDataClass>,
                         val callBack: GroupListCallback
)
    :RecyclerView.Adapter<GroupListRvAdapter.ViewHolder>() {
    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        fun bindData(groupInfoDataClass: GroupInfoDataClass) {
            itemView.findViewById<TextView>(R.id.userName).text = groupInfoDataClass.name
            itemView.findViewById<TextView>(R.id.userName).setOnClickListener{
                callBack.onGroupSelected(groupInfoDataClass)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_list_rv_layout,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(arrayOfUsers[position])
    }

    override fun getItemCount(): Int {
        return arrayOfUsers.size
    }
}