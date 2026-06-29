package com.nightstory.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showApiKey by remember { mutableStateOf(false) }

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

            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Configure your story generator",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(28.dp))

            // === API Key Section ===
            Text(
                text = "API Configuration",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.apiKey,
                onValueChange = { viewModel.updateApiKey(it) },
                label = { Text("Gemini API Key") },
                placeholder = { Text("Paste your Google AI Studio key") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Key, null) },
                trailingIcon = {
                    IconButton(onClick = { showApiKey = !showApiKey }) {
                        Icon(
                            if (showApiKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle visibility"
                        )
                    }
                },
                visualTransformation = if (showApiKey) VisualTransformation.None
                    else PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(Modifier.height(6.dp))
            Text(
                text = "Get a free key at aistudio.google.com/apikey",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            // Model selector
            var modelExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = modelExpanded,
                onExpandedChange = { modelExpanded = it }
            ) {
                OutlinedTextField(
                    value = uiState.model,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gemini Model") },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    leadingIcon = { Icon(Icons.Default.SmartToy, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(modelExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = modelExpanded,
                    onDismissRequest = { modelExpanded = false }
                ) {
                    geminiModels.forEach { model ->
                        DropdownMenuItem(
                            text = { Text(model) },
                            onClick = {
                                viewModel.updateModel(model)
                                modelExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // === Story Preferences ===
            Text(
                text = "Story Preferences",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))

            // Language selector
            var langExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = langExpanded,
                onExpandedChange = { langExpanded = it }
            ) {
                OutlinedTextField(
                    value = uiState.language,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Story Language") },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    leadingIcon = { Icon(Icons.Default.Language, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(langExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = langExpanded,
                    onDismissRequest = { langExpanded = false }
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang) },
                            onClick = {
                                viewModel.updateLanguage(lang)
                                langExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Style selector
            var styleExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = styleExpanded,
                onExpandedChange = { styleExpanded = it }
            ) {
                OutlinedTextField(
                    value = uiState.style,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Story Style") },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    leadingIcon = { Icon(Icons.Default.Palette, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(styleExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = styleExpanded,
                    onDismissRequest = { styleExpanded = false }
                ) {
                    storyStyles.forEach { style ->
                        DropdownMenuItem(
                            text = { Text(style) },
                            onClick = {
                                viewModel.updateStyle(style)
                                styleExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Save button
            Button(
                onClick = { viewModel.save() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    if (uiState.saved) "\u2713 Saved!" else "Save Settings"
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
