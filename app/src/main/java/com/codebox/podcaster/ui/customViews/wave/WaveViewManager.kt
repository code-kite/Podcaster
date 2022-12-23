package com.codebox.podcaster.ui.customViews.wave

import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebox.podcaster.R
import com.codebox.podcaster.storage.db.app.segment.Flag
import com.codebox.podcaster.ui.recordingFlow.editing.WaveElement
import com.codebox.podcaster.ui.recordingFlow.editing.WaveFormAdapter
import kotlinx.coroutines.*


/**
 * Created by Codebox on 17/05/21
 */
class WaveViewManager(private val waveView: View) : AutoCloseable {

    private val context = waveView.context
    private val waveRecycler = waveView.findViewById<RecyclerView>(R.id.recyclerWave)
    private val flagRecycler = waveView.findViewById<RecyclerView>(R.id.recyclerFlag)
    private val slider = waveView.findViewById<View>(R.id.slider)

    private val waveViewScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        private const val TAG = "WaveViewManager"
    }

    init {
        val streaks = 5000
        val flags = 50

        initSlider()
        waveViewScope.launch { showWaveForm(streaks, flags) }
    }

    private var mPrevX = 0f

    private fun initSlider() {

        /*val manager = (waveRecycler.layoutManager as LinearLayoutManager)
        val pos = manager.findFirstVisibleItemPosition()
        val view = manager.findViewByPosition(pos);
        view?.x*/
        //TODO shorten width of recycler so that it can be reached by the pointer
        // Find a way to map pointer's x and y component to item position in recycler view
        // Move sliding loogic to other file



        slider.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                Log.d(TAG, "onTouch: ${event?.action}")

                when (event!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        mPrevX = event.x
                    }
                    MotionEvent.ACTION_MOVE -> {
                        v?.x = (event.rawX - mPrevX)
                    }
                }

                return true
            }

        })
    }


    private suspend fun showWaveForm(streaks: Int, flags: Int) {


        val ampsDef = waveViewScope.async { makeAmplitudeList(streaks) }
        val flagsDef = waveViewScope.async { makeFlagsList(flags, streaks) }

        val flagsList = flagsDef.await()
        val ampsList = ampsDef.await()

        val flagsForSecondDef =
            waveViewScope.async { makeWaveElementsListForSecond(flagsList, streaks) }

        val streaksForSecondDef = waveViewScope.async {
            makeStreaksForSecond(
                waveRecycler.layoutParams.height,
                ampsList
            )
        }

        showSecondWaveForm(streaksForSecondDef.await(), flagsForSecondDef.await())
    }


    private suspend fun makeAmplitudeList(size: Int): List<Int> {
        return withContext(Dispatchers.Default) {

            val amps = ArrayList<Int>()
            for (i in 0..size) {
                val amp = (0..100).random()
                amps.add(amp)
            }
            amps
        }
    }

    private suspend fun makeFlagsList(size: Int, duration: Int): List<Flag> {
        return withContext(Dispatchers.Default) {

            val flags = ArrayList<Flag>()
            for (i in 0 until size) {
                val flagAt = (0 until duration).random()
                flags.add(Flag(-1, flagAt))
            }

            flags.sortBy { it.secondsAfterRecording }

            flags
        }
    }

    private suspend fun makeWaveElementsListForSecond(
        flags: List<Flag>,
        streaks: Int
    ): ArrayList<WaveElement> {
        return withContext(Dispatchers.Default) {

            val waveElements = ArrayList<WaveElement>()

            var flagIndex = if (flags.isNotEmpty()) 0 else -1

            for (i in 0 until streaks) {

                if (flagIndex >= 0 && flagIndex < flags.size && i == flags[flagIndex].secondsAfterRecording) {
                    waveElements.add(WaveElement.WaveOnlyFlag)
                    flagIndex++;
                } else {
                    waveElements.add(WaveElement.WaveEmptyElement)
                }
            }
            waveElements
        }
    }

    private fun makeStreaksForSecond(
        parentHeight: Int,
        ampsList: List<Int>
    ): ArrayList<WaveElement> {
        val result = ArrayList<WaveElement>()
        for (amp in ampsList) {
            result.add(WaveElement.WaveStreak(parentHeight, amp))
        }
        return result
    }

    private fun showSecondWaveForm(
        streaks: ArrayList<WaveElement>,
        flags: ArrayList<WaveElement>
    ) {
        setupStreakAdapter(streaks)

        setipFlagAdapter(flags)

    }

    private fun setupStreakAdapter(streaks: ArrayList<WaveElement>) {
        val streakAdapter = WaveFormAdapter(streaks)
        waveRecycler.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        waveRecycler.adapter = streakAdapter

        waveRecycler.isNestedScrollingEnabled


        waveRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                flagRecycler.scrollBy(dx, dy)

                super.onScrolled(recyclerView, dx, dy)

            }
        })
    }

    private fun setipFlagAdapter(flags: ArrayList<WaveElement>) {
        val flagsAdapter = WaveFormAdapter(flags)

        val manager = object : LinearLayoutManager(context, RecyclerView.HORIZONTAL, false) {

            var enableScrolling = true

            override fun canScrollHorizontally(): Boolean {
                return enableScrolling && super.canScrollHorizontally()
            }
        }

        flagRecycler.layoutManager = manager

        flagRecycler.adapter = flagsAdapter
    }

    override fun close() {
        waveViewScope.cancel()
    }
}