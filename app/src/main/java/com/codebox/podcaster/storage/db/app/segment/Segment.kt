package com.codebox.podcaster.storage.db.app.segment

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.io.File

/**
 * Created by Codebox on 02/03/21
 */
@Parcelize
@Entity
data class Segment(
    var filePath: String,
    var durationInMillis: Long,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
) : Parcelable {
    @Ignore
    val file: File = File(filePath)

    override fun toString(): String {
        return "Id: $id :: File Path: $filePath :: Duration: $durationInMillis milliseconds"
    }

}
