package com.codebox.podcaster.modules.network

import com.codebox.podcaster.network.okhttp.OkHttpClientProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 * Created by Codebox on 06/05/21
 */
@Module
@InstallIn(SingletonComponent::class)
object BaseNetworkModule {

    /**
     *  This might need to change if some runtime values are required while creating client.
     *  Might need to use @Provides and create your own implementation of RemoteClientImpl.
     *  In that case you might need to remove RemoteClient Interface as well
     */
    @Singleton
    @Provides
    fun provideOkHttpClientProvider(loggingInterceptor: HttpLoggingInterceptor): OkHttpClientProvider {
        return OkHttpClientProvider(loggingInterceptor)
    }

    @Provides
    @Singleton
    fun provideConverterFactory(): Converter.Factory {
        return MoshiConverterFactory.create()
    }

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

}