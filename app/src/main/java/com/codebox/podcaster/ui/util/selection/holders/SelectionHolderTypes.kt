package com.codebox.podcaster.ui.util.selection.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.codebox.podcaster.ui.util.selection.data.SelectableItemTypes
import com.codebox.podcaster.ui.util.selection.listeners.ItemClickListener

/**
 * Created by Codebox on 31/05/21
 */
enum class SelectionHolderTypes(val viewType: Int) {

    TITLE_ONLY(SelectableItemTypes.TITLE_ONLY.ordinal) {
        override fun getViewHolder(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            itemClickListener: ItemClickListener
        ): SelectionHolder {
            return SelectionHolder.TitleOnlyHolder(layoutInflater, parent, itemClickListener)
        }

    };

    abstract fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        itemClickListener: ItemClickListener
    ): SelectionHolder


    companion object {
        fun from(viewType: Int): SelectionHolderTypes? {

            for (type in values()) {
                if (type.viewType == viewType)
                    return type
            }
            return null
        }


    }
}