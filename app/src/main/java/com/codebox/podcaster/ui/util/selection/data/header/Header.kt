package com.codebox.podcaster.ui.util.selection.data.header

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.codebox.podcaster.R

/**
 * Created by Codebox on 31/05/21
 */
sealed class Header : Parcelable {


    abstract var layoutId: Int
        protected set

    abstract fun getView(inflater: LayoutInflater, parent: ViewGroup): View


    class TitleOnly(var title: String) : Header(), Parcelable {
        override var layoutId: Int = R.layout.item_title_only_header

        override fun getView(inflater: LayoutInflater, parent: ViewGroup): View {
            val view = inflater.inflate(layoutId, parent, false)
            view.findViewById<TextView>(R.id.title).text = title
            return view
        }


        constructor(parcel: Parcel) : this(parcel.readString()!!) {
            layoutId = parcel.readInt()
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(parcel: Parcel?, flags: Int) {
            parcel?.writeString(title)
            parcel?.writeInt(layoutId)
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