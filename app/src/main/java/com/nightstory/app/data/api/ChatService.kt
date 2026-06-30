package com.nightstory.app.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ChatService {

    @POST("v1/chat/completions")
    suspend fun chat(
        @Header("Authorization") auth: String,
        @Body request: ChatRequest
    ): Response<ChatResponse>

    @GET("v1/models")
    suspend fun listModels(
        @Header("Authorization") auth: String
    ): Response<ModelsResponse>
}

data class ModelsResponse(
    val data: List<ModelInfo>?
)

data class ModelInfo(
    val id: String
)
