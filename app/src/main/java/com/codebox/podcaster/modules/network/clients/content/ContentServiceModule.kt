package com.codebox.podcaster.modules.network.clients.content

import com.codebox.podcaster.network.client.ContentClient
import com.codebox.podcaster.ui.upload.UploadService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by Codebox on 12/05/21
 */
@Module
@InstallIn(SingletonComponent::class)
class ContentServiceModule {

    @Provides
    @Singleton
    fun provideUploadService(contentClient: ContentClient): UploadService {
        return contentClient.createService(UploadService::class.java)
    }
}