package com.codebox.podcaster.ui.util.selection.singleSelection

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.codebox.podcaster.R
import com.codebox.podcaster.ui.util.selection.data.SelectableItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_single_selection_bottom_sheet.*


class SingleSelectionBottomSheetFragment : BottomSheetDialogFragment() {


    companion object {
        private const val TAG = "SingleSelectionFragment"
        const val KEY_SELECTED_ITEM_REQUEST = "keyRequest"
        const val KEY_SELECTED_ITEM = "keyResult"
    }

    private var isResultSent = false;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_single_selection_bottom_sheet, container, false)
    }

    private lateinit var adapter: SingleSelectionAdapter
    val args: SingleSelectionBottomSheetFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        initTitle()
        initRecycler()
    }

    private fun initTitle() {
        val headerView = args.header.getView(layoutInflater, headerContainer)

        headerContainer.addView(headerView)
    }

    override fun onDismiss(dialog: DialogInterface) {

        setResult(null)

        super.onDismiss(dialog)
    }

    private fun setResult(selectableItem: SelectableItem?) {

        if (isResultSent)
            return

        isResultSent = true;

        val bundle = Bundle().apply {
            this.putParcelable(KEY_SELECTED_ITEM, selectableItem)
        }
        setFragmentResult(KEY_SELECTED_ITEM_REQUEST, bundle)

    }

    private fun initRecycler() {


        singleSelectionList.layoutManager = LinearLayoutManager(context)
        adapter = SingleSelectionAdapter(args.selectableItems) {
            setResult(it)
            dismiss()
        }
        singleSelectionList.adapter = adapter


    }
}