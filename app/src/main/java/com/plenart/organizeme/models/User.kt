package com.plenart.organizeme.models

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = 0,
    val fcmToken: String ="",
    var selected: Boolean = false
): Parcelable{
    constructor(parcel: Parcel): this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,

    )

    override fun describeContents() = 0;

    companion object : Parceler<User> {

        override fun User.write(dest: Parcel, flags: Int) = with(dest) {
            writeString(id);
            writeString(name);
            writeString(email);
            writeString(image);
            writeLong(mobile);
            writeString(fcmToken);

        }

        override fun create(source: Parcel): User {
            return User(source!!);
        }

    }



}