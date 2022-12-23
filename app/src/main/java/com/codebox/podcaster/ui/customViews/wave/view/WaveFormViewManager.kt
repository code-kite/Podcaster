package com.codebox.podcaster.ui.customViews.wave.view

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.codebox.podcaster.R
import com.codebox.podcaster.storage.db.app.segment.Flag
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Created by Codebox on 19/05/21
 */
class WaveFormViewManager(
    private val mWaveformView: WaveformView,
    private val mPlayButton: FloatingActionButton,
    private val mStartMarker: MarkerView,
    private val mEndMarker: MarkerView,
    private val mPlaybackTextView: TextView,
    private val markStartButton: TextView?,
    private val markEndButton: TextView?,
    //private val mFilename: String,
    private val soundFile: SoundFile,
    private val resources: Resources,
) : WaveformView.WaveformListener, MarkerView.MarkerListener,
    ViewTreeObserver.OnGlobalLayoutListener {

    private var isInitialised: Boolean = false
    private val context = mWaveformView.context
    private lateinit var mFile: File
    private var mLastDisplayedEndPos: Int = 0
    private var mLastDisplayedStartPos: Int = 0
    private lateinit var mLoadSoundFileThread: Thread
    private var mTouchInitialEndPos: Int = 0
    private var mTouchInitialStartPos: Int = 0
    private var mKeyDown: Boolean = false
    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private var mPlayStartMsec: Int = 0
    private var mMarkerBottomOffset: Int = 0
    private var mMarkerTopOffset: Int = 0
    private var mMarkerRightInset: Int = 0
    private var mDensity = resources.displayMetrics.density
    private var mMarkerLeftInset: Int = 0
    private var mEndVisible: Boolean = false
    private var mStartVisible: Boolean = false
    private var mEndPos: Int = -1
    private var mStartPos: Int = -1
    private var mPlayEndMsec: Int = 0
    private var mWidth: Int = 0
    private var mOffsetGoal: Int = 0
    private var mPlayer: SamplePlayer? = null
    private var mIsPlaying: Boolean = false
    private var mMaxPos: Int = 0
    private var mWaveformTouchStartMsec: Long = 0
    private var mFlingVelocity: Int = 0;
    private var mOffset: Int = 0
    private var mTouchInitialOffset: Int = 0
    private var mTouchStart: Float = 0f
    private var mTouchDragging: Boolean = false
    private var mLoadingLastUpdateTime: Long = 0
    private var mProgressDialog: ProgressDialog? = null
    private var mLoadingKeepGoing = false
    //private val mPlaybackLiveData = MutableLiveData<Int>()

    private val mPlayListener = View.OnClickListener { onPlay(mStartPos) }

    private val mMarkStartListener = View.OnClickListener {
        if (mIsPlaying) {
            mStartPos = mWaveformView.millisecsToPixels(
                mPlayer!!.currentPosition
            )
            updateDisplay()
        }
    }

    private val mMarkEndListener = View.OnClickListener {
        if (mIsPlaying) {
            mEndPos = mWaveformView.millisecsToPixels(
                mPlayer!!.currentPosition
            )
            updateDisplay()
            handlePause()
        }
    }

    companion object {
        private const val TAG = "WaveFormViewManager"
    }


    private val progressListener: SoundFile.ProgressListener =
        SoundFile.ProgressListener { fractionComplete ->

            Log.d(TAG, "ProgressListener : $fractionComplete")

            val now: Long = getCurrentTime()
            if (now - mLoadingLastUpdateTime > 100) {

                Log.d(TAG, ":  $fractionComplete")

                mLoadingLastUpdateTime = now
            }
            //mLoadingKeepGoing
            true
        }

    private val mTimerRunnable: Runnable = object : Runnable {
        override fun run() {
            // Updating an EditText is slow on Android.  Make sure
            // we only do the update if the text has actually changed.

            if (mStartPos != mLastDisplayedStartPos) {
                val startTime = formatTime(mStartPos)
                mStartMarker.text = startTime
                /*if (!mStartText.hasFocus()) {
                    mStartText.text = startTime
                }*/
                mLastDisplayedStartPos = mStartPos
            }

            if (mEndPos != mLastDisplayedEndPos) {
                val endTime = formatTime(mEndPos)
                mEndMarker.text = endTime
                /*if (!mEndText.hasFocus()) {
                    mEndText.text = endTime
                }*/
                mLastDisplayedStartPos = mEndPos
            }

            mHandler.postDelayed(this, 100)
        }
    }

    //private val soundFile = SoundFile.create(mFilename, progressListener)

    init {

        if (mStartMarker.viewTreeObserver.isAlive) {
            mStartMarker.viewTreeObserver.addOnGlobalLayoutListener(this)
        }

    }

    private fun initialiseView() {

        preLoadSetup()
        initObservers()
        initialisePointers()

        initStartEndViews()

        initPlaybackButtons()

        enableDisableButtons()
        initWaveFormView()
        resetPositions()
        initMarkers()


        updateDisplay()



        mHandler = Handler(Looper.getMainLooper())
        mHandler.postDelayed(mTimerRunnable, 100)

        loadFromFile()

        isInitialised = true
    }

    private fun initObservers() {
        //mPlaybackLiveData.observe()
    }

    private fun resetPositions() {
        mStartPos = mWaveformView.secondsToPixels(0.0)
        mEndPos = mWaveformView.secondsToPixels(15.0)
    }

    private fun preLoadSetup() {
        mPlayer = null
        mIsPlaying = false

        mProgressDialog = null

        //mLoadSoundFileThread = null

        mKeyDown = false
    }

    private fun initPlaybackButtons() {
        mPlayButton.setOnClickListener(mPlayListener)
    }

    private fun initStartEndViews() {
        /*mStartText.addTextChangedListener(mTextWatcher)
        mEndText.addTextChangedListener(mTextWatcher)*/

        /*mStartMarker.addTextChangedListener(mTextWatcher)
        mEndMarker.addTextChangedListener(mTextWatcher)*/

        markStartButton?.setOnClickListener(mMarkStartListener)
        markEndButton?.setOnClickListener(mMarkEndListener)
    }

    private fun initialisePointers() {
        mMarkerLeftInset = (25 * mDensity).toInt()
        mMarkerRightInset = (26 * mDensity).toInt()
        mMarkerTopOffset = (30 * mDensity).toInt()
        mMarkerBottomOffset = (20 * mDensity).toInt()

        mMaxPos = 0
        mLastDisplayedStartPos = -1
        mLastDisplayedEndPos = -1
    }

    private fun initMarkers() {

        mStartMarker.setListener(this)
        mStartMarker.alpha = 1f
        mStartMarker.isFocusable = true
        mStartMarker.isFocusableInTouchMode = true
        mStartVisible = false


        mEndMarker.setListener(this)
        mEndMarker.alpha = 1f
        mEndMarker.isFocusable = true
        mEndMarker.isFocusableInTouchMode = true
        mEndVisible = false
    }

    private fun initWaveFormView() {

        val flagList = mutableListOf(Flag(-1, 10, 0), Flag(-1, 20, 0), Flag(-1, 30, 0))

        mWaveformView.setFlags(flagList)
        mWaveformView.setListener(this)

        mWaveformView.setSoundFile(soundFile)

        mWaveformView.recomputeHeights(mDensity)

        mMaxPos = mWaveformView.maxPos()

    }

    private fun getCurrentTime(): Long {
        return System.nanoTime() / 1000000
    }


    override fun waveformTouchStart(x: Float) {
        mTouchDragging = true
        mTouchStart = x
        mTouchInitialOffset = mOffset
        mFlingVelocity = 0
        mWaveformTouchStartMsec = getCurrentTime()
    }

    override fun waveformTouchMove(x: Float) {
        mOffset = trap((mTouchInitialOffset + (mTouchStart - x)).toInt())
        updateDisplay()
    }

    override fun waveformTouchEnd() {
        mTouchDragging = false
        mOffsetGoal = mOffset

        val elapsedMsec = getCurrentTime() - mWaveformTouchStartMsec
        if (elapsedMsec < 300) {
            if (mIsPlaying) {
                val seekMsec = mWaveformView.pixelsToMillisecs(
                    (mTouchStart + mOffset).toInt()
                )
                if (seekMsec >= mPlayStartMsec &&
                    seekMsec < mPlayEndMsec
                ) {
                    mPlayer!!.seekTo(seekMsec)
                } else {
                    handlePause()
                }
            } else {
                onPlay((mTouchStart + mOffset).toInt())
            }
        }
    }

    override fun waveformFling(x: Float) {
        mTouchDragging = false
        mOffsetGoal = mOffset
        mFlingVelocity = (-x).toInt()
        updateDisplay()
    }

    override fun waveformDraw() {
        mWidth = mWaveformView.measuredWidth
        if (mOffsetGoal != mOffset && !mKeyDown) updateDisplay() else if (mIsPlaying) {
            updateDisplay()
        } else if (mFlingVelocity != 0) {
            updateDisplay()
        }
    }

    override fun waveformZoomIn() {
        mWaveformView.zoomIn()
        mStartPos = mWaveformView.start
        mEndPos = mWaveformView.end
        mMaxPos = mWaveformView.maxPos()
        mOffset = mWaveformView.offset
        mOffsetGoal = mOffset
        updateDisplay()
    }

    override fun waveformZoomOut() {
        mWaveformView.zoomOut()
        mStartPos = mWaveformView.start
        mEndPos = mWaveformView.end
        mMaxPos = mWaveformView.maxPos()
        mOffset = mWaveformView.offset
        mOffsetGoal = mOffset
        updateDisplay()
    }

    private fun trap(pos: Int): Int {
        if (pos < 0) return 0
        return if (pos > mMaxPos) mMaxPos else pos
    }

    @Synchronized
    private fun updateDisplay() {
        if (mIsPlaying) {
            val now: Int = mPlayer?.currentPosition!!
            val frames: Int = mWaveformView.millisecsToPixels(now)
            mWaveformView.setPlayback(frames)

            mPlaybackTextView.setText(formatTime(frames))

            setOffsetGoalNoUpdate(frames - mWidth / 2)
            if (now >= mPlayEndMsec) {
                handlePause()
            }
        }
        if (!mTouchDragging) {
            var offsetDelta: Int
            if (mFlingVelocity != 0) {
                offsetDelta = mFlingVelocity / 30
                if (mFlingVelocity > 80) {
                    mFlingVelocity -= 80
                } else if (mFlingVelocity < -80) {
                    mFlingVelocity += 80
                } else {
                    mFlingVelocity = 0
                }
                mOffset += offsetDelta
                if (mOffset + mWidth / 2 > mMaxPos) {
                    mOffset = mMaxPos - mWidth / 2
                    mFlingVelocity = 0
                }
                if (mOffset < 0) {
                    mOffset = 0
                    mFlingVelocity = 0
                }
                mOffsetGoal = mOffset
            } else {
                offsetDelta = mOffsetGoal - mOffset
                if (offsetDelta > 10) offsetDelta =
                    offsetDelta / 10 else if (offsetDelta > 0) offsetDelta =
                    1 else if (offsetDelta < -10) offsetDelta =
                    offsetDelta / 10 else if (offsetDelta < 0) offsetDelta = -1 else offsetDelta = 0
                mOffset += offsetDelta
            }
        }
        mWaveformView.setParameters(mStartPos, mEndPos, mOffset)
        mWaveformView.invalidate()


        var startX: Int = mStartPos - mOffset - mMarkerLeftInset


        if (startX + mStartMarker.getWidth() >= 0) {
            if (!mStartVisible) {
                // Delay this to avoid flicker
                mHandler.postDelayed(Runnable {
                    mStartVisible = true
                    mStartMarker.setAlpha(1f)
                }, 0)
            }
        } else {
            if (mStartVisible) {
                mStartMarker.setAlpha(0f)
                mStartVisible = false
            }
            startX = 0
        }
        var endX: Int = mEndPos - mOffset - mEndMarker.getWidth() + mMarkerRightInset


        if (endX + mEndMarker.getWidth() >= 0) {
            if (!mEndVisible) {
                // Delay this to avoid flicker
                mHandler.postDelayed(Runnable {
                    mEndVisible = true
                    mEndMarker.setAlpha(1f)
                }, 0)
            }
        } else {
            if (mEndVisible) {
                Log.d(TAG, "updateDisplay: end Marker alpha 0")
                mEndMarker.setAlpha(0f)
                mEndVisible = false
            }
            endX = 0
        }
        var params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(
            startX,
            mMarkerTopOffset,
            -mStartMarker.getWidth(),
            -mStartMarker.getHeight()
        )
        mStartMarker.setLayoutParams(params)
        params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(
            endX,
            mWaveformView.getMeasuredHeight() - mEndMarker.getHeight() - mMarkerBottomOffset,
            -mStartMarker.getWidth(),
            -mStartMarker.getHeight()
        )
        mEndMarker.setLayoutParams(params)
    }

    private fun loadFromFile() {

        mPlayer = SamplePlayer(soundFile)

        /*mFile = File(mFilename)

        mLoadingLastUpdateTime = getCurrentTime()
        mLoadingKeepGoing = true
        mProgressDialog = ProgressDialog(context)
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        mProgressDialog?.setTitle("Loading!")
        mProgressDialog!!.setCancelable(true)
        mProgressDialog!!.setOnCancelListener {
            mLoadingKeepGoing = false

        }
        mProgressDialog!!.show()
        val listener: SoundFile.ProgressListener = object : SoundFile.ProgressListener {
            override fun reportProgress(fractionComplete: Double): Boolean {
                val now = getCurrentTime()
                if (now - mLoadingLastUpdateTime > 100) {
                    mProgressDialog!!.progress = (mProgressDialog!!.max * fractionComplete).toInt()
                    mLoadingLastUpdateTime = now
                }
                return mLoadingKeepGoing
            }
        }

        // Load the sound file in a background thread
        mLoadSoundFileThread = object : Thread() {
            override fun run() {
                try {
                    //mSoundFile = SoundFile.create(mFile.getAbsolutePath(), listener)
                    if (soundFile == null) {
                        mProgressDialog!!.dismiss()
                        val name: String = mFile.getName().toLowerCase()
                        val components = name.split("\\.".toRegex()).toTypedArray()
                        val err: String
                        err = if (components.size < 2) {
                            //resources.getString(R.string.no_extension_error)
                            "no_extension_error"
                        } else {
                            *//*resources.getString(
                                R.string.bad_extension_error
                            ).toString() + " " +
                                    components[components.size - 1]*//*
                            "bad_extension_error ${components.size - 1}"
                        }
                        val runnable =
                            Runnable { showFinalAlert(java.lang.Exception(), err) }
                        mHandler.post(runnable)
                        return
                    }
                    mPlayer = SamplePlayer(soundFile)
                } catch (e: java.lang.Exception) {
                    mProgressDialog!!.dismiss()
                    e.printStackTrace()
                    //mInfoContent = e.toString()
                    //runOnUiThread(Runnable { mInfo.setText(mInfoContent) })
                    //val runnable = Runnable { showFinalAlert(e, getResources().getText(R.string.read_error)) }
                    val runnable = Runnable { showFinalAlert(e, "read_error") }
                    mHandler.post(runnable)
                    return
                }
                mProgressDialog!!.dismiss()
                *//*if (mLoadingKeepGoing) {
                    val runnable = Runnable { finishOpeningSoundFile() }
                    mHandler.post(runnable)
                } else if (mFinishActivity) {
                    this@RingdroidEditActivity.finish()
                }*//*
            }
        }
        mLoadSoundFileThread.start()*/
    }


    private fun setOffsetGoalNoUpdate(offset: Int) {
        if (mTouchDragging) {
            return
        }
        mOffsetGoal = offset
        if (mOffsetGoal + mWidth / 2 > mMaxPos) mOffsetGoal = mMaxPos - mWidth / 2
        if (mOffsetGoal < 0) mOffsetGoal = 0
    }

    @Synchronized
    private fun handlePause() {
        if (mPlayer != null && mPlayer!!.isPlaying) {
            mPlayer!!.pause()
        }
        mWaveformView.setPlayback(-1)
        mIsPlaying = false
        enableDisableButtons()
    }

    private fun enableDisableButtons() {
        if (mIsPlaying) {
            mPlayButton.setImageResource(R.drawable.ic_pause)
            //mPlayButton.setContentDescription(getResources().getText(R.string.stop))
        } else {
            mPlayButton.setImageResource(R.drawable.ic_play)
            //mPlayButton.setContentDescription(getResources().getText(R.string.play))
        }
    }

    override fun markerTouchStart(marker: MarkerView?, x: Float) {
        mTouchDragging = true
        mTouchStart = x

        Log.d(
            TAG,
            "markerTouchStart: mStartPos : $mStartPos :: waveSelectionStart: ${mWaveformView.start}"
        )
        Log.d(TAG, "markerTouchStart: mEndPos: $mEndPos :: waveSelectionEnd: ${mWaveformView.end}")



        mTouchInitialStartPos = mStartPos
        mTouchInitialEndPos = mEndPos
    }

    override fun markerTouchMove(marker: MarkerView?, x: Float) {
        val delta: Float = x - mTouchStart

        if (marker === mStartMarker) {
            mStartPos = trap((mTouchInitialStartPos + delta).toInt())
            mEndPos = trap((mTouchInitialEndPos + delta).toInt())
        } else {
            mEndPos = trap((mTouchInitialEndPos + delta).toInt())
            if (mEndPos < mStartPos) mEndPos = mStartPos
        }

        updateDisplay()
    }

    override fun markerTouchEnd(marker: MarkerView?) {
        mTouchDragging = false
        if (marker === mStartMarker) {
            setOffsetGoalStart()
        } else {
            setOffsetGoalEnd()
        }
    }

    override fun markerFocus(marker: MarkerView?) {
        mKeyDown = false
        if (marker === mStartMarker) {
            setOffsetGoalStartNoUpdate()
        } else {
            setOffsetGoalEndNoUpdate()
        }

        // Delay updaing the display because if this focus was in
        // response to a touch event, we want to receive the touch
        // event too before updating the display.

        // Delay updaing the display because if this focus was in
        // response to a touch event, we want to receive the touch
        // event too before updating the display.
        mHandler.postDelayed({ updateDisplay() }, 100)
    }

    override fun markerLeft(marker: MarkerView?, velocity: Int) {
        mKeyDown = true

        if (marker === mStartMarker) {
            val saveStart = mStartPos
            mStartPos = trap(mStartPos - velocity)
            mEndPos = trap(mEndPos - (saveStart - mStartPos))
            setOffsetGoalStart()
        }

        if (marker === mEndMarker) {
            if (mEndPos == mStartPos) {
                mStartPos = trap(mStartPos - velocity)
                mEndPos = mStartPos
            } else {
                mEndPos = trap(mEndPos - velocity)
            }
            setOffsetGoalEnd()
        }

        updateDisplay()
    }

    override fun markerRight(marker: MarkerView?, velocity: Int) {
        mKeyDown = true

        if (marker === mStartMarker) {
            val saveStart = mStartPos
            mStartPos += velocity
            if (mStartPos > mMaxPos) mStartPos = mMaxPos
            mEndPos += mStartPos - saveStart
            if (mEndPos > mMaxPos) mEndPos = mMaxPos
            setOffsetGoalStart()
        }

        if (marker === mEndMarker) {
            mEndPos += velocity
            if (mEndPos > mMaxPos) mEndPos = mMaxPos
            setOffsetGoalEnd()
        }

        updateDisplay()
    }

    override fun markerEnter(marker: MarkerView?) {
        TODO("Not yet implemented")
    }

    override fun markerKeyUp() {
        mKeyDown = false
        updateDisplay()
    }

    override fun markerDraw() {

    }


    private fun setOffsetGoalStartNoUpdate() {
        setOffsetGoalNoUpdate(mStartPos - mWidth / 2)
    }

    private fun setOffsetGoalEndNoUpdate() {
        setOffsetGoalNoUpdate(mEndPos - mWidth / 2)
    }


    private fun setOffsetGoalStart() {
        setOffsetGoal(mStartPos - mWidth / 2)
    }

    private fun setOffsetGoalEnd() {
        setOffsetGoal(mEndPos - mWidth / 2)
    }

    private fun setOffsetGoal(offset: Int) {
        setOffsetGoalNoUpdate(offset)
        updateDisplay()
    }


    @Synchronized
    private fun onPlay(startPosition: Int) {
        if (mIsPlaying) {
            handlePause()
            return
        }
        if (mPlayer == null) {
            // Not initialized yet
            return
        }
        try {
            mPlayStartMsec = mWaveformView.pixelsToMillisecs(startPosition)
            mPlayEndMsec = if (startPosition < mStartPos) {
                mWaveformView.pixelsToMillisecs(mStartPos)
            } else if (startPosition > mEndPos) {
                mWaveformView.pixelsToMillisecs(mMaxPos)
            } else {
                mWaveformView.pixelsToMillisecs(mEndPos)
            }
            mPlayer!!.setOnCompletionListener { handlePause() }
            mIsPlaying = true
            mPlayer!!.seekTo(mPlayStartMsec)
            mPlayer!!.start()
            updateDisplay()
            enableDisableButtons()
        } catch (e: Exception) {
            val msg = "Unable to play this media file"
            showFinalAlert(e, msg)
            return
        }
    }

    /**
     * Show a "final" alert dialog that will exit the activity
     * after the user clicks on the OK button.  If an exception
     * is passed, it's assumed to be an error condition, and the
     * dialog is presented as an error, and the stack trace is
     * logged.  If there's no exception, it's a success message.
     */
    private fun showFinalAlert(e: java.lang.Exception?, message: CharSequence) {
        val title: CharSequence
        if (e != null) {
            Log.e("Ringdroid", "Error: $message")
            Log.e("Ringdroid", getStackTrace(e))
            //title = resources.getText(R.string.alert_title_failure)
            title = "Error"
            //setResult(Activity.RESULT_CANCELED, Intent())
        } else {
            Log.v("Ringdroid", "Success: $message")
            //title = resources.getText(R.string.alert_title_success)
            title = "Success"
        }
        AlertDialog.Builder(mWaveformView.context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                "OK",
                DialogInterface.OnClickListener { dialog, whichButton ->
                    Toast.makeText(
                        mWaveformView.context,
                        "Button Press",
                        Toast.LENGTH_SHORT
                    ).show()
                })
            .setCancelable(false)
            .show()
    }

    private fun getStackTrace(e: java.lang.Exception): String {
        val writer = StringWriter()
        e.printStackTrace(PrintWriter(writer))
        return writer.toString()
    }

    private fun formatTime(pixels: Int): String? {
        return if (mWaveformView.isInitialized) {
            formatDecimal(mWaveformView.pixelsToSeconds(pixels))
        } else {
            ""
        }
    }

    private fun formatDecimal(x: Double): String? {
        var xWhole = x.toInt()
        var xFrac = (100 * (x - xWhole) + 0.5).toInt()
        if (xFrac >= 100) {
            xWhole++ //Round up
            xFrac -= 100 //Now we need the remainder after the round up
            if (xFrac < 10) {
                xFrac *= 10 //we need a fraction that is 2 digits long
            }
        }
        return if (xFrac < 10) "$xWhole.0$xFrac" else "$xWhole.$xFrac"
    }

    override fun onGlobalLayout() {
        if (!isInitialised) {
            if (mStartMarker.viewTreeObserver.isAlive) {
                mStartMarker.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
            initialiseView()
        }

    }
}