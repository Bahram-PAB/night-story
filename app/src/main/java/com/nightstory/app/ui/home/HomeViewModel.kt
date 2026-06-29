package com.nightstory.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nightstory.app.data.SettingsStore
import com.nightstory.app.data.db.AppDatabase
import com.nightstory.app.data.repository.StoryRepository
import com.nightstory.app.domain.model.Story
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.room.Room

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application, AppDatabase::class.java, "night_story_db"
    ).build()

    private val settingsStore = SettingsStore(application)
    private val repository = StoryRepository(db.storyDao(), settingsStore)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun updatePrompt(prompt: String) {
        _uiState.value = _uiState.value.copy(prompt = prompt)
    }

    fun generateStory() {
        val state = _uiState.value
        if (state.prompt.isBlank() || state.isGenerating) return

        _uiState.value = state.copy(isGenerating = true, error = null)

        viewModelScope.launch {
            repository.generateStory(state.prompt)
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
                        error = e.message ?: "Something went wrong"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearStory() {
        _uiState.value = _uiState.value.copy(currentStory = null, prompt = "")
    }
}

data class HomeUiState(
    val prompt: String = "",
    val isGenerating: Boolean = false,
    val currentStory: Story? = null,
    val error: String? = null
)
