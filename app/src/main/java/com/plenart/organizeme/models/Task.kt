package com.plenart.organizeme.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize


@Parcelize
data class Task(
    var title: String = "",
    val createdBy: String = "",
    var cards: ArrayList<Card> = ArrayList()


):Parcelable{
    constructor(parcel: Parcel): this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Card.CREATOR)!!
    )

    override fun describeContents(): Int = 0;

    companion object: Parceler<Task> {

        val CREATOR = object: Parcelable.Creator<Task>{
            override fun createFromParcel(source: Parcel?): Task {
                return Task(source!!);
            }

            override fun newArray(size: Int): Array<Task> {
                TODO("Not yet implemented")
            }

        }


        override fun Task.write(dest: Parcel, flags: Int) = with(dest) {
            writeString(title);
            writeString(createdBy);
            writeTypedList(cards);
        }

        override fun create(parcel: Parcel): Task {
            TODO("Not yet implemented")
        }
    }


}
