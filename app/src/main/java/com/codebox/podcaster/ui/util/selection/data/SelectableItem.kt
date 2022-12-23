package com.codebox.podcaster.ui.util.selection.data

import android.os.Parcel
import android.os.Parcelable


sealed class SelectableItem : Parcelable {


    abstract var viewType: Int


    class TitleOnly(val title: String) : SelectableItem(), Parcelable {
        override var viewType: Int = SelectableItemTypes.TITLE_ONLY.ordinal

        constructor(parcel: Parcel) : this(parcel.readString()!!) {
            viewType = parcel.readInt()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(title)
            parcel.writeInt(viewType)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<TitleOnly> {
            override fun createFromParcel(parcel: Parcel): TitleOnly {
                return TitleOnly(parcel)
            }

            override fun newArray(size: Int): Array<TitleOnly?> {
                return arrayOfNulls(size)
            }
        }
    }
}


