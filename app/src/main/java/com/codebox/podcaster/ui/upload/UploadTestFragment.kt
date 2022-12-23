package com.codebox.podcaster.ui.upload

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.codebox.podcaster.R
import com.codebox.podcaster.ui.main.MainViewModel
import com.codebox.podcaster.ui.main.SnackBarMsg
import com.codebox.podcaster.ui.upload.model.Podcast
import dagger.hilt.android.AndroidEntryPoint
import io.tus.java.client.TusClient
import io.tus.java.client.TusUpload
import kotlinx.android.synthetic.main.fragment_editing.*
import kotlinx.android.synthetic.main.fragment_editing.btnUpload
import kotlinx.android.synthetic.main.fragment_upload_test.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.URL
import javax.inject.Inject

private const val TAG = "UploadTestFragment"

@AndroidEntryPoint
class UploadTestFragment : Fragment(R.layout.fragment_upload_test) {



    @Inject
    lateinit var uploadService: UploadService

    val args: UploadTestFragmentArgs by navArgs()

    val mainViewModel: MainViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //buildRetrofit()



        Log.d(TAG, "onViewCreated: **********Segment: ${args.segmentWithFlags}" )
        Log.d(TAG, "onViewCreated: ********** UploadService Instance $uploadService")

        btnUpload.setOnClickListener { lifecycleScope.launch { onUploadButtonClicked() } }

    }

    /*private fun buildRetrofit() {
        Result
        val prod = "https://tus-go-prod.codebox.com"
        val staging = "http://tus-iopod-stage.codebox.com:8080/"
        val otherUrl = "https://tus-io-stage.codebox.com/";
        val retrofit = Retrofit.Builder()
            .baseUrl(staging)
            .addConverterFactory(MoshiConverterFactory.create())

            .build()

        uploadService = retrofit.create(UploadService::class.java)
    }*/

    private suspend fun onUploadButtonClicked() {
        val fileUrl = uploadFileViaTus()


        if (fileUrl != null) {
            val podcast = Podcast.createSamplePodcast(
                etPodcastName.text.toString(),
                etEpisodeName.text.toString(),
                fileUrl
            )

            Log.d(TAG, "onUploadButtonClicked: Podcast $podcast")
            /*uploadService.uploadPodcastCall(podcast).enqueue(object : Callback<UploadResponse> {
                override fun onResponse(
                    call: Call<UploadResponse>,
                    response: Response<UploadResponse>
                ) {
                    Log.d(TAG, "onResponse: ${response.raw()}")
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Log.d(TAG, "onFailure: ")
                }
            })*/

            //Log.d(TAG, "onUploadButtonClicked: ${response.raw()}")

            try {
                val response = uploadService.uploadPodcast(podcast)
                Log.d(TAG, "onUploadButtonClicked: ${response.raw()}")
                Toast.makeText(requireContext(), "Is SuccessFul:  ${response.isSuccessful}", Toast.LENGTH_SHORT).show()

            } catch (e: java.lang.Exception) {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "onUploadButtonClicked: ${e.message}")
            }
        } else {
            mainViewModel.snackbarMsgData.postValue(SnackBarMsg("Something Went wrong"))
        }


    }

    private suspend fun uploadFileViaTus(): String? {
        val client = TusClient()
        /*val url = "https://tus-gopod-stage.codebox.com/files"
        val prod = "https://tus-go-prod.codebox.com/files"*/
        val url = etTusUrl.text.toString()
        client.uploadCreationURL = URL(url)
        //client.enableResuming(false)

        val upload = TusUpload(args.segmentWithFlags.segment.file)

        return uploadFile(client, upload)
    }

    private suspend fun uploadFile(client: TusClient, upload: TusUpload): String? {
        return withContext(Dispatchers.IO) url@{


            try {
                val uploader = client.resumeOrCreateUpload(upload)
                val totalBytes = upload.size
                var uploadedBytes = uploader.offset

                // Upload file in 1MiB chunks
                uploader.chunkSize = 1024 * 1024
                while (uploader.uploadChunk() > 0) {
                    uploadedBytes = uploader.offset
                    Log.d(
                        TAG,
                        "uploadFile: Progress: uploaded: $uploadedBytes :: total: $totalBytes"
                    )
                }
                uploader.finish()
                Log.d(TAG, "uploadFile: UPLOAD URL**** ${uploader.uploadURL}")
                return@url uploader.uploadURL.toString()

            } catch (e: Exception) {
                e.printStackTrace()
                return@url null
            }
        }
    }
}