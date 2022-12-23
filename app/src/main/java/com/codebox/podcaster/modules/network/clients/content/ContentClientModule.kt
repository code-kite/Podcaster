package com.codebox.podcaster.modules.network.clients.content

import com.codebox.podcaster.network.client.ContentClient
import com.codebox.podcaster.network.okhttp.OkHttpClientProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Converter
import javax.inject.Singleton

/**
 * Created by Codebox on 12/05/21
 */
@Module
@InstallIn(SingletonComponent::class)
object ContentClientModule {

    @Provides
    @Singleton
    fun provideContentClient(
        clientProvider: OkHttpClientProvider,
        converterFactory: Converter.Factory
    ): ContentClient {
        return ContentClient(clientProvider, converterFactory)
    }

}