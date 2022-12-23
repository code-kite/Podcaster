package com.codebox.podcaster.storage.disk.base

import android.content.Context
import android.os.Environment
import com.codebox.podcaster.modules.dispatcher.DispatcherModule
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * Created by Codebox on 05/03/21
 */
class FileUtil @Inject constructor(
    @ApplicationContext val context: Context,
    @DispatcherModule.IODispatcher val ioDispatcher: CoroutineDispatcher
) {

    fun isExternalStorageAvailable(): Boolean {
        val state = Environment.getExternalStorageState()
        return state == Environment.MEDIA_MOUNTED
    }

    suspend fun getFolderInSystem(systemDirectory: String, folderName: String): File? {
        return withContext(ioDispatcher) scope@{
            val dir = File(context.getExternalFilesDir(systemDirectory), folderName)

            if (dir.exists())
                return@scope dir
            if (!dir.mkdirs()) {
                return@scope null
            }
            return@scope dir
        }

    }

}