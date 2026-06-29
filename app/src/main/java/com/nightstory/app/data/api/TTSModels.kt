package com.nightstory.app.data.api

import com.google.gson.annotations.SerializedName

// ===== OpenAI-compatible TTS API Models =====

data class OpenAITTSRequest(
    val model: String = "tts-1",
    val input: String,
    val voice: String = "nova",
    @SerializedName("response_format")
    val responseFormat: String = "mp3",
    val speed: Double = 1.0
)

// ===== Google Cloud TTS API Models =====

data class GoogleTTSRequest(
    val input: SynthesisInput,
    val voice: VoiceSelectionParams,
    @SerializedName("audioConfig")
    val audioConfig: AudioConfig
)

data class SynthesisInput(
    val text: String
)

data class VoiceSelectionParams(
    @SerializedName("languageCode")
    val languageCode: String = "en-US",
    @SerializedName("name")
    val name: String = "en-US-Wavenet-D",
    @SerializedName("ssmlGender")
    val ssmlGender: String = "NEUTRAL"
)

data class AudioConfig(
    @SerializedName("audioEncoding")
    val audioEncoding: String = "MP3",
    @SerializedName("speakingRate")
    val speakingRate: Double = 0.9,
    @SerializedName("pitch")
    val pitch: Double = 1.0
)

data class GoogleTTSResponse(
    @SerializedName("audioContent")
    val audioContent: String? // base64 encoded
)
