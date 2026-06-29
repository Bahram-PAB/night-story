package com.nightstory.app.ui.home

import android.app.Application
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nightstory.app.data.SettingsStore
import com.nightstory.app.data.db.AppDatabase
import com.nightstory.app.data.repository.StoryRepository
import com.nightstory.app.data.repository.TTSRepository
import com.nightstory.app.domain.model.Story
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.room.Room
import java.io.File
import java.util.Locale

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application, AppDatabase::class.java, "night_story_db"
    ).build()

    private val settingsStore = SettingsStore(application)
    private val repository = StoryRepository(db.storyDao(), settingsStore)
    private val ttsRepository = TTSRepository(settingsStore)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Device TTS
    private var deviceTts: TextToSpeech? = null
    private var mediaPlayer: MediaPlayer? = null

    init {
        deviceTts = TextToSpeech(application) { status ->
            if (status == TextToSpeech.SUCCESS) {
                deviceTts?.language = Locale.US
                deviceTts?.setSpeechRate(0.85f)
                deviceTts?.setPitch(1.1f)
            }
        }
        deviceTts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _uiState.value = _uiState.value.copy(isSpeaking = true)
            }
            override fun onDone(utteranceId: String?) {
                _uiState.value = _uiState.value.copy(isSpeaking = false)
            }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _uiState.value = _uiState.value.copy(isSpeaking = false)
            }
        })
    }

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

    fun speakStory() {
        val story = _uiState.value.currentStory ?: return
        val provider = settingsStore.ttsProvider

        if (provider == "device") {
            // Use device TTS
            deviceTts?.speak(story.content, TextToSpeech.QUEUE_FLUSH, null, "story")
            _uiState.value = _uiState.value.copy(isSpeaking = true)
        } else {
            // Use API TTS
            _uiState.value = _uiState.value.copy(isGeneratingSpeech = true, error = null)
            viewModelScope.launch {
                ttsRepository.generateSpeech(story.content)
                    .onSuccess { audioBytes: ByteArray ->
                        playAudioBytes(audioBytes)
                    }
                    .onFailure { e: Throwable ->
                        _uiState.value = _uiState.value.copy(
                            isGeneratingSpeech = false,
                            error = e.message ?: "Failed to generate speech"
                        )
                    }
            }
        }
    }

    private fun playAudioBytes(data: ByteArray) {
        try {
            val tempFile = File.createTempFile("story_", ".mp3", getApplication<Application>().cacheDir)
            tempFile.writeBytes(data)

            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(tempFile.absolutePath)
                prepare()
                start()
                setOnCompletionListener {
                    _uiState.value = _uiState.value.copy(isSpeaking = false, isGeneratingSpeech = false)
                    it.release()
                    tempFile.delete()
                }
                setOnErrorListener { _, _, _ ->
                    _uiState.value = _uiState.value.copy(isSpeaking = false, isGeneratingSpeech = false)
                    tempFile.delete()
                    true
                }
            }
            _uiState.value = _uiState.value.copy(isSpeaking = true, isGeneratingSpeech = false)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isSpeaking = false,
                isGeneratingSpeech = false,
                error = "Audio playback error: ${e.message}"
            )
        }
    }

    fun stopSpeaking() {
        deviceTts?.stop()
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
        _uiState.value = _uiState.value.copy(isSpeaking = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearStory() {
        stopSpeaking()
        _uiState.value = _uiState.value.copy(currentStory = null, prompt = "")
    }

    override fun onCleared() {
        deviceTts?.stop()
        deviceTts?.shutdown()
        mediaPlayer?.release()
    }
}

data class HomeUiState(
    val prompt: String = "",
    val isGenerating: Boolean = false,
    val isSpeaking: Boolean = false,
    val isGeneratingSpeech: Boolean = false,
    val currentStory: Story? = null,
    val error: String? = null
)
