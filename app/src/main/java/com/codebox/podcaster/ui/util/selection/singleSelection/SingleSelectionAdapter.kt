package com.codebox.podcaster.ui.util.selection.singleSelection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codebox.podcaster.ui.util.selection.data.SelectableItem
import com.codebox.podcaster.ui.util.selection.holders.SelectionHolder
import com.codebox.podcaster.ui.util.selection.holders.SelectionHolderTypes
import com.codebox.podcaster.ui.util.selection.listeners.ItemClickListener

/**
 * Created by Codebox on 31/05/21
 */
class SingleSelectionAdapter(
    val selectableItems: Array<SelectableItem>,
    val listener: (selectableItem: SelectableItem) -> Unit
) :
    RecyclerView.Adapter<SelectionHolder>() {

    private val itemClickListener = object : ItemClickListener {
        override fun onItemClicked(position: Int) {
            listener(selectableItems[position])
        }
    }


    override fun getItemViewType(position: Int): Int {
        return selectableItems[position].viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectionHolder {

        val inflater = LayoutInflater.from(parent.context)
        val type = SelectionHolderTypes.from(viewType)
        return type?.getViewHolder(inflater, parent, itemClickListener)
            ?: throw IllegalAccessException()

    }

    override fun onBindViewHolder(holder: SelectionHolder, position: Int) {
        holder.bind(selectableItems[position])
    }

    override fun getItemCount(): Int {
        return selectableItems.size;
    }

}