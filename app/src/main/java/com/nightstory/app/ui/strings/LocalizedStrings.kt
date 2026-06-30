package com.nightstory.app.ui.strings

data class LocalizedStrings(
    // App
    val appName: String,
    val appTagline: String,

    // Bottom Navigation
    val navHome: String,
    val navHistory: String,
    val navSettings: String,

    // Home Screen
    val promptLabel: String,
    val promptPlaceholder: String,
    val generateButton: String,
    val generatingText: String,
    val readAloud: String,
    val stopButton: String,
    val generatingVoice: String,
    val newStory: String,
    val promptPrefix: String,

    // History Screen
    val storyLibrary: String,
    val storiesSaved: String, // use %d for count
    val noStoriesYet: String,
    val noStoriesHint: String,
    val deleteStory: String,
    val deleteStoryConfirm: String, // use %s for title
    val deleteAll: String,
    val deleteAllConfirm: String,
    val readMore: String,
    val showLess: String,
    val cancel: String,
    val delete: String,

    // Settings Screen
    val settingsTitle: String,
    val settingsSubtitle: String,
    val storyGeneration: String,
    val geminiApiKey: String,
    val geminiApiKeyPlaceholder: String,
    val geminiApiKeyHelper: String,
    val geminiModel: String,
    val storyPreferences: String,
    val storyLanguage: String,
    val storyStyle: String,
    val voiceSection: String,
    val voiceSectionSubtitle: String,
    val ttsProvider: String,
    val deviceVoice: String,
    val openaiTTS: String,
    val googleCloudTTS: String,
    val openaiApiKey: String,
    val openaiApiKeyPlaceholder: String,
    val openaiApiKeyHelper: String,
    val apiBaseUrl: String,
    val ttsModel: String,
    val voice: String,
    val speed: String,
    val googleCloudApiKey: String,
    val googleApiKeyPlaceholder: String,
    val googleApiKeyHelper: String,
    val language: String,
    val saveButton: String,
    val savedButton: String,
    val toggleVisibility: String,

    // Story Styles
    val styleFairyTale: String,
    val styleAdventure: String,
    val styleFunny: String,
    val styleSpooky: String,
    val styleAnimal: String,
    val styleSpace: String,
    val stylePrincess: String,
    val styleDinosaur: String,
    val styleOcean: String,
    val styleMagic: String,

    // Language Names (as they appear in the dropdown)
    val langEnglish: String,
    val langSpanish: String,
    val langFrench: String,
    val langGerman: String,
    val langPortuguese: String,
    val langChinese: String,
    val langJapanese: String,
    val langKorean: String,
    val langArabic: String,
    val langPersian: String,
    val langHindi: String,
    val langTurkish: String,
    val langItalian: String,
    val langRussian: String,
    val langDutch: String
)
