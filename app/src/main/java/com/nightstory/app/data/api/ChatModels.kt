package com.nightstory.app.data.api

import com.google.gson.annotations.SerializedName

// ===== OpenAI-compatible Chat Completions API =====

data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    @SerializedName("max_tokens")
    val maxTokens: Int = 2048,
    val temperature: Double = 0.9
)

data class ChatMessage(
    val role: String, // "system" or "user"
    val content: String
)

data class ChatResponse(
    val choices: List<Choice>?
)

data class Choice(
    val message: ChatMessage?
)
