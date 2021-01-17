package com.uzair.chatmodulewithfirebase.activites

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.uzair.chatmodulewithfirebase.SharedPrefHelper
import com.uzair.chatmodulewithfirebase.activites.selectUser.SelectUserActivity
import com.uzair.chatmodulewithfirebase.dataClasses.UserInfoClass

open class BaseActivity : AppCompatActivity() {

    open var currentUserId = ""
    open var currentUserName = ""
    lateinit var userRef: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentUserName = SharedPrefHelper.readString(this, SelectUserActivity.CURRENT_USER_NAME_KEY)
        currentUserId = SharedPrefHelper.readString(this, SelectUserActivity.CURRENT_USER_ID_kEY)
        userRef = Firebase.firestore.collection("UserStatus").document(currentUserId)

    }

    override fun onStart() {
        super.onStart()
        setUserOnLine()
    }

    private fun setUserOnLine() {
        val onLineHashMap= hashMapOf(
            Pair("userId",currentUserId),
            Pair("userName",currentUserName),
            Pair("state","online")
        )
        userRef.set(onLineHashMap)
    }

    override fun onPause() {
        super.onPause()
        setUserOffline()

    }

    private fun setUserOffline() {
        val offLineHashMap= hashMapOf(
            Pair("userId",currentUserId),
            Pair("userName",currentUserName),
            Pair("lastSeen",SelectUserActivity.currentTime()),
            Pair("state","offline")
        )
        userRef.set(offLineHashMap)
    }

     fun getUserList(): ArrayList<UserInfoClass> {
        val dummyUserList = arrayListOf(
            UserInfoClass("adnan", "456",""),
            UserInfoClass("uzair", "4",""),
            UserInfoClass("daniyal", "789",""),
            UserInfoClass("minhaj", "0123",""),
            UserInfoClass("usman", "346",""),
            UserInfoClass("faizan", "3","")
        )
    return dummyUserList
    }

}