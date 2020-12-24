package com.uzair.chatmodulewithfirebase.dataClasses

import android.os.Parcel
import android.os.Parcelable

class InboxDataClass():Parcelable {
    var date: String = ""
    var last_message = ""
    var receiverId = ""
    var receiverName = ""
    var senderId = ""
    var senderName = ""
    var timeInMilli = ""
    var unreadMessages = ""

    constructor(parcel: Parcel) : this() {
        date = parcel.readString()!!
        last_message = parcel.readString()!!
        receiverId = parcel.readString()!!
        receiverName = parcel.readString()!!
        senderId = parcel.readString()!!
        senderName = parcel.readString()!!
        timeInMilli = parcel.readString()!!
        unreadMessages = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(date)
        parcel.writeString(last_message)
        parcel.writeString(receiverId)
        parcel.writeString(receiverName)
        parcel.writeString(senderId)
        parcel.writeString(senderName)
        parcel.writeString(timeInMilli)
        parcel.writeString(unreadMessages)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InboxDataClass> {
        override fun createFromParcel(parcel: Parcel): InboxDataClass {
            return InboxDataClass(parcel)
        }

        override fun newArray(size: Int): Array<InboxDataClass?> {
            return arrayOfNulls(size)
        }
    }

}
