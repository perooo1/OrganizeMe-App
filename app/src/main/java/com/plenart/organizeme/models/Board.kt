package com.plenart.organizeme.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize


@Parcelize
data class Board(
    val name: String = "",
    val image: String = "",
    val createdBy: String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    var documentID: String = "",

    var taskList: ArrayList<Task> = ArrayList()

): Parcelable{
    constructor(parcel: Parcel): this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Task.CREATOR)!!
    )

    override fun describeContents() = 0

    companion object : Parceler<Board> {

        val CREATOR = object: Parcelable.Creator<Board>{             //careful!
            override fun createFromParcel(source: Parcel?): Board {
                return Board(source!!);
            }

            override fun newArray(size: Int): Array<Board?> = arrayOfNulls(size)

        }

        override fun Board.write(dest: Parcel, flags: Int) = with(dest) {
           writeString(name);
           writeString(image);
           writeString(createdBy);
           writeStringList(assignedTo);
            writeString(documentID);
            writeTypedList(taskList);
        }

        override fun create(parcel: Parcel): Board =
            Board(parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.createStringArrayList()!!,
                parcel.readString()!!,
                parcel.createTypedArrayList(Task.CREATOR)!!)

    }

}