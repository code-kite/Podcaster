package com.codebox.podcaster.ui.upload.model


import com.squareup.moshi.Json

data class UploadResponse(
    @Json(name = "response")
    val response: String,
    @Json(name = "status")
    val status: Int
)