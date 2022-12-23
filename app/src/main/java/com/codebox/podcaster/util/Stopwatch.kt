package com.codebox.podcaster.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codebox.podcaster.modules.dispatcher.DispatcherModule
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Created by Codebox on 22/04/21
 */
class Stopwatch @Inject constructor(@DispatcherModule.DefaultDispatcher val defaultDispatcher: CoroutineDispatcher) {

    companion object {
        private const val STARTED = 0
        private const val STOPPED = 1
        private const val PAUSED = 2
    }

    private val interval: Long = 1000

    private var state = STOPPED

    private val tickLiveData = MutableLiveData<Long>()

    private lateinit var watchJob: Job


    fun start() {

        when(state){

            STOPPED ->{
                state = STARTED
                startWatchJob()
            }

        }


    }

    fun pause() {
        state = PAUSED
    }

    fun stop() {
        state = STOPPED
        watchJob.cancel(null)
    }

    fun resume() {
        state = STARTED
    }

    fun getTickLiveData(): LiveData<Long> {
        return tickLiveData
    }

    private fun startWatchJob() {

        //TODO Figure out a way to implement parallel execution without coroutine scope

        watchJob = CoroutineScope(defaultDispatcher).launch {

            var timeElapsed = 0L

            while (state != STOPPED) {

                if (state != PAUSED){
                    tickLiveData.postValue(timeElapsed)
                    timeElapsed += interval
                }


                delay(interval)


            }
        }

    }


}