package com.nightstory.app.data.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAITTSService {

    @POST("v1/audio/speech")
    suspend fun generateSpeech(
        @Header("Authorization") auth: String,
        @Body request: OpenAITTSRequest
    ): Response<ResponseBody>
}

interface GoogleTTSService {

    @POST("v1/text:synthesize")
    suspend fun synthesize(
        @retrofit2.http.Query("key") apiKey: String,
        @Body request: GoogleTTSRequest
    ): Response<GoogleTTSResponse>
}
