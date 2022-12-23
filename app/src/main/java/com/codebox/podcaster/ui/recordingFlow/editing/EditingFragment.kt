package com.codebox.podcaster.ui.recordingFlow.editing

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.codebox.podcaster.R
import com.codebox.podcaster.player.Player
import com.codebox.podcaster.storage.db.app.segment.SegmentWithFlags
import com.codebox.podcaster.ui.customViews.wave.view.SoundFile
import com.codebox.podcaster.ui.customViews.wave.view.WaveFormViewManager
import com.codebox.podcaster.ui.main.MainViewModel
import kotlinx.android.synthetic.main.fragment_discover.*
import kotlinx.android.synthetic.main.fragment_editing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


class EditingFragment : Fragment(R.layout.fragment_editing) {


    val args: EditingFragmentArgs by navArgs()

    private val mainViewModel: MainViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.bottomNavVisibilityData.value = View.GONE
        Log.d("EditingFragment", "**********onViewCreated: ${args.segmentWithFlags.toString()}")

        lifecycleScope.launchWhenCreated { initViews() }


        //playRecording(args.segmentWithFlags)
    }

    private suspend fun initViews() {


        btnUpload.setOnClickListener {

            val directions =
                EditingFragmentDirections.actionEditingFragmentToUploadTestFragment(args.segmentWithFlags)

            findNavController().navigate(directions)
        }

        initWaveFormView()
    }

    private suspend fun initWaveFormView(){
        progressbar.visibility = View.VISIBLE
        val soundFile = createSoundFile(args.segmentWithFlags.segment.filePath)
        progressbar.visibility = View.GONE

        val waveformViewManager = WaveFormViewManager(
            waveformView,
            btnPlayPause,
            startMarker,
            endMarker,
            tvTimer,
            null,
            null,
            soundFile,
            resources
        )

        waveViewContainer.visibility = View.VISIBLE


    }

    private suspend fun createSoundFile(filePath: String): SoundFile {

        return withContext(Dispatchers.IO) soundFile@{
            val file = SoundFile.create(filePath, null)
            return@soundFile file
        }

    }

    private fun playRecording(segmentWithFlags: SegmentWithFlags) {
        val player = Player()

        val file = File(segmentWithFlags.segment.filePath.trim())
        player.play(file)
    }
}