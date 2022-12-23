package com.codebox.podcaster.recorder.base

import java.io.File

interface Recorder {

    suspend fun startRecording(): Boolean
    fun stopRecording(): File?

}
