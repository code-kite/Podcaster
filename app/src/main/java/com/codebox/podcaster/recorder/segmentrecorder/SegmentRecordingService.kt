package com.codebox.podcaster.recorder.segmentrecorder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.codebox.podcaster.R
import com.codebox.podcaster.storage.db.app.segment.SegmentWithFlags
import com.codebox.podcaster.ui.main.MainActivity
import com.codebox.podcaster.util.DateTimeUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class SegmentRecordingService : Service() {

    companion object {
        const val TAG = "SegmentRecordingService"
        const val OPERATION_KEY = "operationKey"
        const val NO_OPERATION = -1
        const val START_RECORDING = 0
        const val PAUSE_RECORDING = 1
        const val RESUME_RECORDING = 2
        const val ADD_FLAG = 3
        const val GENERATE_SEGMENT = 5
    }

    private val uiScope = MainScope()


    private var recordingBinder = RecordingBinder()

    @Inject
    lateinit var recordingManager: SegmentRecordingManagerImpl


    @Inject
    lateinit var dateTimeUtil: DateTimeUtil

    var stateData = MutableLiveData<Int>()

    override fun onCreate() {
        super.onCreate()
        startRecordingService()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onBind(intent: Intent): IBinder {
        return recordingBinder
    }

    override fun onDestroy() {

        uiScope.cancel()

        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val operation = intent?.getIntExtra(OPERATION_KEY, NO_OPERATION) ?: NO_OPERATION

        when (operation) {
            START_RECORDING -> uiScope.launch { startRecording() }
            PAUSE_RECORDING -> uiScope.launch { pauseRecording() }
            RESUME_RECORDING -> uiScope.launch { resumeRecording() }
            GENERATE_SEGMENT -> uiScope.launch { generateSegment() }
            ADD_FLAG -> uiScope.launch { addFlag() }
            else -> updateRecordingNotification(operation)
        }

        return START_NOT_STICKY
    }

    fun getStopwatchData(): LiveData<String> {

        return Transformations.map(recordingManager.getTickData()) { dateTimeUtil.formatMillis(it) }
    }

    suspend fun startRecording(): Boolean {
        stateData.postValue(START_RECORDING)

        val result = recordingManager.startRecording()
        updateRecordingNotification(START_RECORDING)
        return result
    }

    suspend fun pauseRecording() {
        stateData.postValue(PAUSE_RECORDING)

        recordingManager.pauseRecording()
        updateRecordingNotification(PAUSE_RECORDING)
    }

    suspend fun resumeRecording() {
        stateData.postValue(RESUME_RECORDING)

        recordingManager.resumeRecording()
        updateRecordingNotification(RESUME_RECORDING)
    }

    suspend fun addFlag() {
        recordingManager.addFlag()
        updateRecordingNotification(ADD_FLAG)
    }

    suspend fun deleteAllRecordingsAndTerminate() {
        recordingManager.deleteSubSegmentFiles()
        terminateService()
    }

    suspend fun generateSegment(): SegmentWithFlags? {
        val segment = recordingManager.generateSegment()

        terminateService()

        return segment
    }

    suspend fun stopAndGenerateSegment() {
        val state = stateData.value
        when (state) {
            START_RECORDING, RESUME_RECORDING -> {
                pauseRecording()
            }
        }

        if (state != NO_OPERATION) {
            val segment = generateSegment()
        }
    }

    private fun terminateService() {
        stateData.postValue(NO_OPERATION)

        stopForeground(true)
        stopSelf()
    }


    override fun onTaskRemoved(rootIntent: Intent?) {

        uiScope.launch { stopAndGenerateSegment() }

        super.onTaskRemoved(rootIntent)
    }

    private fun startRecordingService() {
        val intent = Intent(this, SegmentRecordingService::class.java).apply {
            putExtra(OPERATION_KEY, NO_OPERATION)
        }

        startService(intent)
    }

    private fun updateRecordingNotification(operation: Int) {


        val channelId = createNotificationChannel()
        val notificationClickRequestCode = 1002
        val stopRecordingClickRequestCode = 1003

        val recordingIntent = Intent(this, MainActivity::class.java).apply {
            putExtra(OPERATION_KEY, operation)
        }
        val recordingPendingIntent =
            PendingIntent.getActivity(
                this,
                notificationClickRequestCode,
                recordingIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        /*val stopRecordingIntent = Intent(this, SegmentRecordingService::class.java).apply {
            putExtra(OPERATION_KEY, STOP_AND_GENERATE_SEGMENT)
        }*/

        /*val stopRecordingPendingIntent =
            PendingIntent.getService(this, stopRecordingClickRequestCode, stopRecordingIntent, 0)*/

        val notificationChannelId = "RecordingNotificationChannelId"

        val builder =
            NotificationCompat.Builder(this, notificationChannelId)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(recordingPendingIntent)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setChannelId(channelId)
                .setSound(Uri.EMPTY)


        when (operation) {
            START_RECORDING, RESUME_RECORDING -> {
                builder.setContentText("Recording...")
                //builder.addAction(-1, "Stop Recording", stopRecordingPendingIntent)
            }
            PAUSE_RECORDING -> {
                builder.setContentText("Recording Paused.")
            }
        }


        val notificationId = 1
        if (operation != NO_OPERATION)
            startForeground(notificationId, builder.build())
    }

    private fun createNotificationChannel(): String {
        val channelName = "Recording Channel"
        val channelDescription = "Controls notifications related to recording"
        val channelId = "com.codebox.podcaster.recording"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        return channelId
    }


    inner class RecordingBinder : Binder() {
        fun getService(): SegmentRecordingService = this@SegmentRecordingService
    }


}