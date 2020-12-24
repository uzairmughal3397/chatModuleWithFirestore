package com.uzair.chatmodulewithfirebase.activites.usersList.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uzair.chatmodulewithfirebase.R
import com.uzair.chatmodulewithfirebase.activites.groupList.adapter.GroupListRvAdapter
import com.uzair.chatmodulewithfirebase.activites.usersList.UserListCallback
import com.uzair.chatmodulewithfirebase.dataClasses.UserInfoDataClass

class UserListsRvAdapter(private val arrayOfUsers:ArrayList<UserInfoDataClass>,val callBack:UserListCallback):RecyclerView.Adapter<UserListsRvAdapter.ViewHolder>() {
    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        fun bindData(userInfoDataClass: UserInfoDataClass) {
            itemView.findViewById<TextView>(R.id.userName).text = userInfoDataClass.name
            itemView.findViewById<TextView>(R.id.userName).setOnClickListener{
                callBack.onUserSelected(userInfoDataClass)
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