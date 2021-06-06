package com.plenart.organizeme.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
data class Card(
    val name: String = "",
    val createdBy: String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    val labelColor: String = "",
    val dueDate: Long = 0
): Parcelable{
    constructor(parcel: Parcel): this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.readLong()
    )

    override fun describeContents(): Int = 0;

    companion object : Parceler<Card> {

        val CREATOR = object: Parcelable.Creator<Card>{             //careful!
            override fun createFromParcel(source: Parcel?): Card {
                return Card(source!!);
            }

            override fun newArray(size: Int): Array<Card?> = arrayOfNulls(size)

        }

        override fun Card.write(dest: Parcel, flags: Int) = with(dest) {
            writeString(name);
            writeString(createdBy);
            writeStringList(assignedTo);
            writeString(labelColor);
            writeLong(dueDate);
        }

        override fun create(parcel: Parcel): Card = TODO()
    }

}
