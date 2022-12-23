package com.codebox.podcaster.permission

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Created by Codebox on 26/02/21
 */
class PermissionManager(
    private val fragment: Fragment,
    private val permission: String,
    private val listener: InteractionListener
) {

    private val activityResultCallback =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                listener.onPermissionGranted()
            } else {
                listener.onPermissionDenied()
            }
        }


    fun startRequestPermissionFlow() {

        when {
            isPermissionGranted() -> {
                listener.onPermissionGranted()
            }
            fragment.shouldShowRequestPermissionRationale(permission) -> {
                listener.showRationale()
            }
            else -> {
                requestPermission()
            }
        }

    }

    fun onPositiveResponseToRationale() {
        requestPermission()
    }

    private fun requestPermission() {
        activityResultCallback.launch(permission)
    }


    private fun isPermissionGranted(): Boolean {

        return ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }


    interface InteractionListener {
        fun onPermissionGranted()

        fun onPermissionDenied()

        fun showRationale()
    }
}