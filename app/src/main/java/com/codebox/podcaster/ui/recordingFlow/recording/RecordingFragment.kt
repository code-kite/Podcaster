package com.codebox.podcaster.ui.recordingFlow.recording

import android.Manifest
import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.View.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.codebox.podcaster.R
import com.codebox.podcaster.permission.PermissionManager
import com.codebox.podcaster.recorder.segmentrecorder.SegmentRecordingService
import com.codebox.podcaster.recorder.segmentrecorder.SegmentRecordingService.Companion.NO_OPERATION
import com.codebox.podcaster.recorder.segmentrecorder.SegmentRecordingService.Companion.PAUSE_RECORDING
import com.codebox.podcaster.recorder.segmentrecorder.SegmentRecordingService.Companion.RESUME_RECORDING
import com.codebox.podcaster.recorder.segmentrecorder.SegmentRecordingService.Companion.START_RECORDING
import com.codebox.podcaster.storage.db.app.segment.SegmentWithFlags
import com.codebox.podcaster.ui.main.MainViewModel
import com.codebox.podcaster.ui.main.SnackBarMsg
import com.codebox.podcaster.ui.util.selection.data.SelectableItem
import com.codebox.podcaster.ui.util.selection.data.header.Header
import com.codebox.podcaster.ui.util.selection.singleSelection.SingleSelectionBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_recording.*
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RecordingFragment : Fragment(R.layout.fragment_recording) {


    private var recordingServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as SegmentRecordingService.RecordingBinder
            segmentRecordingService = binder.getService()
            initialiseViewState()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            segmentRecordingService = null
        }
    }

    private fun initialiseViewState() {

        segmentRecordingService?.stateData?.observe(this, { manageViewState(it) })
        segmentRecordingService?.getStopwatchData()?.observe(this, { tvTimer.text = it })

    }


    private var isAddFlagEnabled = false
    private val mainViewModel: MainViewModel by activityViewModels()

    private fun manageViewState(state: Int) {
        when (state) {
            NO_OPERATION -> {
                btnClose.visibility = GONE
                onBackPressedCallback.isEnabled = false
                mainViewModel.bottomNavVisibilityData.value = VISIBLE
                stopRecordingGroup.visibility = VISIBLE
                recordingGroup.visibility = INVISIBLE
            }
            START_RECORDING, RESUME_RECORDING -> {
                onBackPressedCallback.isEnabled = true
                mainViewModel.bottomNavVisibilityData.value = GONE
                imgRecording.setImageResource(R.drawable.ic_record_full)
                stopRecordingGroup.visibility = INVISIBLE
                recordingGroup.visibility = VISIBLE
                btnClose.visibility = GONE

                setRecordingDotColor(START_RECORDING)
                startDotAnimator()

                tvPlayState.text = getString(R.string.pause)
                btnPlayPause.setImageResource(R.drawable.ic_pause)

                enableAddFlag()

                tvTitle.text = getString(R.string.recording)
            }
            PAUSE_RECORDING -> {
                onBackPressedCallback.isEnabled = true
                mainViewModel.bottomNavVisibilityData.value = GONE
                btnClose.visibility = VISIBLE
                stopDotAnimator()
                disableAddFlag()
                btnPlayPause.setImageResource(R.drawable.ic_record_white)
                setRecordingDotColor(PAUSE_RECORDING)
                tvTitle.text = getString(R.string.paused)
            }
        }
    }

    private fun setRecordingDotColor(recordingState: Int) {
        val color = when (recordingState) {
            PAUSE_RECORDING -> R.color.stopRecordingColor
            else -> R.color.primaryColor
        }

        val unwrappedDrawable = AppCompatResources.getDrawable(
            requireContext(), R.drawable.drawable_dot
        )
        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(requireContext(), color))

        recordingDot.background = unwrappedDrawable

    }

    private fun disableAddFlag() {
        btnAddFlag.isEnabled = false
        btnAddFlag.alpha = 0.5f
        tvFlagState.alpha = 0.5f
        isAddFlagEnabled = false
    }

    private fun enableAddFlag() {
        btnAddFlag.isEnabled = true
        btnAddFlag.alpha = 1f
        tvFlagState.alpha = 1f
        isAddFlagEnabled = true
    }

    private var segmentRecordingService: SegmentRecordingService? = null


    val TAG = RecordingFragment::class.simpleName

    private lateinit var recordAudioPermissionManager: PermissionManager

    val recordingViewModel: RecordingViewModel by viewModels()

    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            onBackPressed()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseRecordPermissionManager()
        initialiseViews()

        handleBackPress()


    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onResume() {
        super.onResume()
        bindRecordingService()
    }

    override fun onPause() {
        super.onPause()
        unBindRecordingService()
    }

    private fun unBindRecordingService() {
        requireActivity().unbindService(recordingServiceConnection)
        segmentRecordingService = null
    }

    private fun initialiseViews() {

        btnPlayPause.setOnClickListener { onButtonRecordPressed() }
        btnAddFlag.setOnClickListener { addFlag() }
        btnClose.setOnClickListener { onCloseButtonPressed() }
        btnAction.setOnClickListener { onSaveButtonPressed() }
    }

    private fun onSaveButtonPressed() {

        pauseRecording()
        navigateToSaveOptionsBottomSheet()

    }

    private val saveInExistingEpisode: (segment: SegmentWithFlags) -> Unit = {
        Toast.makeText(requireContext(), "saveInExistingEpisode", Toast.LENGTH_SHORT).show()
    }
    private val saveInNewEpisode: (segment: SegmentWithFlags) -> Unit = {
        Toast.makeText(requireContext(), "saveInNewEpisode", Toast.LENGTH_SHORT).show()
    }

    private val saveAsSegment: (segment: SegmentWithFlags) -> Unit = {
        val direction =
            RecordingFragmentDirections.actionRecordingFragmentToSaveAsSegmentFragment(it)
        findNavController().navigate(direction)
    }


    private fun navigateToSaveOptionsBottomSheet() {


        val selectableItemsFunctionMap =
            linkedMapOf<SelectableItem, (segment: SegmentWithFlags) -> Unit>(
                SelectableItem.TitleOnly(getString(R.string.save_in_existing_episode)) to saveInExistingEpisode,
                SelectableItem.TitleOnly(getString(R.string.save_in_new_episode)) to saveInNewEpisode,
                SelectableItem.TitleOnly(getString(R.string.save_as_segment)) to saveAsSegment
            )


        val selectableItems = selectableItemsFunctionMap.keys.toTypedArray()

        val header = Header.TitleOnly(getString(R.string.save_recording))


        val directions = RecordingFragmentDirections.actionGlobalSingleSelectionBottomSheetFragment(
            selectableItems,
            header
        )


        setFragmentResultListener(SingleSelectionBottomSheetFragment.KEY_SELECTED_ITEM_REQUEST) { _, bundle ->

            val item =
                bundle.getParcelable<SelectableItem?>(SingleSelectionBottomSheetFragment.KEY_SELECTED_ITEM)

            if (item == null) {
                resumeRecording()
            } else {
                lifecycleScope.launch {
                    val segment = createFinalSegment()
                    selectableItemsFunctionMap[item]?.invoke(segment!!)
                }
            }
        }


        findNavController().navigate(directions)


    }

    private suspend fun createFinalSegment(): SegmentWithFlags? {

        showProgressUI()

        when (getCurrentRecordingState()) {

            START_RECORDING, RESUME_RECORDING -> {
                segmentRecordingService?.pauseRecording()
            }
        }
        val segmentWithFlags = segmentRecordingService?.generateSegment()

        hideProgressUI()

        return segmentWithFlags

    }

    private fun onBackPressed() {
        when (getCurrentRecordingState()) {
            START_RECORDING, RESUME_RECORDING -> {
                pauseRecording()
            }
        }

        showDeleteAlertDialog()
    }

    private fun onCloseButtonPressed() {
        showDeleteAlertDialog()
    }

    private fun showDeleteAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.delete_msg))
            .setPositiveButton(getString(R.string.continue_txt)) { dialog, which ->

                lifecycleScope.launch { segmentRecordingService?.deleteAllRecordingsAndTerminate() }

            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, which -> dialog.dismiss() }
            .create().show()

    }

    private fun onButtonRecordPressed() {
        when (getCurrentRecordingState()) {
            NO_OPERATION -> {
                checkPermissionAndStartRecording()
            }
            START_RECORDING -> {
                pauseRecording()
            }
            PAUSE_RECORDING -> {
                resumeRecording()
            }
            RESUME_RECORDING -> {
                pauseRecording()
            }
        }
    }

    private fun getCurrentRecordingState(): Int {
        return segmentRecordingService?.stateData?.value ?: NO_OPERATION
    }

    private fun addFlag() {
        if (isAddFlagEnabled) {
            lifecycleScope.launch { segmentRecordingService?.addFlag() }
            mainViewModel.snackbarMsgData.value = SnackBarMsg(getString(R.string.flag_added))
        }

    }

    private fun hideProgressUI() {

    }

    private fun showProgressUI() {

    }


    private fun checkPermissionAndStartRecording() {
        recordAudioPermissionManager.startRequestPermissionFlow()
    }

    private fun startRecording() {
        lifecycleScope.launch { segmentRecordingService?.startRecording() }
    }

    private fun pauseRecording() {
        lifecycleScope.launch { segmentRecordingService?.pauseRecording() }
    }

    private fun resumeRecording() {
        lifecycleScope.launch { segmentRecordingService?.resumeRecording() }
    }


    private lateinit var dotAnimator: ValueAnimator

    private fun setupDotAnimator() {
        dotAnimator = ValueAnimator.ofFloat(1.0f, 0.0f)
        dotAnimator.repeatCount = INFINITE
        dotAnimator.repeatMode = REVERSE
        dotAnimator.duration = 800
        dotAnimator.addUpdateListener { recordingDot?.alpha = it.animatedValue as Float }
    }

    private fun startDotAnimator() {

        if (!::dotAnimator.isInitialized)
            setupDotAnimator()

        dotAnimator.start()

    }

    private fun stopDotAnimator() {
        if (::dotAnimator.isInitialized)
            dotAnimator.cancel()
    }


    private fun bindRecordingService() {

        val serviceIntent = Intent(requireContext(), SegmentRecordingService::class.java)
        requireActivity().bindService(serviceIntent, recordingServiceConnection, BIND_AUTO_CREATE)

    }


    private fun initialiseRecordPermissionManager() {
        recordAudioPermissionManager = PermissionManager(
            this,
            Manifest.permission.RECORD_AUDIO,
            object : PermissionManager.InteractionListener {
                override fun onPermissionGranted() {
                    startRecording()
                }

                override fun onPermissionDenied() {
                    pauseRecording()
                }

                override fun showRationale() {
                    AlertDialog.Builder(requireContext())
                        .setMessage("Record Audio permission is required to start recording")
                        .setPositiveButton(
                            "Request Again"
                        ) { dialog, which ->
                            dialog.dismiss()
                            recordAudioPermissionManager.onPositiveResponseToRationale()
                        }
                        .setNegativeButton(
                            "Cancel"
                        ) { dialog, which -> dialog.dismiss() }
                        .create().show()
                }

            })
    }

}