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

    // ===== API Configuration =====

    var apiEndpoint: String
        get() = prefs.getString(KEY_API_ENDPOINT, "") ?: ""
        set(value) = prefs.edit().putString(KEY_API_ENDPOINT, value).apply()

    var apiKey: String
        get() = prefs.getString(KEY_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_API_KEY, value).apply()

    var modelName: String
        get() = prefs.getString(KEY_MODEL_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_MODEL_NAME, value).apply()

    // ===== Story Preferences =====

    var storyLanguage: String
        get() = prefs.getString(KEY_STORY_LANGUAGE, "Persian") ?: "Persian"
        set(value) = prefs.edit().putString(KEY_STORY_LANGUAGE, value).apply()

    var storyStyle: String
        get() = prefs.getString(KEY_STORY_STYLE, "Fairy Tale") ?: "Fairy Tale"
        set(value) = prefs.edit().putString(KEY_STORY_STYLE, value).apply()

    var childGender: String
        get() = prefs.getString(KEY_CHILD_GENDER, "both") ?: "both"
        set(value) = prefs.edit().putString(KEY_CHILD_GENDER, value).apply()

    var ageRange: String
        get() = prefs.getString(KEY_AGE_RANGE, "3-5") ?: "3-5"
        set(value) = prefs.edit().putString(KEY_AGE_RANGE, value).apply()

    companion object {
        private const val KEY_API_ENDPOINT = "api_endpoint"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_MODEL_NAME = "model_name"
        private const val KEY_STORY_LANGUAGE = "story_language"
        private const val KEY_STORY_STYLE = "story_style"
        private const val KEY_CHILD_GENDER = "child_gender"
        private const val KEY_AGE_RANGE = "age_range"
    }
}

enum class ChildGender(val id: String) {
    BOY("boy"),
    GIRL("girl"),
    BOTH("both")
}

enum class AgeRange(val id: String, val label: String) {
    BABY("0-2", "۰ تا ۲ سال"),
    TODDLER("3-5", "۳ تا ۵ سال"),
    CHILD("6-8", "۶ تا ۸ سال"),
    OLDER("9-12", "۹ تا ۱۲ سال")
}
