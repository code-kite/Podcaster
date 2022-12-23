package com.codebox

import com.codebox.podcaster.recorder.segmentrecorder.SegmentRecordingManagerImpl
import com.codebox.podcaster.recorder.segmentrecorder.base.SegmentRecorder
import com.codebox.podcaster.recorder.segmentrecorder.base.SegmentRecordingManager
import com.codebox.podcaster.repo.SegmentRepository
import com.codebox.podcaster.storage.disk.segmentstorage.SegmentDiskStorage
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Created by Codebox on 03/03/21
 */
class SegmentRecordingManagerImplTest {

    private lateinit var storage: SegmentDiskStorage
    private lateinit var repo: SegmentRepository
    private lateinit var manager: SegmentRecordingManagerImpl
    private lateinit var recorder: SegmentRecorder


    @Before
    fun setup() {

        storage = mock()
        recorder = mock()
        repo = mock()

        manager = SegmentRecordingManagerImpl(repo, recorder)

    }

    private fun SegmentRecordingManagerImpl(
        repository: SegmentRepository,
        recorder: SegmentRecorder
    ): SegmentRecordingManagerImpl {
        TODO("Not yet implemented")
    }


    @Test
    suspend fun startRecording_StartsRecorder() {
        //manager.startRecording()

        verify(recorder).startRecording()
    }

    @Test(expected = SegmentRecordingManager.IllegalOperationException::class)
    fun addFlag_WithoutStartRecording_throwException() {
        //manager.addFlag()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun onPauseRecording_StopRecorder() = runBlockingTest {
        manager.startRecording()
        manager.pauseRecording()
        verify(recorder).stopRecording()
    }

    @Test
    fun onCreateFinalSegment_() = runBlockingTest{
        manager.startRecording()
        delay(1000)
        manager.addFlag()
        delay(1000)
        manager.addFlag()

        whenever(recorder.stopRecording()).thenReturn(File(""))

        manager.pauseRecording()

        delay(1000)
        manager.resumeRecording()
        delay(1000)
        manager.addFlag()
        delay(1000)
        manager.addFlag()
        manager.pauseRecording()


        val file = File("")
        whenever(repo.generateSegmentFile(any())).thenReturn(file)
        //whenever(repo.persistSegment(any())).thenReturn(Segment(""))

        manager.generateSegment()



        verify(repo).generateSegmentFile(any())
        verify(repo).persistSegment(any())
        verify(repo).persistFlagsOfSegment(any())
    }

}