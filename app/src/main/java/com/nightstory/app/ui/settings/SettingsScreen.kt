package com.nightstory.app.ui.settings

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val s = LocalStrings.current
    var showApiKey by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) { kotlinx.coroutines.delay(2000); viewModel.clearSaved() }
    }

    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(24.dp))
            Text(s.settingsTitle, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
            Text(s.settingsSubtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(28.dp))

            // ===== API SETTINGS =====
            SectionHeader(s.apiSettings, Icons.Default.Cloud)
            Spacer(Modifier.height(8.dp))

            // Endpoint
            OutlinedTextField(
                uiState.apiEndpoint, { viewModel.updateApiEndpoint(it) },
                label = { Text(s.apiEndpoint) }, placeholder = { Text(s.apiEndpointPlaceholder) },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                leadingIcon = { Icon(Icons.Default.Link, null) }
            )
            Spacer(Modifier.height(4.dp))
            Text(s.apiEndpointHelper, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(12.dp))

            // API Key
            OutlinedTextField(
                uiState.apiKey, { viewModel.updateApiKey(it) },
                label = { Text(s.apiKeyLabel) }, placeholder = { Text(s.apiKeyPlaceholder) },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                leadingIcon = { Icon(Icons.Default.Key, null) },
                trailingIcon = { IconButton(onClick = { showApiKey = !showApiKey }) { Icon(if (showApiKey) Icons.Default.VisibilityOff else Icons.Default.Visibility, s.toggleVisibility) } },
                visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation()
            )
            Spacer(Modifier.height(4.dp))
            Text(s.apiKeyHelper, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(12.dp))

            // Model Name
            OutlinedTextField(
                uiState.modelName, { viewModel.updateModelName(it) },
                label = { Text(s.modelNameLabel) }, placeholder = { Text(s.modelNamePlaceholder) },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                leadingIcon = { Icon(Icons.Default.SmartToy, null) }
            )
            Spacer(Modifier.height(4.dp))
            Text(s.modelNameHelper, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(28.dp))
            HorizontalDivider()
            Spacer(Modifier.height(28.dp))

            // ===== STORY PREFERENCES =====
            SectionHeader(s.storyPreferences, Icons.Default.Palette)
            Spacer(Modifier.height(8.dp))

            // Language
            val langPairs = LocalizationManager.getLocalizedLanguageList()
            val currentLangDisplay = langPairs.firstOrNull { it.first == uiState.language }?.second ?: uiState.language
            DropdownField(s.storyLanguage, currentLangDisplay, langPairs.map { it.second }, Icons.Default.Language) { selected ->
                viewModel.updateLanguage(langPairs.firstOrNull { it.second == selected }?.first ?: selected)
            }

            Spacer(Modifier.height(12.dp))

            // Style
            val styleList = LocalizationManager.getLocalizedStyleList(s)
            val currentStyleDisplay = LocalizationManager.getStyleDisplay(uiState.style, s)
            DropdownField(s.storyStyle, currentStyleDisplay, styleList, Icons.Default.Style) { selected ->
                viewModel.updateStyle(LocalizationManager.getStyleId(selected, s))
            }

            Spacer(Modifier.height(12.dp))

            // Gender
            val genderOptions = listOf("boy" to s.genderBoy, "girl" to s.genderGirl, "both" to s.genderBoth)
            val currentGenderDisplay = genderOptions.firstOrNull { it.first == uiState.gender }?.second ?: s.genderBoth
            DropdownField(s.childGender, currentGenderDisplay, genderOptions.map { it.second }, Icons.Default.ChildCare) { selected ->
                viewModel.updateGender(genderOptions.firstOrNull { it.second == selected }?.first ?: "both")
            }

            Spacer(Modifier.height(12.dp))

            // Age Range
            val ageOptions = listOf(
                "0-2" to s.ageBaby, "3-5" to s.ageToddler, "6-8" to s.ageChild, "9-12" to s.ageOlder
            )
            val currentAgeDisplay = ageOptions.firstOrNull { it.first == uiState.ageRange }?.second ?: s.ageToddler
            DropdownField(s.ageRange, currentAgeDisplay, ageOptions.map { it.second }, Icons.Default.Cake) { selected ->
                viewModel.updateAgeRange(ageOptions.firstOrNull { it.second == selected }?.first ?: "3-5")
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
