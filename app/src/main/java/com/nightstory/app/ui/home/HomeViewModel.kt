package com.nightstory.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.nightstory.app.data.SettingsStore
import com.nightstory.app.data.db.AppDatabase
import com.nightstory.app.data.repository.StoryRepository
import com.nightstory.app.domain.model.Story
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application, AppDatabase::class.java, "night_story_db"
    ).build()

    private val settingsStore = SettingsStore(application)
    private val repository = StoryRepository(db.storyDao(), settingsStore)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun updateCustomPrompt(prompt: String) {
        _uiState.value = _uiState.value.copy(customPrompt = prompt)
    }

    fun generateRandom() {
        if (_uiState.value.isGenerating) return
        _uiState.value = _uiState.value.copy(isGenerating = true, error = null)

        viewModelScope.launch {
            repository.generateRandomStory()
                .onSuccess { story ->
                    _uiState.value = _uiState.value.copy(
                        isGenerating = false,
                        currentStory = story,
                        error = null
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isGenerating = false,
                        error = e.message ?: "مشکلی پیش آمد"
                    )
                }
        }
    }

    fun generateCustom() {
        val prompt = _uiState.value.customPrompt
        if (prompt.isBlank() || _uiState.value.isGenerating) return
        _uiState.value = _uiState.value.copy(isGenerating = true, error = null)

        viewModelScope.launch {
            repository.generateCustomStory(prompt)
                .onSuccess { story ->
                    _uiState.value = _uiState.value.copy(
                        isGenerating = false,
                        currentStory = story,
                        error = null,
                        showCustomInput = false
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isGenerating = false,
                        error = e.message ?: "مشکلی پیش آمد"
                    )
                }
        }
    }

    fun toggleCustomInput() {
        _uiState.value = _uiState.value.copy(showCustomInput = !_uiState.value.showCustomInput)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearStory() {
        _uiState.value = _uiState.value.copy(currentStory = null, customPrompt = "", showCustomInput = false)
    }
}

data class HomeUiState(
    val customPrompt: String = "",
    val isGenerating: Boolean = false,
    val showCustomInput: Boolean = false,
    val currentStory: Story? = null,
    val error: String? = null
)
