package com.codebox.podcaster.network.client.base

import retrofit2.Retrofit

/**
 * Created by Codebox on 12/05/21
 */
abstract class BaseRemoteClient {

    protected abstract var clinet: Retrofit

    protected abstract fun createClient() : Retrofit

    fun <T> createService(cls: Class<T>): T {
        return clinet.create(cls)
    }

}