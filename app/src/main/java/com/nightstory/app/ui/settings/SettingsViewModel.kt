package com.nightstory.app.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nightstory.app.data.SettingsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsStore = SettingsStore(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _uiState.value = SettingsUiState(
            apiKey = settingsStore.geminiApiKey,
            model = settingsStore.geminiModel,
            language = settingsStore.storyLanguage,
            style = settingsStore.storyStyle,
            ttsProvider = settingsStore.ttsProvider,
            openaiTTSApiKey = settingsStore.openaiTTSApiKey,
            openaiTTSBaseUrl = settingsStore.openaiTTSBaseUrl,
            openaiTTSModel = settingsStore.openaiTTSModel,
            openaiTVoice = settingsStore.openaiTVoice,
            openaiTTSSpeed = settingsStore.openaiTTSSpeed,
            googleTTSApiKey = settingsStore.googleTTSApiKey,
            googleTVoice = settingsStore.googleTVoiceName,
            googleTTSLanguage = settingsStore.googleTTSLanguage
        )
    }

    fun updateApiKey(value: String) { _uiState.value = _uiState.value.copy(apiKey = value) }
    fun updateModel(value: String) { _uiState.value = _uiState.value.copy(model = value) }
    fun updateLanguage(value: String) { _uiState.value = _uiState.value.copy(language = value) }
    fun updateStyle(value: String) { _uiState.value = _uiState.value.copy(style = value) }

    fun updateTTSProvider(value: String) { _uiState.value = _uiState.value.copy(ttsProvider = value) }
    fun updateOpenaiTTSApiKey(value: String) { _uiState.value = _uiState.value.copy(openaiTTSApiKey = value) }
    fun updateOpenaiTTSBaseUrl(value: String) { _uiState.value = _uiState.value.copy(openaiTTSBaseUrl = value) }
    fun updateOpenaiTTSModel(value: String) { _uiState.value = _uiState.value.copy(openaiTTSModel = value) }
    fun updateOpenaiTVoice(value: String) { _uiState.value = _uiState.value.copy(openaiTVoice = value) }
    fun updateOpenaiTTSSpeed(value: Float) { _uiState.value = _uiState.value.copy(openaiTTSSpeed = value) }
    fun updateGoogleTTSApiKey(value: String) { _uiState.value = _uiState.value.copy(googleTTSApiKey = value) }
    fun updateGoogleTVoice(value: String) { _uiState.value = _uiState.value.copy(googleTVoice = value) }
    fun updateGoogleTTSLanguage(value: String) { _uiState.value = _uiState.value.copy(googleTTSLanguage = value) }

    fun save() {
        val state = _uiState.value
        settingsStore.geminiApiKey = state.apiKey.trim()
        settingsStore.geminiModel = state.model.trim()
        settingsStore.storyLanguage = state.language
        settingsStore.storyStyle = state.style
        settingsStore.ttsProvider = state.ttsProvider
        settingsStore.openaiTTSApiKey = state.openaiTTSApiKey.trim()
        settingsStore.openaiTTSBaseUrl = state.openaiTTSBaseUrl.trim()
        settingsStore.openaiTTSModel = state.openaiTTSModel.trim()
        settingsStore.openaiTVoice = state.openaiTVoice.trim()
        settingsStore.openaiTTSSpeed = state.openaiTTSSpeed
        settingsStore.googleTTSApiKey = state.googleTTSApiKey.trim()
        settingsStore.googleTVoiceName = state.googleTVoice.trim()
        settingsStore.googleTTSLanguage = state.googleTTSLanguage.trim()
        _uiState.value = state.copy(saved = true)
    }

    fun clearSaved() {
        _uiState.value = _uiState.value.copy(saved = false)
    }
}

data class SettingsUiState(
    // Gemini
    val apiKey: String = "",
    val model: String = "gemini-2.0-flash",
    val language: String = "English",
    val style: String = "Fairy Tale",
    // TTS
    val ttsProvider: String = "device",
    val openaiTTSApiKey: String = "",
    val openaiTTSBaseUrl: String = "https://api.openai.com/",
    val openaiTTSModel: String = "tts-1",
    val openaiTVoice: String = "nova",
    val openaiTTSSpeed: Float = 1.0f,
    val googleTTSApiKey: String = "",
    val googleTVoice: String = "en-US-Wavenet-D",
    val googleTTSLanguage: String = "en-US",
    // UI
    val saved: Boolean = false
)
