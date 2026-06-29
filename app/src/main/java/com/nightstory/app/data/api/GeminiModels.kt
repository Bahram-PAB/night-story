package com.nightstory.app.data.api

import com.google.gson.annotations.SerializedName

// ===== Gemini API Models =====

data class GeminiRequest(
    val contents: List<GeminiContent>,
    @SerializedName("generation_config")
    val generationConfig: GenerationConfig? = null
)

data class GeminiContent(
    val role: String = "user",
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String
)

data class GenerationConfig(
    @SerializedName("temperature")
    val temperature: Double = 0.8,
    @SerializedName("max_output_tokens")
    val maxOutputTokens: Int = 2048
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    val content: GeminiContent?
)
