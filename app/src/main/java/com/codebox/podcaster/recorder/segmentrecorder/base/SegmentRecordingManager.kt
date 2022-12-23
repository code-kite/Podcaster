package com.codebox.podcaster.recorder.segmentrecorder.base

import androidx.lifecycle.LiveData
import com.codebox.podcaster.storage.db.app.segment.SegmentWithFlags

interface SegmentRecordingManager {
    suspend fun startRecording(): Boolean
    suspend fun resumeRecording(): Boolean
    suspend fun pauseRecording(): Boolean
    suspend fun addFlag()
    suspend fun generateSegment(): SegmentWithFlags?

    fun getTickData() : LiveData<Long>

    companion object{

        const val START_RECORDING_FAILURE = "Current Recording is under process."
        const val ADD_FLAG_FAILURE = "Recording is not started. Cannot add flag."

    }


    fun startRecordingFailure(){
        throw IllegalOperationException(START_RECORDING_FAILURE)
    }

    fun addFlagFailure(){
        throw IllegalOperationException(ADD_FLAG_FAILURE)
    }

    class IllegalOperationException(msg: String = "") : RuntimeException(msg)
}
