package com.codebox.podcaster.network.client

import com.codebox.podcaster.BuildConfig
import com.codebox.podcaster.network.client.base.BaseRemoteClient
import com.codebox.podcaster.network.okhttp.OkHttpClientProvider
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Created by Codebox on 12/05/21
 */
class ContentClient @Inject constructor (
    private val clientProvider: OkHttpClientProvider,
    private val converterFactory: Converter.Factory
) : BaseRemoteClient() {


    override var clinet: Retrofit = createClient()

    override fun createClient(): Retrofit {
        return Retrofit.Builder()
            .client(clientProvider.client)
            .addConverterFactory(converterFactory)
            .baseUrl(BuildConfig.TUS_BASE_URL)
            .build()
    }
}