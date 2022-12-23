package com.codebox.podcaster.storage.db.app.segment

import java.io.File

/**
 * Created by Codebox on 02/03/21
 */
class SubSegment {

    var startTime: Long = -1
    var endTime: Long = -1
    lateinit var subSegmentFile: File
    val flags = mutableListOf<Long>()

}