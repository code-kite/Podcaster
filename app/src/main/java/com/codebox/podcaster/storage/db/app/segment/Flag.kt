package com.codebox.podcaster.storage.db.app.segment

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Codebox on 05/03/21
 */
@Parcelize
@Entity
class Flag(
    @ColumnInfo
    val segmentId: Long,
    @ColumnInfo
    val secondsAfterRecording: Int,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
) : Parcelable{

    override fun toString(): String {
        return "Segment Id: $segmentId, seconds: $secondsAfterRecording"
    }

}