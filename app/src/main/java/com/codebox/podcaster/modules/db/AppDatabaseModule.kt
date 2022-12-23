package com.codebox.podcaster.modules.db

import android.content.Context
import com.codebox.podcaster.storage.db.app.AppDatabase
import com.codebox.podcaster.storage.db.app.segment.FlagDao
import com.codebox.podcaster.storage.db.app.segment.SegmentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

/**
 * Created by Codebox on 08/03/21
 */
@Module
@InstallIn(SingletonComponent::class)
object AppDatabaseModule {

    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideSegmentDao(appDatabase: AppDatabase): SegmentDao {
        return appDatabase.segmentDao()
    }

    @Provides
    fun provideFlagDao(appDatabase: AppDatabase): FlagDao {
        return appDatabase.flagDao()
    }
}
