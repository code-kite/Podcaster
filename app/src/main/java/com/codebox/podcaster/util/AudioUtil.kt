package com.codebox.podcaster.util

import android.util.Log
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.FFprobe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * Created by Codebox on 05/03/21
 */
class AudioUtil @Inject constructor() {

    suspend fun concatenateFiles(files: List<File>, outputPath: String): Boolean {

        val filesString = makeFilesString(files)

        val code = concatenate(filesString, outputPath)

        return code == RETURN_CODE_SUCCESS
    }

    private suspend fun concatenate(filesString: String, outputPath: String): Int {
        return withContext(Dispatchers.IO) {

            val command = "-i \"concat:$filesString\" -c copy $outputPath"
            val code = FFmpeg.execute(command)
            code
        }

    }

    private suspend fun makeFilesString(files: List<File>): String {
        return withContext(Dispatchers.Default) {

            val filesStringBuilder = StringBuilder()

            for (file in files) {
                filesStringBuilder.append(file.absolutePath)
                filesStringBuilder.append("|")
            }

            filesStringBuilder.replace(filesStringBuilder.length - 1, filesStringBuilder.length, "")
            filesStringBuilder.toString()

        }
    }

    suspend fun  generateWaveArray(filePath:String){

        withContext(Dispatchers.Default){

            val command = "-v error -f lavfi -i amovie=$filePath,asetnsamples=44100,astats=metadata=1:reset=1 -show_entries frame_tags=lavfi.astats.Overall.RMS_level -of csv=p=0"
            val code = FFprobe.execute(command)

            Log.d("Wave", "generateWaveArray: $code")
            code

        }

    }

}