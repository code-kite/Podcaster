package com.codebox.podcaster.modules.segment

import com.codebox.podcaster.recorder.base.Recorder
import com.codebox.podcaster.recorder.segmentrecorder.SegmentRecorderImpl
import com.codebox.podcaster.recorder.segmentrecorder.SegmentRecordingManagerImpl
import com.codebox.podcaster.recorder.segmentrecorder.base.SegmentRecordingManager
import com.codebox.podcaster.repo.SegmentRepository
import com.codebox.podcaster.repo.SegmentRepositoryImpl
import com.codebox.podcaster.storage.disk.segmentstorage.SegmentDiskStorage
import com.codebox.podcaster.storage.disk.segmentstorage.SegmentStorageImpl
import com.codebox.podcaster.storage.disk.subsegmentstorage.SubSegmentDiskStorage
import com.codebox.podcaster.storage.disk.subsegmentstorage.SubSegmentStorageImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent

/**
 * Created by Codebox on 08/03/21
 */
@Module
@InstallIn(ServiceComponent::class)
abstract class SegmentModule {
    @Binds
    abstract fun bindSegmentDiskStorage(segmentStorageImpl: SegmentStorageImpl): SegmentDiskStorage

    @Binds
    abstract fun bindSegmentRepository(segmentRepositoryImpl: SegmentRepositoryImpl): SegmentRepository

    @Binds
    abstract fun bindSubSegmentDiskStorage(subSegmentStorageImpl: SubSegmentStorageImpl): SubSegmentDiskStorage

    @Binds
    abstract fun bindSegmentRecorder(segmentRecorderImpl: SegmentRecorderImpl): Recorder

    @Binds
    abstract fun bindSegmentRecordingManager(segmentRecordingManagerImpl: SegmentRecordingManagerImpl): SegmentRecordingManager
}