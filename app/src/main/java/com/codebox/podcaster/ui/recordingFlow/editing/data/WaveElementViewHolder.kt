package com.codebox.podcaster.ui.recordingFlow.editing.data

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.codebox.podcaster.R
import com.codebox.podcaster.ui.recordingFlow.editing.WaveElement

/**
 * Created by Codebox on 29/04/21
 */
sealed class WaveElementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(waveElement: WaveElement)


    class StreakViewHolder(itemView: View) : WaveElementViewHolder(itemView) {

        private val element: View = itemView.findViewById(R.id.element)

        override fun bind(waveElement: WaveElement) {
            val streak = waveElement as WaveElement.WaveStreak
            val existingParams = element.layoutParams
            val heightInPixels = (streak.recyclerHeight * streak.streakAmplitude) / 100
            existingParams.height = heightInPixels
            element.layoutParams = existingParams
        }

    }

    class FlagViewHolder(itemView: View) : WaveElementViewHolder(itemView) {


        override fun bind(waveElement: WaveElement) {

        }
    }

    class EmptyViewHolder(itemView: View) : WaveElementViewHolder(itemView){
        override fun bind(waveElement: WaveElement) {

        }
    }

    class OnlyFlagViewHolder(itemView: View) : WaveElementViewHolder(itemView){
        override fun bind(waveElement: WaveElement) {

        }

    }
}
