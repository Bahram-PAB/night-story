package com.nightstory.app.data.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ChatClient {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(
                Interceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header("User-Agent", "NightStory/1.2.8")
                        .build()
                    chain.proceed(request)
                }
            )
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
    }

    // LENIENT: ignore unknown fields + lenient parsing
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    fun createService(baseUrl: String): ChatService {
        var url = baseUrl.trimEnd('/')
        if (url.endsWith("/v1")) {
            url = url.removeSuffix("/v1")
        }
        url = "$url/"

        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ChatService::class.java)
    }
}
