package com.codebox.podcaster.repo

import com.codebox.podcaster.storage.db.app.segment.Flag
import com.codebox.podcaster.storage.db.app.segment.Segment
import java.io.File

interface SegmentRepository {

    suspend fun generateSegmentFile(files: List<File>): File?

    suspend fun persistSegment(segment: Segment): Long

    suspend fun persistFlagsOfSegment(flags: List<Flag>)


}
