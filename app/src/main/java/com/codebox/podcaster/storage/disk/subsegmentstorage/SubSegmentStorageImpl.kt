package com.codebox.podcaster.storage.disk.subsegmentstorage

import android.os.Environment
import com.codebox.podcaster.storage.disk.base.FileUtil
import com.codebox.podcaster.storage.disk.segmentstorage.SegmentDiskStorage
import com.codebox.podcaster.storage.disk.subsegmentstorage.SubSegmentDiskStorage.Companion.SUB_SEGMENT_FILE_PREFIX
import com.codebox.podcaster.util.DateTimeUtil
import java.io.File
import javax.inject.Inject

/**
 * Created by Codebox on 05/03/21
 */
class SubSegmentStorageImpl @Inject constructor(
    private val fileUtil: FileUtil,
    private val dateTimeUtil: DateTimeUtil
) :
    SubSegmentDiskStorage {


    override suspend fun createNewFile(): File? {

        if (fileUtil.isExternalStorageAvailable()) {
            val storage = getSubSegmentsDir() ?: return null
            return createSubSegmentFile(storage)
        }
        return null
    }

    private fun createSubSegmentFile(directory: File): File? {
        val fileName = createSubSegmentFileName()
        val draftFile = File(directory, fileName)
        if (draftFile.createNewFile())
            return draftFile
        return null
    }

    private suspend fun getSubSegmentsDir(): File? {
        return fileUtil.getFolderInSystem(
            Environment.DIRECTORY_PODCASTS,
            SegmentDiskStorage.SUB_SEGMENTS_DIR
        )
    }

    private fun createSubSegmentFileName() =
        "${SUB_SEGMENT_FILE_PREFIX}_${dateTimeUtil.getTimeStamp(DateTimeUtil.DateTimePattern.PATTERN_1)}${SegmentDiskStorage.SEGMENT_FILE_EXTENSION}"

}