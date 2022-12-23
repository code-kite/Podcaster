package com.codebox.podcaster.recorder.segmentrecorder.base

import com.codebox.podcaster.recorder.base.Recorder

/**
 * Created by Codebox on 02/03/21
 */
interface SegmentRecorder : Recorder {

    class RecordingNotStartedException(msg: String = "Try calling ${Recorder::startRecording.name} method first") :
        RuntimeException(msg)

}