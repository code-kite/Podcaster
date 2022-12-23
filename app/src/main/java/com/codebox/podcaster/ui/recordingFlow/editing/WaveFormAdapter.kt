package com.codebox.podcaster.ui.recordingFlow.editing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.codebox.podcaster.R
import com.codebox.podcaster.ui.recordingFlow.editing.data.WaveElementViewHolder
import com.codebox.podcaster.ui.recordingFlow.editing.data.WaveElementViewHolderTypes
import com.codebox.podcaster.ui.recordingFlow.editing.WaveElement

/**
 * Created by Codebox on 29/04/21
 */
class WaveFormAdapter(val elements: List<WaveElement>) :
    RecyclerView.Adapter<WaveElementViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return elements[position].layoutResource
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaveElementViewHolder {


        val waveElementViewHolderType = WaveElementViewHolderTypes.from(viewType)
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return waveElementViewHolderType?.getViewHolder(view) ?: throw IllegalAccessException()

    }

    override fun onBindViewHolder(holder: WaveElementViewHolder, position: Int) {
        holder.bind(elements[position])
    }

    override fun getItemCount(): Int {
        return elements.size
    }

}