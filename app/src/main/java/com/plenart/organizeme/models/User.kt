package com.plenart.organizeme.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = 0,
    val fcmToken: String =""
): Parcelable{
    constructor(parcel: Parcel): this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!

    )

    override fun describeContents() = 0;

    companion object : Parceler<User> {

        val CREATOR = object: Parcelable.Creator<User>{             //careful!
            override fun createFromParcel(source: Parcel?): User {
                return User(source!!);
            }

            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)

        }

        override fun User.write(dest: Parcel, flags: Int) = with(dest) {
            writeString(id);
            writeString(name);
            writeString(email);
            writeString(image);
            writeLong(mobile);
            writeString(fcmToken);

        }

        override fun create(parcel: Parcel): User = TODO()
    }



}