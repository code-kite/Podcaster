package com.codebox.podcaster.storage.db.app.segment

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.android.parcel.Parcelize

/**
 * Created by Codebox on 05/03/21
 */
@Parcelize
data class SegmentWithFlags(
    @Embedded val segment: Segment,
    @Relation(
        parentColumn = "id",
        entityColumn = "segmentId"
    )
    val flags: List<Flag>
) : Parcelable {


    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("\n")
        flags.forEach {
            builder.append(it)
            builder.append("\n")
        }
        return "Segment: $segment :: Flags: $builder"
    }

}