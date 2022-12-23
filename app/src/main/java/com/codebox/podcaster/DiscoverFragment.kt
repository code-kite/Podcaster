package com.codebox.podcaster

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.codebox.podcaster.storage.db.app.segment.Flag
import com.codebox.podcaster.storage.db.app.segment.Segment
import com.codebox.podcaster.storage.db.app.segment.SegmentWithFlags
import com.codebox.podcaster.ui.customViews.wave.WaveViewManager
import com.codebox.podcaster.R
import com.codebox.podcaster.util.AudioUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_discover.*
import kotlinx.android.synthetic.main.wave_view.*
import javax.inject.Inject

private const val TAG = "DiscoverFragment"

@AndroidEntryPoint
class DiscoverFragment : Fragment(R.layout.fragment_discover) {

    @Inject
    lateinit var audioUtil: AudioUtil

    private lateinit var waveViewManager: WaveViewManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btnLogin.setOnClickListener {

            val dirs = DiscoverFragmentDirections.actionDiscoverFragmentToLoginFragment()
            findNavController().navigate(dirs)
        }

        btnAction.setOnClickListener { onButtonActionPressed() }

        waveViewManager = WaveViewManager(waveViewContainer)
        



    }



    private fun onButtonActionPressed() {


        val segmentWithFlags = SegmentWithFlags(
            Segment(
                "/storage/emulated/0/Android/data/com.codebox.podcaster/files/Podcasts/segments/Segment_14_09_2021__13_39_32.aac",
                1,
                2
            ),
            mutableListOf(Flag(0, 10, 0))
        )

        val direction =
            DiscoverFragmentDirections.actionDiscoverFragmentToEditingFragment(segmentWithFlags)


        findNavController().navigate(direction)
    }


}