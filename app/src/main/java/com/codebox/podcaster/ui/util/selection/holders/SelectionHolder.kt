package com.codebox.podcaster.ui.util.selection.holders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codebox.podcaster.R
import com.codebox.podcaster.ui.util.selection.data.SelectableItem
import com.codebox.podcaster.ui.util.selection.listeners.ItemClickListener

/**
 * Created by Codebox on 31/05/21
 */

sealed class SelectionHolder(itemView: View, itemClickListener: ItemClickListener) :
    RecyclerView.ViewHolder(itemView) {

    init {
        itemView.setOnClickListener { itemClickListener.onItemClicked(bindingAdapterPosition) }
    }

    abstract fun bind(item: SelectableItem)

    class TitleOnlyHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        itemClickListener: ItemClickListener
    ) :
        SelectionHolder(
            layoutInflater.inflate(R.layout.item_title_only, parent, false),
            itemClickListener
        ) {


        private val titleTv = itemView.findViewById<TextView>(R.id.title)

        override fun bind(i: SelectableItem) {
            val item = i as SelectableItem.TitleOnly
            titleTv.text = item.title


        }

    }


}
