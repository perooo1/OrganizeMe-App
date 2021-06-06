package com.plenart.organizeme.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize


@Parcelize
data class SelectedMembers(
    val id: String = "",
    val image: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun describeContents() = 0

    companion object : Parceler<SelectedMembers> {

        override fun SelectedMembers.write(dest: Parcel, flags: Int) = with(dest) {
            writeString(id);
            writeString(image);

        }

        override fun create(source: Parcel): SelectedMembers {
            return SelectedMembers(source!!);
        }

    }
}