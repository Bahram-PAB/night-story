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
            style = settingsStore.storyStyle
        )
    }

    fun updateApiKey(value: String) {
        _uiState.value = _uiState.value.copy(apiKey = value)
    }

    fun updateModel(value: String) {
        _uiState.value = _uiState.value.copy(model = value)
    }

    fun updateLanguage(value: String) {
        _uiState.value = _uiState.value.copy(language = value)
    }

    fun updateStyle(value: String) {
        _uiState.value = _uiState.value.copy(style = value)
    }

    fun save() {
        val state = _uiState.value
        settingsStore.geminiApiKey = state.apiKey.trim()
        settingsStore.geminiModel = state.model.trim()
        settingsStore.storyLanguage = state.language
        settingsStore.storyStyle = state.style
        _uiState.value = state.copy(saved = true)
    }

    fun clearSaved() {
        _uiState.value = _uiState.value.copy(saved = false)
    }
}

data class SettingsUiState(
    val apiKey: String = "",
    val model: String = "gemini-2.0-flash",
    val language: String = "English",
    val style: String = "Fairy Tale",
    val saved: Boolean = false
)
