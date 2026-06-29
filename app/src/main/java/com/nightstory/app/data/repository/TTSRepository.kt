package com.nightstory.app.data.repository

import android.util.Base64
import com.nightstory.app.data.SettingsStore
import com.nightstory.app.data.api.*

class TTSRepository(private val settingsStore: SettingsStore) {

    sealed class TTSResult {
        data class Success(val audioBytes: ByteArray) : TTSResult()
        data class Error(val message: String) : TTSResult()
    }

    suspend fun generateSpeech(text: String): TTSResult {
        return when (settingsStore.ttsProvider) {
            "openai" -> generateOpenAISpeech(text)
            "google" -> generateGoogleSpeech(text)
            else -> TTSResult.Error("Use device TTS for this provider")
        }
    }

    private suspend fun generateOpenAISpeech(text: String): TTSResult {
        val apiKey = settingsStore.openaiTTSApiKey
        if (apiKey.isBlank()) {
            return TTSResult.Error("Please set your OpenAI TTS API key in Settings")
        }

        return try {
            val service = TTSClient.createOpenAIService(settingsStore.openaiTTSBaseUrl)
            val request = OpenAITTSRequest(
                model = settingsStore.openaiTTSModel,
                input = text.take(4096), // OpenAI TTS limit
                voice = settingsStore.openaiTVoice,
                speed = settingsStore.openaiTTSSpeed.toDouble()
            )

            val response = service.generateSpeech(
                auth = "Bearer $apiKey",
                request = request
            )

            if (response.isSuccessful) {
                val bytes = response.body()?.bytes()
                    ?: return TTSResult.Error("Empty audio response")
                TTSResult.Success(bytes)
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                TTSResult.Error("TTS API Error (${response.code()}): $error")
            }
        } catch (e: Exception) {
            TTSResult.Error("Network error: ${e.message}")
        }
    }

    private suspend fun generateGoogleSpeech(text: String): TTSResult {
        val apiKey = settingsStore.googleTTSApiKey
        if (apiKey.isBlank()) {
            return TTSResult.Error("Please set your Google Cloud TTS API key in Settings")
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
                    ?: return TTSResult.Error("Empty audio response")
                val bytes = Base64.decode(audioBase64, Base64.DEFAULT)
                TTSResult.Success(bytes)
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                TTSResult.Error("Google TTS Error (${response.code()}): $error")
            }
        } catch (e: Exception) {
            TTSResult.Error("Network error: ${e.message}")
        }
    }
}
