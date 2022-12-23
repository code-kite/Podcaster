package com.codebox.podcaster.storage.disk.subsegmentstorage

import com.codebox.podcaster.storage.disk.base.DiskStorage
import java.io.File

/**
 * Created by Codebox on 05/03/21
 */
interface SubSegmentDiskStorage : DiskStorage {

    companion object {
        const val SUB_SEGMENTS_DIR = "subsegments"
        const val SUB_SEGMENT_FILE_PREFIX = "SubSegment"
        const val SUB_SEGMENT_FILE_EXTENSION = ".aac"
    }


    suspend fun createNewFile(): File?
}