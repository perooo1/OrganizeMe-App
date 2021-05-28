package com.plenart.organizeme.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize


@Parcelize
data class Board(
    val name: String = "",
    val image : String = "",
    val createdBy: String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    var documentID: String = ""

): Parcelable{
    constructor(parcel: Parcel): this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!
    )

    override fun describeContents() = 0

    companion object : Parceler<Board> {
        override fun Board.write(dest: Parcel, flags: Int) = with(dest) {
           writeString(name);
           writeString(image);
           writeString(createdBy);
           writeStringList(assignedTo);
            writeString(documentID);
        }

        override fun create(parcel: Parcel): Board = TODO()
    }

}