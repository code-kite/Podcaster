package com.codebox.podcaster.recorder.segmentrecorder

import androidx.lifecycle.LiveData
import com.codebox.podcaster.recorder.base.Recorder
import com.codebox.podcaster.recorder.segmentrecorder.base.SegmentRecordingManager
import com.codebox.podcaster.repo.SegmentRepository
import com.codebox.podcaster.storage.db.app.segment.Flag
import com.codebox.podcaster.storage.db.app.segment.Segment
import com.codebox.podcaster.storage.db.app.segment.SegmentWithFlags
import com.codebox.podcaster.storage.db.app.segment.SubSegment
import com.codebox.podcaster.util.Stopwatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * Created by Codebox on 03/03/21
 */
class SegmentRecordingManagerImpl @Inject constructor(
    private val repository: SegmentRepository,
    private val recorder: Recorder,
    private val stopwatch: Stopwatch
) : SegmentRecordingManager {

    private val subSegments = mutableListOf<SubSegment>()
    private var currentSubSegment: SubSegment? = null


    override suspend fun startRecording(): Boolean {

        if (currentSubSegment != null)
            startRecordingFailure()

        
        stopwatch.start()

        initialiseCurrentSubSegment()
        return recorder.startRecording()
    }


    override suspend fun resumeRecording(): Boolean {
        stopwatch.resume()
        return startRecording()
    }

    override suspend fun pauseRecording(): Boolean {

        stopwatch.pause()
        val subSegment = recorder.stopRecording() ?: return false
        addCurrentSubSegmentToSubSegments(subSegment)
        destroyCurrentSubSegment()
        return true

    }

    override suspend fun addFlag() {
        currentSubSegment?.flags?.add(System.currentTimeMillis())
            ?: addFlagFailure()
    }

    override suspend fun generateSegment(): SegmentWithFlags? {

        stopwatch.stop()

        val subSegmentFiles =
            withContext(Dispatchers.Default) { subSegments.map { it.subSegmentFile } }
        val segmentFile =
            repository.generateSegmentFile(subSegmentFiles) ?: return null
        val segment = persistSegment(makeSegmentObject(segmentFile)) ?: return null
        val flags = persistFlagsOfSegment(segment.id)
        deleteSubSegmentFiles()
        return SegmentWithFlags(segment, flags)
    }

    override fun getTickData(): LiveData<Long> {
        return stopwatch.getTickLiveData()
    }

    private suspend fun persistFlagsOfSegment(segmentId: Long): List<Flag> {
        val flags = parseFlagsFromSubSegments(segmentId)
        repository.persistFlagsOfSegment(flags)
        return flags
    }

    private suspend fun persistSegment(segment: Segment): Segment? {
        val id = repository.persistSegment(segment)
        if (id < 0)
            return null
        return segment.apply { this.id = id }
    }

    private fun makeSegmentObject(segmentFile: File): Segment {
        return Segment(segmentFile.absolutePath, calculateCompleteSegmentDuration(subSegments))
    }

    private fun calculateCompleteSegmentDuration(subSegments: List<SubSegment>): Long {
        var duration = 0L
        subSegments.forEach { duration += it.endTime - it.startTime }
        return duration
    }

    private suspend fun parseFlagsFromSubSegments(segmentId: Long): List<Flag> {

        return withContext(Dispatchers.Default) {

            val lastTime = subSegments.get(0).startTime

            subSegments.flatMap {

                it.flags.map {
                    Flag(segmentId, ((it - lastTime) / 1000).toInt())
                }
            }

        }

    }

    suspend fun deleteSubSegmentFiles() {
        withContext(Dispatchers.Default) {
            subSegments.forEach {
                withContext(Dispatchers.IO) { it.subSegmentFile.delete() }
            }
        }
    }

    private fun initialiseCurrentSubSegment() {
        currentSubSegment = SubSegment().apply { startTime = System.currentTimeMillis() }
    }

    private fun destroyCurrentSubSegment() {
        currentSubSegment = null
    }

    private fun addCurrentSubSegmentToSubSegments(file: File) {
        currentSubSegment!!.endTime = System.currentTimeMillis()
        currentSubSegment!!.subSegmentFile = file
        subSegments.add(currentSubSegment!!)
    }
}