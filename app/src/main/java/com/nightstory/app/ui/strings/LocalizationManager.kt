package com.nightstory.app.ui.strings

import androidx.compose.runtime.compositionLocalOf

val LocalStrings = compositionLocalOf { englishStrings }

object LocalizationManager {

    fun getStrings(language: String): LocalizedStrings {
        return when (language) {
            "Persian" -> persianStrings
            "Arabic" -> arabicStrings
            "Spanish" -> spanishStrings
            "French" -> frenchStrings
            "German" -> germanStrings
            "Portuguese" -> portugueseStrings
            "Chinese" -> chineseStrings
            "Japanese" -> japaneseStrings
            "Korean" -> koreanStrings
            "Hindi" -> hindiStrings
            "Turkish" -> turkishStrings
            "Italian" -> italianStrings
            "Russian" -> russianStrings
            "Dutch" -> dutchStrings
            else -> englishStrings
        }
    }

    fun getLanguageName(language: String): String {
        return when (language) {
            "Persian" -> "فارسی"
            "Arabic" -> "العربية"
            "Spanish" -> "Español"
            "French" -> "Français"
            "German" -> "Deutsch"
            "Portuguese" -> "Português"
            "Chinese" -> "中文"
            "Japanese" -> "日本語"
            "Korean" -> "한국어"
            "Hindi" -> "हिन्दी"
            "Turkish" -> "Türkçe"
            "Italian" -> "Italiano"
            "Russian" -> "Русский"
            "Dutch" -> "Nederlands"
            else -> "English"
        }
    }

    fun getLocalizedLanguageList(): List<Pair<String, String>> {
        return listOf(
            "English" to "English",
            "Spanish" to "Español",
            "French" to "Français",
            "German" to "Deutsch",
            "Portuguese" to "Português",
            "Chinese" to "中文",
            "Japanese" to "日本語",
            "Korean" to "한국어",
            "Arabic" to "العربية",
            "Persian" to "فارسی",
            "Hindi" to "हिन्दी",
            "Turkish" to "Türkçe",
            "Italian" to "Italiano",
            "Russian" to "Русский",
            "Dutch" to "Nederlands"
        )
    }

    fun getLocalizedStyleList(s: LocalizedStrings): List<String> {
        return listOf(
            s.styleFairyTale, s.styleAdventure, s.styleFunny, s.styleSpooky,
            s.styleAnimal, s.styleSpace, s.stylePrincess, s.styleDinosaur,
            s.styleOcean, s.styleMagic
        )
    }

    fun getStyleId(displayName: String, s: LocalizedStrings): String {
        return when (displayName) {
            s.styleFairyTale -> "Fairy Tale"
            s.styleAdventure -> "Adventure"
            s.styleFunny -> "Funny"
            s.styleSpooky -> "Spooky (mild)"
            s.styleAnimal -> "Animal Story"
            s.styleSpace -> "Space Adventure"
            s.stylePrincess -> "Princess & Knight"
            s.styleDinosaur -> "Dinosaur Story"
            s.styleOcean -> "Ocean Adventure"
            s.styleMagic -> "Magic & Wizards"
            else -> displayName
        }
    }

    fun getStyleDisplay(styleId: String, s: LocalizedStrings): String {
        return when (styleId) {
            "Fairy Tale" -> s.styleFairyTale
            "Adventure" -> s.styleAdventure
            "Funny" -> s.styleFunny
            "Spooky (mild)" -> s.styleSpooky
            "Animal Story" -> s.styleAnimal
            "Space Adventure" -> s.styleSpace
            "Princess & Knight" -> s.stylePrincess
            "Dinosaur Story" -> s.styleDinosaur
            "Ocean Adventure" -> s.styleOcean
            "Magic & Wizards" -> s.styleMagic
            else -> styleId
        }
    }
}
