package com.codebox.podcaster.storage.disk.segmentstorage

import android.os.Environment
import com.codebox.podcaster.modules.dispatcher.DispatcherModule
import com.codebox.podcaster.storage.disk.base.FileUtil
import com.codebox.podcaster.storage.disk.segmentstorage.SegmentDiskStorage.Companion.SEGMENTS_DIR
import com.codebox.podcaster.storage.disk.segmentstorage.SegmentDiskStorage.Companion.SEGMENT_FILE_EXTENSION
import com.codebox.podcaster.storage.disk.segmentstorage.SegmentDiskStorage.Companion.SEGMENT_FILE_PREFIX
import com.codebox.podcaster.util.AudioUtil
import com.codebox.podcaster.util.DateTimeUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


/**
 * Created by Codebox on 01/03/21
 */
class SegmentStorageImpl @Inject constructor(
    private val dateTimeUtil: DateTimeUtil,
    private val audioUtil: AudioUtil,
    private val fileUtil: FileUtil,
    @DispatcherModule.DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) :
    SegmentDiskStorage {


    companion object {
        private const val TAG = "SegmentStorageImpl"
    }


    override suspend fun joinSubSegmentFiles(files: List<File>): File? {

        return withContext(defaultDispatcher) scope@{
            val segmentDirectory = getSegmentsDir() ?: return@scope null
            val outputFilePath = getNewSegmentFilePath(segmentDirectory)
            val isSuccessful = audioUtil.concatenateFiles(files, outputFilePath)

            if (isSuccessful) File(outputFilePath) else null
        }

    }


    private fun getNewSegmentFilePath(directory: File): String {
        val fileName = createSegmentFileName()
        val draftFile = File(directory, fileName)
        return draftFile.absolutePath
    }

    private suspend fun getSegmentsDir(): File? {
        return fileUtil.getFolderInSystem(Environment.DIRECTORY_PODCASTS, SEGMENTS_DIR)
    }

    private fun createSegmentFileName(): String {
        return "${SEGMENT_FILE_PREFIX}_${dateTimeUtil.getTimeStamp(DateTimeUtil.DateTimePattern.PATTERN_1)}$SEGMENT_FILE_EXTENSION"
    }

}