package com.uzair.chatmodulewithfirebase.activites.createGroup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uzair.chatmodulewithfirebase.R
import com.uzair.chatmodulewithfirebase.dataClasses.UserInfoClass
//import com.uzair.chatmodulewithfirebase.dataClasses.UserInfoDataClass

class UserListsForGroupRvAdapter(
    private val arrayOfUsers: MutableList<UserInfoClass>,
    val callBack: SelectUserForGroupCallBack
) : RecyclerView.Adapter<UserListsForGroupRvAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(userInfoDataClass: UserInfoClass) {
            itemView.findViewById<TextView>(R.id.tvUsername).text = userInfoDataClass.name
            itemView.findViewById<CheckBox>(R.id.cbUser)
                .setOnCheckedChangeListener { _, b ->
                    callBack.onCheckBoxChange(userInfoDataClass, b)
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.select_users_rv_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(arrayOfUsers[position])
    }

    override fun getItemCount(): Int {
        return arrayOfUsers.size
    }
}