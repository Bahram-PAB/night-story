package com.nightstory.app.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

val storyStyles = listOf(
    "Fairy Tale", "Adventure", "Funny", "Spooky (mild)",
    "Animal Story", "Space Adventure", "Princess & Knight",
    "Dinosaur Story", "Ocean Adventure", "Magic & Wizards"
)

val languages = listOf(
    "English", "Spanish", "French", "German", "Portuguese",
    "Chinese", "Japanese", "Korean", "Arabic", "Persian",
    "Hindi", "Turkish", "Italian", "Russian", "Dutch"
)

val geminiModels = listOf(
    "gemini-2.0-flash",
    "gemini-2.0-flash-lite",
    "gemini-1.5-flash",
    "gemini-1.5-pro"
)

val openaiTTSModels = listOf("tts-1", "tts-1-hd")
val openaiTVoices = listOf("alloy", "echo", "fable", "onyx", "nova", "shimmer")

val googleTVoices = listOf(
    "en-US-Wavenet-A", "en-US-Wavenet-B", "en-US-Wavenet-C", "en-US-Wavenet-D",
    "en-US-Wavenet-E", "en-US-Wavenet-F", "en-GB-Wavenet-A", "en-GB-Wavenet-B",
    "en-AU-Wavenet-A", "en-AU-Wavenet-B", "en-IN-Wavenet-A", "en-IN-Wavenet-B",
    "de-DE-Wavenet-A", "de-DE-Wavenet-B", "fr-FR-Wavenet-A", "fr-FR-Wavenet-B",
    "es-ES-Wavenet-A", "es-ES-Wavenet-B", "pt-BR-Wavenet-A", "ja-JP-Wavenet-A",
    "ko-KR-Wavenet-A", "cmn-CN-Wavenet-A", "ar-XA-Wavenet-A", "hi-IN-Wavenet-A",
    "fa-IR-Wavenet-A", "tr-TR-Wavenet-A", "it-IT-Wavenet-A", "ru-RU-Wavenet-A"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showApiKey by remember { mutableStateOf(false) }
    var showTTSApiKey by remember { mutableStateOf(false) }
    var showGoogleTTSKey by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearSaved()
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(24.dp))

            Text("Settings", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
            Text("Configure your story generator", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(28.dp))

            // ===== STORY GENERATION =====
            SectionHeader("Story Generation", Icons.Default.AutoAwesome)
            Spacer(Modifier.height(8.dp))

            // Gemini API Key
            PasswordField(
                value = uiState.apiKey,
                onValueChange = { viewModel.updateApiKey(it) },
                label = "Gemini API Key",
                placeholder = "Paste your Google AI Studio key",
                showKey = showApiKey,
                onToggleShow = { showApiKey = !showApiKey },
                helperText = "Get a free key at aistudio.google.com/apikey"
            )

            Spacer(Modifier.height(16.dp))
            DropdownField("Gemini Model", uiState.model, geminiModels, Icons.Default.SmartToy) { viewModel.updateModel(it) }

            Spacer(Modifier.height(28.dp))

            // Story Preferences
            SectionHeader("Story Preferences", Icons.Default.Palette)
            Spacer(Modifier.height(8.dp))
            DropdownField("Story Language", uiState.language, languages, Icons.Default.Language) { viewModel.updateLanguage(it) }
            Spacer(Modifier.height(12.dp))
            DropdownField("Story Style", uiState.style, storyStyles, Icons.Default.Style) { viewModel.updateStyle(it) }

            Spacer(Modifier.height(28.dp))
            HorizontalDivider()
            Spacer(Modifier.height(28.dp))

            // ===== TTS / VOICE =====
            SectionHeader("Voice (Text-to-Speech)", Icons.Default.RecordVoiceOver)
            Text(
                "Choose how stories are read aloud",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))

            // TTS Provider selector
            val ttsProviders = listOf(
                "device" to "Device Voice (Free, offline)",
                "openai" to "OpenAI TTS (API key)",
                "google" to "Google Cloud TTS (API key)"
            )
            DropdownField(
                "TTS Provider",
                ttsProviders.firstOrNull { it.first == uiState.ttsProvider }?.second ?: "Device Voice",
                ttsProviders.map { it.second },
                Icons.Default.VoiceOverOff
            ) { selected ->
                val providerId = ttsProviders.firstOrNull { it.second == selected }?.first ?: "device"
                viewModel.updateTTSProvider(providerId)
            }

            // OpenAI TTS settings
            AnimatedVisibility(visible = uiState.ttsProvider == "openai") {
                Column {
                    Spacer(Modifier.height(16.dp))
                    PasswordField(
                        value = uiState.openaiTTSApiKey,
                        onValueChange = { viewModel.updateOpenaiTTSApiKey(it) },
                        label = "OpenAI API Key",
                        placeholder = "sk-...",
                        showKey = showTTSApiKey,
                        onToggleShow = { showTTSApiKey = !showTTSApiKey },
                        helperText = "Works with any OpenAI-compatible provider"
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.openaiTTSBaseUrl,
                        onValueChange = { viewModel.updateOpenaiTTSBaseUrl(it) },
                        label = { Text("API Base URL") },
                        placeholder = { Text("https://api.openai.com/") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Link, null) }
                    )
                    Spacer(Modifier.height(12.dp))
                    DropdownField("TTS Model", uiState.openaiTTSModel, openaiTTSModels, Icons.Default.SmartToy) { viewModel.updateOpenaiTTSModel(it) }
                    Spacer(Modifier.height(12.dp))
                    DropdownField("Voice", uiState.openaiTVoice, openaiTVoices, Icons.Default.RecordVoiceOver) { viewModel.updateOpenaiTVoice(it) }
                    Spacer(Modifier.height(12.dp))
                    Text("Speed: ${"%.1f".format(uiState.openaiTTSSpeed)}x", style = MaterialTheme.typography.bodyMedium)
                    Slider(
                        value = uiState.openaiTTSSpeed,
                        onValueChange = { viewModel.updateOpenaiTTSSpeed(it) },
                        valueRange = 0.5f..2.0f,
                        steps = 14
                    )
                }
            }

            // Google Cloud TTS settings
            AnimatedVisibility(visible = uiState.ttsProvider == "google") {
                Column {
                    Spacer(Modifier.height(16.dp))
                    PasswordField(
                        value = uiState.googleTTSApiKey,
                        onValueChange = { viewModel.updateGoogleTTSApiKey(it) },
                        label = "Google Cloud API Key",
                        placeholder = "AIza...",
                        showKey = showGoogleTTSKey,
                        onToggleShow = { showGoogleTTSKey = !showGoogleTTSKey },
                        helperText = "Enable Cloud Text-to-Speech API in Google Cloud Console"
                    )
                    Spacer(Modifier.height(12.dp))
                    DropdownField("Voice", uiState.googleTVoice, googleTVoices, Icons.Default.RecordVoiceOver) { viewModel.updateGoogleTVoice(it) }
                    Spacer(Modifier.height(12.dp))
                    DropdownField("Language", uiState.googleTTSLanguage, languages.map { langToCode(it) }, Icons.Default.Language) { viewModel.updateGoogleTTSLanguage(it) }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Save button
            Button(
                onClick = { viewModel.save() },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text(if (uiState.saved) "\u2713 Saved!" else "Save Settings")
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ===== Reusable composables =====

@Composable
private fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    showKey: Boolean,
    onToggleShow: () -> Unit,
    helperText: String? = null
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Key, null) },
            trailingIcon = {
                IconButton(onClick = onToggleShow) {
                    Icon(
                        if (showKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle visibility"
                    )
                }
            },
            visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true
        )
        helperText?.let {
            Spacer(Modifier.height(4.dp))
            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    selectedValue: String,
    options: List<String>,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            leadingIcon = { Icon(icon, null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onSelect(option); expanded = false }
                )
            }
        }
    }
}

private fun langToCode(language: String): String = when (language) {
    "English" -> "en-US"
    "Spanish" -> "es-ES"
    "French" -> "fr-FR"
    "German" -> "de-DE"
    "Portuguese" -> "pt-BR"
    "Chinese" -> "cmn-CN"
    "Japanese" -> "ja-JP"
    "Korean" -> "ko-KR"
    "Arabic" -> "ar-XA"
    "Persian" -> "fa-IR"
    "Hindi" -> "hi-IN"
    "Turkish" -> "tr-TR"
    "Italian" -> "it-IT"
    "Russian" -> "ru-RU"
    "Dutch" -> "nl-NL"
    else -> "en-US"
}
