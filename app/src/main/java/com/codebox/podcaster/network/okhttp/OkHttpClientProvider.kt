package com.codebox.podcaster.network.okhttp

import com.codebox.podcaster.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject

/**
 * Created by Codebox on 12/05/21
 */
class OkHttpClientProvider @Inject constructor(private val loggingInterceptor: HttpLoggingInterceptor) {

    var client: OkHttpClient = createClientBuilder().build()
        private set


    private fun createClientBuilder(): OkHttpClient.Builder {

        val builder = OkHttpClient.Builder()

        addLoggingInterceptorIfRequired(builder)

        return builder

    }

    private fun addLoggingInterceptorIfRequired(builder: OkHttpClient.Builder) {

        if (BuildConfig.ENABLE_NETWORK_LOGGING) {
            builder.addInterceptor(loggingInterceptor)
        }
    }

}