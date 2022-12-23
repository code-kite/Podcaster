package com.codebox.podcaster.repo

import com.codebox.podcaster.storage.db.app.segment.Flag
import com.codebox.podcaster.storage.db.app.segment.FlagDao
import com.codebox.podcaster.storage.db.app.segment.Segment
import com.codebox.podcaster.storage.db.app.segment.SegmentDao
import com.codebox.podcaster.storage.disk.segmentstorage.SegmentDiskStorage
import java.io.File
import javax.inject.Inject


/**
 * Created by Codebox on 02/03/21
 */
class SegmentRepositoryImpl @Inject constructor (
    private val segmentStorage: SegmentDiskStorage,
    private val segmentDao: SegmentDao,
    private val flagDao: FlagDao
) : SegmentRepository {

    override suspend fun generateSegmentFile(files: List<File>): File? {
        return segmentStorage.joinSubSegmentFiles(files)
    }

    override suspend fun persistSegment(segment: Segment): Long {
        return insertSegmentInDB(segment)
    }

    override suspend fun persistFlagsOfSegment(flags: List<Flag>) {
        flagDao.insertFlags(flags)
    }


    private suspend fun insertSegmentInDB(segment: Segment) =
        segmentDao.insertSegment(segment)

}