package com.nightstory.app.ui.history

import android.app.Application
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.nightstory.app.data.SettingsStore
import com.nightstory.app.data.db.AppDatabase
import com.nightstory.app.data.db.StoryEntity
import com.nightstory.app.data.repository.TTSRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application, AppDatabase::class.java, "night_story_db"
    ).build()

    private val dao = db.storyDao()
    private val settingsStore = SettingsStore(application)
    private val ttsRepository = TTSRepository(settingsStore)

    val stories: StateFlow<List<StoryEntity>> = dao.getAllStories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _speakingStoryId = MutableStateFlow<Long?>(null)
    val speakingStoryId: StateFlow<Long?> = _speakingStoryId

    private val _isLoadingSpeech = MutableStateFlow<Long?>(null)
    val isLoadingSpeech: StateFlow<Long?> = _isLoadingSpeech

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
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) { _speakingStoryId.value = null }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) { _speakingStoryId.value = null }
        })
    }

    fun speakStory(story: StoryEntity) {
        if (_speakingStoryId.value == story.id) {
            stopSpeaking()
            return
        }

        val provider = settingsStore.ttsProvider
        if (provider == "device") {
            deviceTts?.speak(story.content, TextToSpeech.QUEUE_FLUSH, null, "story_${story.id}")
            _speakingStoryId.value = story.id
        } else {
            _isLoadingSpeech.value = story.id
            viewModelScope.launch {
                ttsRepository.generateSpeech(story.content)
                    .onSuccess { audioBytes ->
                        playAudioBytes(audioBytes, story.id)
                    }
                    .onFailure {
                        _isLoadingSpeech.value = null
                        // Fallback to device TTS
                        deviceTts?.speak(story.content, TextToSpeech.QUEUE_FLUSH, null, "story_${story.id}")
                        _speakingStoryId.value = story.id
                    }
            }
        }
    }

    private fun playAudioBytes(data: ByteArray, storyId: Long) {
        try {
            val tempFile = File.createTempFile("story_", ".mp3", getApplication<Application>().cacheDir)
            tempFile.writeBytes(data)
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(tempFile.absolutePath)
                prepare()
                start()
                setOnCompletionListener {
                    _speakingStoryId.value = null
                    _isLoadingSpeech.value = null
                    it.release()
                    tempFile.delete()
                }
            }
            _speakingStoryId.value = storyId
            _isLoadingSpeech.value = null
        } catch (_: Exception) {
            _isLoadingSpeech.value = null
        }
    }

    fun stopSpeaking() {
        deviceTts?.stop()
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
        _speakingStoryId.value = null
    }

    fun deleteStory(story: StoryEntity) {
        viewModelScope.launch { dao.delete(story) }
    }

    fun deleteAll() {
        viewModelScope.launch { dao.deleteAll() }
    }

    override fun onCleared() {
        deviceTts?.stop()
        deviceTts?.shutdown()
        mediaPlayer?.release()
    }
}
