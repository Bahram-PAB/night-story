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
import com.nightstory.app.ui.strings.LocalStrings
import com.nightstory.app.ui.strings.LocalizationManager

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

val geminiModels = listOf("gemini-2.0-flash", "gemini-2.0-flash-lite", "gemini-1.5-flash", "gemini-1.5-pro")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val s = LocalStrings.current
    var showApiKey by remember { mutableStateOf(false) }
    var showTTSApiKey by remember { mutableStateOf(false) }
    var showGoogleTTSKey by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) { kotlinx.coroutines.delay(2000); viewModel.clearSaved() }
    }

    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(24.dp))
            Text(s.settingsTitle, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
            Text(s.settingsSubtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(28.dp))

            // Story Generation
            SectionHeader(s.storyGeneration, Icons.Default.AutoAwesome)
            Spacer(Modifier.height(8.dp))
            PasswordField(uiState.apiKey, { viewModel.updateApiKey(it) }, s.geminiApiKey, s.geminiApiKeyPlaceholder, showApiKey, { showApiKey = !showApiKey }, s.geminiApiKeyHelper, s.toggleVisibility)
            Spacer(Modifier.height(16.dp))
            DropdownField(s.geminiModel, uiState.model, geminiModels, Icons.Default.SmartToy) { viewModel.updateModel(it) }

            Spacer(Modifier.height(28.dp))

            // Story Preferences
            SectionHeader(s.storyPreferences, Icons.Default.Palette)
            Spacer(Modifier.height(8.dp))

            // Language dropdown - shows localized language names
            val langPairs = LocalizationManager.getLocalizedLanguageList()
            val currentLangDisplay = langPairs.firstOrNull { it.first == uiState.language }?.second ?: uiState.language
            DropdownField(s.storyLanguage, currentLangDisplay, langPairs.map { it.second }, Icons.Default.Language) { selected ->
                val langId = langPairs.firstOrNull { it.second == selected }?.first ?: selected
                viewModel.updateLanguage(langId)
            }

            Spacer(Modifier.height(12.dp))

            // Style dropdown - shows localized style names
            val styleList = LocalizationManager.getLocalizedStyleList(s)
            val currentStyleDisplay = LocalizationManager.getStyleDisplay(uiState.style, s)
            DropdownField(s.storyStyle, currentStyleDisplay, styleList, Icons.Default.Style) { selected ->
                val styleId = LocalizationManager.getStyleId(selected, s)
                viewModel.updateStyle(styleId)
            }

            Spacer(Modifier.height(28.dp))
            HorizontalDivider()
            Spacer(Modifier.height(28.dp))

            // Voice Section
            SectionHeader(s.voiceSection, Icons.Default.RecordVoiceOver)
            Text(s.voiceSectionSubtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))

            val ttsProviders = listOf("device" to s.deviceVoice, "openai" to s.openaiTTS, "google" to s.googleCloudTTS)
            DropdownField(s.ttsProvider, ttsProviders.firstOrNull { it.first == uiState.ttsProvider }?.second ?: s.deviceVoice, ttsProviders.map { it.second }, Icons.Default.VoiceOverOff) { selected ->
                viewModel.updateTTSProvider(ttsProviders.firstOrNull { it.second == selected }?.first ?: "device")
            }

            AnimatedVisibility(visible = uiState.ttsProvider == "openai") {
                Column {
                    Spacer(Modifier.height(16.dp))
                    PasswordField(uiState.openaiTTSApiKey, { viewModel.updateOpenaiTTSApiKey(it) }, s.openaiApiKey, s.openaiApiKeyPlaceholder, showTTSApiKey, { showTTSApiKey = !showTTSApiKey }, s.openaiApiKeyHelper, s.toggleVisibility)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(uiState.openaiTTSBaseUrl, { viewModel.updateOpenaiTTSBaseUrl(it) }, label = { Text(s.apiBaseUrl) }, placeholder = { Text("https://api.openai.com/") }, modifier = Modifier.fillMaxWidth(), singleLine = true, leadingIcon = { Icon(Icons.Default.Link, null) })
                    Spacer(Modifier.height(12.dp))
                    DropdownField(s.ttsModel, uiState.openaiTTSModel, openaiTTSModels, Icons.Default.SmartToy) { viewModel.updateOpenaiTTSModel(it) }
                    Spacer(Modifier.height(12.dp))
                    DropdownField(s.voice, uiState.openaiTVoice, openaiTVoices, Icons.Default.RecordVoiceOver) { viewModel.updateOpenaiTVoice(it) }
                    Spacer(Modifier.height(12.dp))
                    Text("${s.speed}: ${"%.1f".format(uiState.openaiTTSSpeed)}x", style = MaterialTheme.typography.bodyMedium)
                    Slider(uiState.openaiTTSSpeed, { viewModel.updateOpenaiTTSSpeed(it) }, valueRange = 0.5f..2.0f, steps = 14)
                }
            }

            AnimatedVisibility(visible = uiState.ttsProvider == "google") {
                Column {
                    Spacer(Modifier.height(16.dp))
                    PasswordField(uiState.googleTTSApiKey, { viewModel.updateGoogleTTSApiKey(it) }, s.googleCloudApiKey, s.googleApiKeyPlaceholder, showGoogleTTSKey, { showGoogleTTSKey = !showGoogleTTSKey }, s.googleApiKeyHelper, s.toggleVisibility)
                    Spacer(Modifier.height(12.dp))
                    DropdownField(s.voice, uiState.googleTVoice, googleTVoices, Icons.Default.RecordVoiceOver) { viewModel.updateGoogleTVoice(it) }
                    Spacer(Modifier.height(12.dp))
                    DropdownField(s.language, uiState.googleTTSLanguage, langToCodeList(), Icons.Default.Language) { viewModel.updateGoogleTTSLanguage(it) }
                }
            }

            Spacer(Modifier.height(32.dp))
            Button(onClick = { viewModel.save() }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text(if (uiState.saved) "\u2713 ${s.savedButton}" else s.saveButton)
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row { Icon(icon, null, tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.width(8.dp)); Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary) }
}

@Composable
private fun PasswordField(value: String, onValueChange: (String) -> Unit, label: String, placeholder: String, showKey: Boolean, onToggleShow: () -> Unit, helperText: String? = null, toggleLabel: String = "Toggle visibility") {
    Column {
        OutlinedTextField(value, onValueChange, label = { Text(label) }, placeholder = { Text(placeholder) }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.Key, null) }, trailingIcon = { IconButton(onClick = onToggleShow) { Icon(if (showKey) Icons.Default.VisibilityOff else Icons.Default.Visibility, toggleLabel) } }, visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(), singleLine = true)
        helperText?.let { Spacer(Modifier.height(4.dp)); Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(label: String, selectedValue: String, options: List<String>, icon: androidx.compose.ui.graphics.vector.ImageVector, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(selectedValue, {}, readOnly = true, label = { Text(label) }, modifier = Modifier.fillMaxWidth().menuAnchor(), leadingIcon = { Icon(icon, null) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) })
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option -> DropdownMenuItem(text = { Text(option) }, onClick = { onSelect(option); expanded = false }) }
        }
    }
}

private fun langToCodeList() = listOf("en-US", "es-ES", "fr-FR", "de-DE", "pt-BR", "cmn-CN", "ja-JP", "ko-KR", "ar-XA", "fa-IR", "hi-IN", "tr-TR", "it-IT", "ru-RU", "nl-NL")
