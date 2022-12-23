package com.codebox.podcaster.storage.db.app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.codebox.podcaster.storage.db.app.segment.Flag
import com.codebox.podcaster.storage.db.app.segment.FlagDao
import com.codebox.podcaster.storage.db.app.segment.Segment
import com.codebox.podcaster.storage.db.app.segment.SegmentDao

/**
 * Created by Codebox on 02/03/21
 */
@Database(entities = arrayOf(Segment::class, Flag::class), version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun segmentDao(): SegmentDao
    abstract fun flagDao(): FlagDao


    companion object {

        private const val NAME = "app-database"


        fun getDatabase(applicationContext: Context): AppDatabase {
            return Room.databaseBuilder(applicationContext, AppDatabase::class.java, NAME).build()
        }

    }


}