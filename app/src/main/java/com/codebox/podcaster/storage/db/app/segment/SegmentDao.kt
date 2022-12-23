package com.codebox.podcaster.storage.db.app.segment

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

/**
 * Created by Codebox on 02/03/21
 */
@Dao
interface SegmentDao {

    @Insert
    suspend fun insertSegment(segment: Segment): Long

    @Transaction
    @Query("Select * from Segment")
    suspend fun getSegmentWithFlags(): List<SegmentWithFlags>

}