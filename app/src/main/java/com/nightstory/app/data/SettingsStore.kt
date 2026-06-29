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

    var geminiApiKey: String
        get() = prefs.getString(KEY_GEMINI_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_GEMINI_API_KEY, value).apply()

    var geminiModel: String
        get() = prefs.getString(KEY_GEMINI_MODEL, "gemini-2.0-flash") ?: "gemini-2.0-flash"
        set(value) = prefs.edit().putString(KEY_GEMINI_MODEL, value).apply()

    var storyLanguage: String
        get() = prefs.getString(KEY_STORY_LANGUAGE, "English") ?: "English"
        set(value) = prefs.edit().putString(KEY_STORY_LANGUAGE, value).apply()

    var storyStyle: String
        get() = prefs.getString(KEY_STORY_STYLE, "Fairy Tale") ?: "Fairy Tale"
        set(value) = prefs.edit().putString(KEY_STORY_STYLE, value).apply()

    companion object {
        private const val KEY_GEMINI_API_KEY = "gemini_api_key"
        private const val KEY_GEMINI_MODEL = "gemini_model"
        private const val KEY_STORY_LANGUAGE = "story_language"
        private const val KEY_STORY_STYLE = "story_style"
    }
}
