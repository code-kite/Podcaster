package com.codebox.podcaster.ui.recordingFlow.editing

import com.codebox.podcaster.R

/**
 * Created by Codebox on 29/04/21
 */
sealed class WaveElement {

    abstract val layoutResource: Int


    class WaveStreak(val recyclerHeight: Int, val streakAmplitude: Int) : WaveElement() {
        override val layoutResource: Int
            get() = R.layout.item_wave_streak
    }

    object WaveEmptyElement : WaveElement(){
        override val layoutResource: Int
            get() = R.layout.item_wave_empty
    }

    object WaveOnlyFlag: WaveElement(){
        override val layoutResource: Int
            get() = R.layout.item_wave_only_flag
    }
}

