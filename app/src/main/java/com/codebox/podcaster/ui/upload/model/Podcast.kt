package com.codebox.podcaster.ui.upload.model


import com.squareup.moshi.Json
import com.squareup.moshi.Moshi

data class Podcast(
    @Json(name = "category")
    val category: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "episodes")
    val episodes: List<Episode>,
    @Json(name = "image")
    val image: String,
    @Json(name = "keywords")
    val keywords: String,
    @Json(name = "language")
    val language: String,
    @Json(name = "pubDate")
    val pubDate: String,
    @Json(name = "requestType")
    val requestType: Int,
    @Json(name = "title")
    val title: String
) {

    companion object {
        fun createSamplePodcast(
            podcastName: String,
            episodeName: String,
            episodeUrl: String
        ): Podcast {

            return Podcast(
                requestType = 1,
                title = podcastName,
                description = podcastName + "description",
                language = "Hindi",
                category = "$podcastName category",
                pubDate = "${System.currentTimeMillis()}",
                image = "",
                episodes = arrayListOf(Episode.createSampleEpisode(episodeName, episodeUrl)),
                keywords = "$podcastName Keywords"
            )

        }


    }

    override fun toString(): String {
        return Moshi.Builder().build().adapter(Podcast::class.java).toJson(this)

        //return super.toString()
    }

}