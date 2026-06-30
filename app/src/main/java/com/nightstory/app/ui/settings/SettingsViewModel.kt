package com.nightstory.app.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nightstory.app.data.SettingsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsStore = SettingsStore(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        val s = settingsStore
        _uiState.value = SettingsUiState(
            apiEndpoint = s.apiEndpoint,
            apiKey = s.apiKey,
            modelName = s.modelName,
            language = s.storyLanguage,
            style = s.storyStyle,
            gender = s.childGender,
            ageRange = s.ageRange
        )
    }

    fun updateApiEndpoint(v: String) { _uiState.value = _uiState.value.copy(apiEndpoint = v) }
    fun updateApiKey(v: String) { _uiState.value = _uiState.value.copy(apiKey = v) }
    fun updateModelName(v: String) { _uiState.value = _uiState.value.copy(modelName = v) }
    fun updateLanguage(v: String) { _uiState.value = _uiState.value.copy(language = v) }
    fun updateStyle(v: String) { _uiState.value = _uiState.value.copy(style = v) }
    fun updateGender(v: String) { _uiState.value = _uiState.value.copy(gender = v) }
    fun updateAgeRange(v: String) { _uiState.value = _uiState.value.copy(ageRange = v) }

    fun save() {
        val state = _uiState.value
        settingsStore.apiEndpoint = state.apiEndpoint.trim()
        settingsStore.apiKey = state.apiKey.trim()
        settingsStore.modelName = state.modelName.trim()
        settingsStore.storyLanguage = state.language
        settingsStore.storyStyle = state.style
        settingsStore.childGender = state.gender
        settingsStore.ageRange = state.ageRange
        _uiState.value = state.copy(saved = true)
    }

    fun clearSaved() {
        _uiState.value = _uiState.value.copy(saved = false)
    }
}

data class SettingsUiState(
    val apiEndpoint: String = "",
    val apiKey: String = "",
    val modelName: String = "",
    val language: String = "Persian",
    val style: String = "Fairy Tale",
    val gender: String = "both",
    val ageRange: String = "3-5",
    val saved: Boolean = false
)
