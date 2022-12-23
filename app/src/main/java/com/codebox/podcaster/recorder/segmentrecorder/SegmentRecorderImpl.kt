package com.codebox.podcaster.recorder.segmentrecorder

import android.media.MediaRecorder
import com.codebox.podcaster.recorder.segmentrecorder.base.SegmentRecorder
import com.codebox.podcaster.storage.disk.subsegmentstorage.SubSegmentDiskStorage
import java.io.File
import javax.inject.Inject

/**
 * Created by Codebox on 01/03/21
 */
class SegmentRecorderImpl @Inject constructor(
    val storage: SubSegmentDiskStorage
) : SegmentRecorder {

    private var recorder: MediaRecorder? = null
    private lateinit var outputFile: File

    private val TAG = SegmentRecorderImpl::class.java.simpleName

    override suspend fun startRecording(): Boolean {

        fetchNewFileFromStorage()

        if (isOutputFileAvailable()) {
            prepareRecorder()
            initiateRecording()
            return true
        }
        return false
    }

    override fun stopRecording(): File {
        checkRecorderState()

        recorder?.stop()
        destroyRecorder()
        return outputFile
    }

    private fun destroyRecorder() {
        recorder?.reset()
        recorder = null
    }

    private fun checkRecorderState() {
        if (recorder == null)
            throw SegmentRecorder.RecordingNotStartedException()
    }

    private fun isOutputFileAvailable(): Boolean {
        return ::outputFile.isInitialized
    }

    private fun initiateRecording() {
        recorder?.start()
    }

    private suspend fun fetchNewFileFromStorage() {
        val file = storage.createNewFile()
        if (file != null)
            this.outputFile = file
    }

    private fun prepareRecorder() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.DEFAULT)// UNPROCESSED available above 24. See if it can be used somehow.
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setOutputFile(outputFile.absolutePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC) // Confirm with others
            setOnInfoListener(object : MediaRecorder.OnInfoListener {
                override fun onInfo(mr: MediaRecorder?, what: Int, extra: Int) {
                }

            })
            setOnErrorListener(object : MediaRecorder.OnErrorListener {
                override fun onError(mr: MediaRecorder?, what: Int, extra: Int) {
                }

            })
            prepare()
        }
    }
}