package com.nightstory.app.data.repository

import com.nightstory.app.data.SettingsStore
import com.nightstory.app.data.api.ChatClient
import com.nightstory.app.data.api.ChatMessage
import com.nightstory.app.data.api.ChatRequest
import com.nightstory.app.data.db.StoryDao
import com.nightstory.app.data.db.StoryEntity
import com.nightstory.app.domain.model.Story
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.random.Random

class StoryRepository(
    private val storyDao: StoryDao,
    private val settingsStore: SettingsStore
) {

    val allStories: Flow<List<Story>> = storyDao.getAllStories().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun generateRandomStory(): Result<Story> {
        val randomSeed = Random.nextInt(100000)
        val prompt = buildRandomPrompt(randomSeed)
        return generate(prompt, "random_$randomSeed")
    }

    suspend fun generateCustomStory(userPrompt: String): Result<Story> {
        val prompt = buildCustomPrompt(userPrompt)
        return generate(prompt, userPrompt)
    }

    private suspend fun generate(systemPrompt: String, displayPrompt: String): Result<Story> = runCatching {
        val endpoint = settingsStore.apiEndpoint
        require(endpoint.isNotBlank()) { "لطفاً آدرس سرور (Endpoint) را در تنظیمات وارد کنید" }

        val apiKey = settingsStore.apiKey
        require(apiKey.isNotBlank()) { "لطفاً کلید API را در تنظیمات وارد کنید" }

        val model = settingsStore.modelName
        require(model.isNotBlank()) { "لطفاً نام مدل را در تنظیمات وارد کنید" }

        val service = ChatClient.createService(endpoint)
        val request = ChatRequest(
            model = model,
            messages = listOf(
                ChatMessage("system", systemPrompt),
                ChatMessage("user", displayPrompt)
            ),
            temperature = 0.95
        )

        val response = service.chat(
            auth = "Bearer $apiKey",
            request = request
        )

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string() ?: "خطای ناشناخته"
            throw RuntimeException("خطای API (${response.code()}): $errorBody")
        }

        val chatResponse = response.body()
            ?: throw RuntimeException("پاسخ خالی از سرور")

        val content = chatResponse.choices
            ?.firstOrNull()
            ?.message
            ?.content
            ?.trim()
            ?: throw RuntimeException("داستانی تولید نشد. ممکن است توسط فیلترهای امنیتی مسدود شده باشد.")

        val cleanContent = cleanStoryText(content)
        val title = extractTitle(cleanContent, displayPrompt)

        val entity = StoryEntity(
            title = title,
            prompt = displayPrompt,
            content = cleanContent,
            createdAt = System.currentTimeMillis()
        )
        val id = storyDao.insert(entity)

        Story(
            id = id,
            title = title,
            prompt = displayPrompt,
            content = cleanContent,
            createdAt = entity.createdAt
        )
    }

    suspend fun deleteStory(story: Story) {
        storyDao.getStoryById(story.id)?.let { storyDao.delete(it) }
    }

    suspend fun testConnection(): Result<List<String>> = runCatching {
        val endpoint = settingsStore.apiEndpoint
        require(endpoint.isNotBlank()) { "آدرس سرور وارد نشده" }

        val apiKey = settingsStore.apiKey
        require(apiKey.isNotBlank()) { "کلید API وارد نشده" }

        val service = ChatClient.createService(endpoint)
        val response = service.listModels(auth = "Bearer $apiKey")

        if (!response.isSuccessful) {
            val error = response.errorBody()?.string() ?: "خطای ناشناخته"
            throw RuntimeException("خطا (${response.code()}): $error")
        }

        val models = response.body()?.data?.map { it.id }?.sorted()
            ?: throw RuntimeException("لیست مدل‌ها خالی است")

        models
    }

    suspend fun fetchModels(): List<String> {
        return try {
            val endpoint = settingsStore.apiEndpoint
            val apiKey = settingsStore.apiKey
            if (endpoint.isBlank() || apiKey.isBlank()) return emptyList()

            val service = ChatClient.createService(endpoint)
            val response = service.listModels(auth = "Bearer $apiKey")

            if (response.isSuccessful) {
                response.body()?.data?.map { it.id }?.sorted() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun buildRandomPrompt(seed: Int): String {
        val lang = settingsStore.storyLanguage
        val style = settingsStore.storyStyle
        val gender = settingsStore.childGender
        val age = settingsStore.ageRange

        val genderText = when (gender) {
            "boy" -> "پسر" to "he/him"
            "girl" -> "دختر" to "she/her"
            else -> "کودک" to "they"
        }

        val ageDesc = when (age) {
            "0-2" -> "نوزاد و نوپا (۰ تا ۲ سال) - خیلی ساده، کلمات کوتاه، تکراری"
            "3-5" -> "کودک خردسال (۳ تا ۵ سال) - ساده و روان، شخصیت‌های دوست‌داشتنی"
            "6-8" -> "کودک (۶ تا ۸ سال) - ماجراجویانه، کمی پیچیده‌تر"
            "9-12" -> "نوجوان (۹ تا ۱۲ سال) - جذاب‌تر، با پیام اخلاقی"
            else -> "کودک (۳ تا ۵ سال)"
        }

        val storyTopics = listOf(
            "یک ${genderText.first} کوچولو که با حیوانات جنگل دوست می‌شه",
            "سفر ${genderText.first} کوچولو به سیاره‌ای رنگارنگ",
            "${genderText.first} شجاعی که از روستاش محافظت می‌کنه",
            "ماجراجویی ${genderText.first} کوچولو در زیر دریا",
            "${genderText.first} کوچولویی که یک بچه اژدها پیدا می‌کنه",
            "شبی که ${genderText.first} کوچولو با ستاره‌ها حرف زد",
            "${genderText.first} کوچولو و دوست رباتیکش",
            "قایم‌موشک بازی ${genderText.first} کوچولو در قصر جادویی",
            "${genderText.first} کوچولو که نقاشی‌هاش زنده می‌شدن",
            "سفر ${genderText.first} کوچولو به جنگل افسانه‌ای",
            "${genderText.first} کوچولو و گربه‌ای که پرواز می‌کرد",
            "هدیه ویژه‌ای که ${genderText.first} کوچولو برای مامانش درست کرد",
            "${genderText.first} کوچولو و چراغ جادو",
            "باغ مخفی ${genderText.first} کوچولو",
            "${genderText.first} کوچولو که زبان حیوانات رو یاد گرفت"
        )

        val topic = storyTopics[seed % storyTopics.size]

        return """یک نویسنده حرفهای داستان کودکان هستی. فقط و فقط به $lang بنویس. هیچ کلمه انگلیسی استفاده نکن.

وظیفه: یک داستان کوتاه قبل از خواب بنویس.

فرمت خروجی:
خط اول: عنوان کوتاه داستان (حداکثر ۵ کلمه)
خط دوم: خالی
از خط سوم: متن داستان

قوانین:
- موضوع: $topic
- سبک: $style
- سن: $ageDesc
- جنسیت شخصیت اصلی: ${genderText.first}
- ۱۵۰ تا ۳۰۰ کلمه
- زبان ساده و روان
- شخصیتهای جذاب و دوستداشتنی
- پایان خوش و آرامشبخش
- مناسب قبل از خواب
- هیچ کلمه یا عبارت انگلیسی در متن نباشد
- تمام متن فارسی باشد
- اعداد را فارسی بنویس (نه لاتین)
- seed: $seed (برای یکتا بودن)
- اگر جنسیت مشخص شده، شخصیت اصلی همان جنسیت باشد"""
    }

    private fun buildCustomPrompt(userInput: String): String {
        val lang = settingsStore.storyLanguage
        val style = settingsStore.storyStyle
        val gender = settingsStore.childGender
        val age = settingsStore.ageRange

        val genderText = when (gender) {
            "boy" -> "پسر" to "he/him"
            "girl" -> "دختر" to "she/her"
            else -> "کودک" to "they"
        }

        val ageDesc = when (age) {
            "0-2" -> "نوزاد و نوپا (۰ تا ۲ سال) - خیلی ساده، کلمات کوتاه، تکراری"
            "3-5" -> "کودک خردسال (۳ تا ۵ سال) - ساده و روان، شخصیت‌های دوست‌داشتنی"
            "6-8" -> "کودک (۶ تا ۸ سال) - ماجراجویانه، کمی پیچیده‌تر"
            "9-12" -> "نوجوان (۹ تا ۱۲ سال) - جذاب‌تر، با پیام اخلاقی"
            else -> "کودک (۳ تا ۵ سال)"
        }

        return """یک نویسنده حرفهای داستان کودکان هستی. فقط و فقط به $lang بنویس. هیچ کلمه انگلیسی استفاده نکن.

وظیفه: یک داستان کوتاه قبل از خواب بنویس بر اساس درخواست کاربر.

فرمت خروجی:
خط اول: عنوان کوتاه داستان (حداکثر ۵ کلمه)
خط دوم: خالی
از خط سوم: متن داستان

قوانین:
- سبک: $style
- سن: $ageDesc
- جنسیت شخصیت اصلی: ${genderText.first}
- ۱۵۰ تا ۳۰۰ کلمه
- زبان ساده و روان
- شخصیتهای جذاب و دوستداشتنی
- پایان خوش و آرامشبخش
- مناسب قبل از خواب
- هیچ کلمه یا عبارت انگلیسی در متن نباشد
- تمام متن فارسی باشد
- اعداد را فارسی بنویس (نه لاتین)
- اگر جنسیت مشخص شده، شخصیت اصلی همان جنسیت باشد
- درخواست کاربر را دقیقاً اجرا کن

درخواست کاربر: $userInput"""
    }

    private fun cleanStoryText(raw: String): String {
        return raw.trim()
            .removePrefix("```")
            .removeSuffix("```")
            .removePrefix("```text")
            .trim()
    }

    private fun extractTitle(content: String, prompt: String): String {
        val lines = content.lines()
        val firstLine = lines.first().trim()

        // New format: first line is the title (short, no period)
        if (firstLine.length in 2..50 && !firstLine.endsWith(".") && !firstLine.endsWith("،")) {
            return firstLine
        }

        // Fallback: use first 5 words as title
        val words = firstLine.split(" ").take(5).joinToString(" ")
        return if (words.length > 3) words else "داستان"
    }

    private fun StoryEntity.toDomain() = Story(
        id = id,
        title = title,
        prompt = prompt,
        content = content,
        createdAt = createdAt
    )
}
