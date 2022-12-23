package com.codebox.podcaster.storage.disk.segmentstorage

import com.codebox.podcaster.storage.disk.base.DiskStorage
import java.io.File

/**
 * Created by Codebox on 02/03/21
 */
interface SegmentDiskStorage : DiskStorage {

    companion object {
        const val SUB_SEGMENTS_DIR = "subsegments"
        const val SUB_SEGMENT_FILE_PREFIX = "SubSegment"

        const val SEGMENTS_DIR = "segments"
        const val SEGMENT_FILE_PREFIX = "Segment"
        const val SEGMENT_FILE_EXTENSION = ".aac"
    }

    suspend fun joinSubSegmentFiles(files : List<File>) : File?
}