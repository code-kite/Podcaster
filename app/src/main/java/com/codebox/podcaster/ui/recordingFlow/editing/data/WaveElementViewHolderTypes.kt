package com.codebox.podcaster.ui.recordingFlow.editing.data

import android.view.View
import androidx.annotation.LayoutRes
import com.codebox.podcaster.R

/**
 * Created by Codebox on 29/04/21
 */
enum class WaveElementViewHolderTypes(@LayoutRes val layoutId: Int) {
    STREAK(R.layout.item_wave_streak) {
        override fun getViewHolder(view: View): WaveElementViewHolder {
            return WaveElementViewHolder.StreakViewHolder(view)
        }
    },

    EMPTY(R.layout.item_wave_empty) {
        override fun getViewHolder(view: View): WaveElementViewHolder {
            return WaveElementViewHolder.EmptyViewHolder(view)
        }
    },

    ONLY_FLAG(R.layout.item_wave_only_flag) {
        override fun getViewHolder(view: View): WaveElementViewHolder {
            return WaveElementViewHolder.OnlyFlagViewHolder(view)
        }
    }

    ;


    abstract fun getViewHolder(view: View): WaveElementViewHolder

    companion object {
        fun from(@LayoutRes layoutId: Int): WaveElementViewHolderTypes? {
            for (viewHolder in values()) {
                if (viewHolder.layoutId == layoutId)
                    return viewHolder
            }
            return null
        }
    }
}