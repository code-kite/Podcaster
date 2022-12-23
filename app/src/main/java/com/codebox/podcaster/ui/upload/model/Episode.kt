package com.codebox.podcaster.ui.upload.model


import com.squareup.moshi.Json
import com.squareup.moshi.Moshi

data class Episode(
    @Json(name = "audioFile")
    val audioFile: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "duration")
    val duration: String,
    @Json(name = "episodeType")
    val episodeType: String,
    @Json(name = "guid")
    val guid: String,
    @Json(name = "image")
    val image: String,
    @Json(name = "pubDate")
    val pubDate: String,
    @Json(name = "season")
    val season: Int,
    @Json(name = "title")
    val title: String
) {
    companion object {
        fun createSampleEpisode(episodeName: String, url: String): Episode {

            return Episode(
                title = episodeName,
                description = "$episodeName description",
                pubDate = "${System.currentTimeMillis()}",
                guid = "$episodeName guid",
                duration = "00:35:13",
                episodeType = "24",
                image = "",
                season = 1,
                audioFile = url
            )

        }
    }

    override fun toString(): String {
        return Moshi.Builder().build().adapter(Episode::class.java).toJson(this)

        //return super.toString()
    }
}