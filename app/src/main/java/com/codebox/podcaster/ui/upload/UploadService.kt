package com.codebox.podcaster.ui.upload

import com.codebox.podcaster.ui.upload.model.Podcast
import com.codebox.podcaster.ui.upload.model.UploadResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


/**
 * Created by Codebox on 10/05/21
 */
interface UploadService {


    /*@Multipart
    @POST("files")
    fun uploadFile(
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part,
    ): Call<ResponseBody>*/


    @POST("test/podcast/create")
    suspend fun uploadPodcast(@Body podcast: Podcast): Response<UploadResponse>

    @POST("test/podcast/create")
    fun uploadPodcastCall(@Body podcast: Podcast): Call<UploadResponse>


}