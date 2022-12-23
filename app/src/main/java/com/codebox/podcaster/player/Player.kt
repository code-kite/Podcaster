package com.codebox.podcaster.player

import android.media.MediaPlayer
import android.util.Log
import java.io.File
import java.lang.Exception

/**
 * Created by Codebox on 01/03/21
 */
class Player {


    private val player = MediaPlayer()


    fun play(file: File) {

        try {
            player.setDataSource(file.absolutePath)
            player.prepare()
            player.start()
        }catch (e:Exception){
            Log.d("Player", "play: ")
        }

    }



}