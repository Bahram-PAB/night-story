package com.nightstory.app.ui.history

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.nightstory.app.data.db.AppDatabase
import com.nightstory.app.data.db.StoryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(application, AppDatabase::class.java, "night_story_db").build()
    private val dao = db.storyDao()

    val stories: StateFlow<List<StoryEntity>> = dao.getAllStories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _speakingStoryId = MutableStateFlow<Long?>(null)
    val speakingStoryId: StateFlow<Long?> = _speakingStoryId

    private var deviceTts: TextToSpeech? = null

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
        } else {
            deviceTts?.speak(story.content, TextToSpeech.QUEUE_FLUSH, null, "story_${story.id}")
            _speakingStoryId.value = story.id
        }
    }

    fun stopSpeaking() {
        deviceTts?.stop()
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
    }
}
