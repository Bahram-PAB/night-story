package com.nightstory.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SettingsStore(context: Context) {

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        "night_story_prefs",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // ===== Gemini (Story Generation) =====

    var geminiApiKey: String
        get() = prefs.getString(KEY_GEMINI_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_GEMINI_API_KEY, value).apply()

    var geminiModel: String
        get() = prefs.getString(KEY_GEMINI_MODEL, "gemini-2.0-flash") ?: "gemini-2.0-flash"
        set(value) = prefs.edit().putString(KEY_GEMINI_MODEL, value).apply()

    // ===== Story Preferences =====

    var storyLanguage: String
        get() = prefs.getString(KEY_STORY_LANGUAGE, "English") ?: "English"
        set(value) = prefs.edit().putString(KEY_STORY_LANGUAGE, value).apply()

    var storyStyle: String
        get() = prefs.getString(KEY_STORY_STYLE, "Fairy Tale") ?: "Fairy Tale"
        set(value) = prefs.edit().putString(KEY_STORY_STYLE, value).apply()

    // ===== TTS Provider =====

    var ttsProvider: String
        get() = prefs.getString(KEY_TTS_PROVIDER, "device") ?: "device"
        set(value) = prefs.edit().putString(KEY_TTS_PROVIDER, value).apply()

    // OpenAI-compatible TTS (works with OpenAI, Groq, etc.)
    var openaiTTSApiKey: String
        get() = prefs.getString(KEY_OPENAI_TTS_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_OPENAI_TTS_API_KEY, value).apply()

    var openaiTTSBaseUrl: String
        get() = prefs.getString(KEY_OPENAI_TTS_BASE_URL, "https://api.openai.com/") ?: "https://api.openai.com/"
        set(value) = prefs.edit().putString(KEY_OPENAI_TTS_BASE_URL, value).apply()

    var openaiTTSModel: String
        get() = prefs.getString(KEY_OPENAI_TTS_MODEL, "tts-1") ?: "tts-1"
        set(value) = prefs.edit().putString(KEY_OPENAI_TTS_MODEL, value).apply()

    var openaiTVoice: String
        get() = prefs.getString(KEY_OPENAI_TTS_VOICE, "nova") ?: "nova"
        set(value) = prefs.edit().putString(KEY_OPENAI_TTS_VOICE, value).apply()

    var openaiTTSSpeed: Float
        get() = prefs.getFloat(KEY_OPENAI_TTS_SPEED, 1.0f)
        set(value) = prefs.edit().putFloat(KEY_OPENAI_TTS_SPEED, value).apply()

    // Google Cloud TTS
    var googleTTSApiKey: String
        get() = prefs.getString(KEY_GOOGLE_TTS_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_GOOGLE_TTS_API_KEY, value).apply()

    var googleTVoiceName: String
        get() = prefs.getString(KEY_GOOGLE_TTS_VOICE, "en-US-Wavenet-D") ?: "en-US-Wavenet-D"
        set(value) = prefs.edit().putString(KEY_GOOGLE_TTS_VOICE, value).apply()

    var googleTTSLanguage: String
        get() = prefs.getString(KEY_GOOGLE_TTS_LANG, "en-US") ?: "en-US"
        set(value) = prefs.edit().putString(KEY_GOOGLE_TTS_LANG, value).apply()

    companion object {
        private const val KEY_GEMINI_API_KEY = "gemini_api_key"
        private const val KEY_GEMINI_MODEL = "gemini_model"
        private const val KEY_STORY_LANGUAGE = "story_language"
        private const val KEY_STORY_STYLE = "story_style"
        private const val KEY_TTS_PROVIDER = "tts_provider"
        private const val KEY_OPENAI_TTS_API_KEY = "openai_tts_api_key"
        private const val KEY_OPENAI_TTS_BASE_URL = "openai_tts_base_url"
        private const val KEY_OPENAI_TTS_MODEL = "openai_tts_model"
        private const val KEY_OPENAI_TTS_VOICE = "openai_tts_voice"
        private const val KEY_OPENAI_TTS_SPEED = "openai_tts_speed"
        private const val KEY_GOOGLE_TTS_API_KEY = "google_tts_api_key"
        private const val KEY_GOOGLE_TTS_VOICE = "google_tts_voice"
        private const val KEY_GOOGLE_TTS_LANG = "google_tts_lang"
    }
}

enum class TTSProvider(val id: String, val label: String) {
    DEVICE("device", "Device Voice (Free)"),
    OPENAI("openai", "OpenAI TTS"),
    GOOGLE("google", "Google Cloud TTS")
}
