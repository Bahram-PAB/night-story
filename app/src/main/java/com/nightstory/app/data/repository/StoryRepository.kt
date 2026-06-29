package com.nightstory.app.data.repository

import com.nightstory.app.data.SettingsStore
import com.nightstory.app.data.api.ApiClient
import com.nightstory.app.data.api.GeminiContent
import com.nightstory.app.data.api.GeminiPart
import com.nightstory.app.data.api.GeminiRequest
import com.nightstory.app.data.api.GenerationConfig
import com.nightstory.app.data.db.StoryDao
import com.nightstory.app.data.db.StoryEntity
import com.nightstory.app.domain.model.Story
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoryRepository(
    private val storyDao: StoryDao,
    private val settingsStore: SettingsStore
) {

    val allStories: Flow<List<Story>> = storyDao.getAllStories().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun generateStory(prompt: String): Result<Story> = runCatching {
        val apiKey = settingsStore.geminiApiKey
        require(apiKey.isNotBlank()) { "Please set your Gemini API key in Settings" }

        val systemPrompt = buildSystemPrompt()
        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    role = "user",
                    parts = listOf(GeminiPart("$systemPrompt\n\nUser prompt: $prompt"))
                )
            ),
            generationConfig = GenerationConfig(
                temperature = 0.9,
                maxOutputTokens = 2048
            )
        )

        val response = ApiClient.geminiService.generateContent(
            model = settingsStore.geminiModel,
            apiKey = apiKey,
            request = request
        )

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string() ?: "Unknown error"
            throw RuntimeException("API Error (${response.code()}): $errorBody")
        }

        val geminiResponse = response.body()
            ?: throw RuntimeException("Empty response from API")

        val content = geminiResponse.candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
            ?: throw RuntimeException("No story generated. The response may have been blocked by safety filters.")

        val cleanContent = cleanStoryText(content)
        val title = extractTitle(cleanContent, prompt)

        val entity = StoryEntity(
            title = title,
            prompt = prompt,
            content = cleanContent,
            createdAt = System.currentTimeMillis()
        )
        val id = storyDao.insert(entity)

        Story(
            id = id,
            title = title,
            prompt = prompt,
            content = cleanContent,
            createdAt = entity.createdAt
        )
    }

    suspend fun deleteStory(story: Story) {
        storyDao.getStoryById(story.id)?.let { entity ->
            storyDao.delete(entity)
        }
    }

    private fun buildSystemPrompt(): String {
        val language = settingsStore.storyLanguage
        val style = settingsStore.storyStyle
        val isRTL = language in listOf("Persian", "Arabic")

        val rtlNote = if (isRTL) {
            "\nIMPORTANT: Write entirely in $language script (not transliterated). Use proper $language grammar and vocabulary."
        } else ""

        return """You are a wonderful children's story writer.

Your task: Write a short bedtime story for kids (ages 3-8).

Rules:
- Write in $language$rtlNote
- Story style: $style
- Keep it 150-300 words
- Use simple, easy-to-understand language
- Include vivid descriptions and friendly characters
- Have a happy, comforting ending
- Make it suitable for bedtime reading
- Start directly with the story (no title, no preamble)

Format: Just the story text, nothing else."""
    }

    private fun cleanStoryText(raw: String): String {
        return raw.trim()
            .removePrefix("```")
            .removeSuffix("```")
            .removePrefix("```text")
            .trim()
    }

    private fun extractTitle(content: String, prompt: String): String {
        // Try to use first line if it looks like a title
        val firstLine = content.lines().first().trim()
        if (firstLine.length in 3..60 && !firstLine.endsWith(".")) {
            return firstLine
        }
        // Fallback: create title from prompt
        return prompt.take(50).let {
            if (it.length == 50) "$it..." else it
        }
    }

    private fun StoryEntity.toDomain() = Story(
        id = id,
        title = title,
        prompt = prompt,
        content = content,
        createdAt = createdAt
    )
}
