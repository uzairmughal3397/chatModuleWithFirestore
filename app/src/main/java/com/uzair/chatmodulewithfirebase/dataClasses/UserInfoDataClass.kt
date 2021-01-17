package com.uzair.chatmodulewithfirebase.dataClasses

import android.os.Parcel
import android.os.Parcelable

class UserInfoClass() : Parcelable {
    constructor(name: String, id: String,imgStr:String) : this() {
        this.name = name
        this.id = id
    }

    var name = ""
    var id = ""
    var imgStr = ""

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()!!
        id = parcel.readString()!!
        imgStr = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(id)
        parcel.writeString(imgStr)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserInfoClass> {
        override fun createFromParcel(parcel: Parcel): UserInfoClass {
            return UserInfoClass(parcel)
        }

        override fun newArray(size: Int): Array<UserInfoClass?> {
            return arrayOfNulls(size)
        }
    }
}
