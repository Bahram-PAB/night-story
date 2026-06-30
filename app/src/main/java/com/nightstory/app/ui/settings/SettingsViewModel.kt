package com.nightstory.app.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.nightstory.app.data.SettingsStore
import com.nightstory.app.data.db.AppDatabase
import com.nightstory.app.data.repository.StoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsStore = SettingsStore(application)
    private val db = Room.databaseBuilder(application, AppDatabase::class.java, "night_story_db").build()
    private val repository = StoryRepository(db.storyDao(), settingsStore)

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

    fun testConnection() {
        val state = _uiState.value
        if (state.apiEndpoint.isBlank() || state.apiKey.isBlank()) {
            _uiState.value = state.copy(testResult = TestResult.Error("آدرس سرور و کلید API را وارد کنید"))
            return
        }

        // Save first so repository uses latest values
        settingsStore.apiEndpoint = state.apiEndpoint.trim()
        settingsStore.apiKey = state.apiKey.trim()

        _uiState.value = _uiState.value.copy(isTesting = true, testResult = null)

        viewModelScope.launch {
            repository.testConnection()
                .onSuccess { models ->
                    _uiState.value = _uiState.value.copy(
                        isTesting = false,
                        testResult = TestResult.Success(models.size),
                        availableModels = models
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isTesting = false,
                        testResult = TestResult.Error(e.message ?: "خطای ناشناخته")
                    )
                }
        }
    }

    fun selectModel(model: String) {
        _uiState.value = _uiState.value.copy(modelName = model)
    }

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

    fun clearTestResult() {
        _uiState.value = _uiState.value.copy(testResult = null)
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
    val saved: Boolean = false,
    val isTesting: Boolean = false,
    val testResult: TestResult? = null,
    val availableModels: List<String> = emptyList()
)

sealed class TestResult {
    data class Success(val modelCount: Int) : TestResult()
    data class Error(val message: String) : TestResult()
}
