package com.codebox.podcaster.storage.db.app.segment

import androidx.room.Dao
import androidx.room.Insert

/**
 * Created by Codebox on 05/03/21
 */
@Dao
interface FlagDao {

    @Insert
    suspend fun insertFlags(flags:List<Flag>)

}