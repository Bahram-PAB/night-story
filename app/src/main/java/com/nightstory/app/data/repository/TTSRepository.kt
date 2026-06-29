package com.nightstory.app.data.repository

import android.util.Base64
import com.nightstory.app.data.SettingsStore
import com.nightstory.app.data.api.*

class TTSRepository(private val settingsStore: SettingsStore) {

    suspend fun generateSpeech(text: String): Result<ByteArray> {
        return when (settingsStore.ttsProvider) {
            "openai" -> generateOpenAISpeech(text)
            "google" -> generateGoogleSpeech(text)
            else -> Result.failure(IllegalStateException("Use device TTS for this provider"))
        }
    }

    private suspend fun generateOpenAISpeech(text: String): Result<ByteArray> {
        val apiKey = settingsStore.openaiTTSApiKey
        if (apiKey.isBlank()) {
            return Result.failure(IllegalStateException("Please set your OpenAI TTS API key in Settings"))
        }

        return try {
            val service = TTSClient.createOpenAIService(settingsStore.openaiTTSBaseUrl)
            val request = OpenAITTSRequest(
                model = settingsStore.openaiTTSModel,
                input = text.take(4096),
                voice = settingsStore.openaiTVoice,
                speed = settingsStore.openaiTTSSpeed.toDouble()
            )

            val response = service.generateSpeech(
                auth = "Bearer $apiKey",
                request = request
            )

            if (response.isSuccessful) {
                val bytes = response.body()?.bytes()
                    ?: return Result.failure(RuntimeException("Empty audio response"))
                Result.success(bytes)
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(RuntimeException("TTS API Error (${response.code()}): $error"))
            }
        } catch (e: Exception) {
            Result.failure(RuntimeException("Network error: ${e.message}"))
        }
    }

    private suspend fun generateGoogleSpeech(text: String): Result<ByteArray> {
        val apiKey = settingsStore.googleTTSApiKey
        if (apiKey.isBlank()) {
            return Result.failure(IllegalStateException("Please set your Google Cloud TTS API key in Settings"))
        }

        return try {
            val service = TTSClient.createGoogleTTSService()
            val languageCode = settingsStore.googleTTSLanguage

            val request = GoogleTTSRequest(
                input = SynthesisInput(text = text.take(5000)),
                voice = VoiceSelectionParams(
                    languageCode = languageCode,
                    name = settingsStore.googleTVoiceName,
                    ssmlGender = "NEUTRAL"
                ),
                audioConfig = AudioConfig(
                    audioEncoding = "MP3",
                    speakingRate = 0.9
                )
            )

            val response = service.synthesize(
                apiKey = apiKey,
                request = request
            )

            if (response.isSuccessful) {
                val audioBase64 = response.body()?.audioContent
                    ?: return Result.failure(RuntimeException("Empty audio response"))
                val bytes = Base64.decode(audioBase64, Base64.DEFAULT)
                Result.success(bytes)
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(RuntimeException("Google TTS Error (${response.code()}): $error"))
            }
        } catch (e: Exception) {
            Result.failure(RuntimeException("Network error: ${e.message}"))
        }
    }
}
