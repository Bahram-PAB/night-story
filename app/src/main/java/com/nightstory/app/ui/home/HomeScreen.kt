package com.nightstory.app.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nightstory.app.ui.strings.LocalStrings

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val s = LocalStrings.current

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // Header
            Text(
                text = "\u2728 ${s.appName} \u2728",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = s.appTagline,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            // ===== TWO MAIN BUTTONS =====

            // Random Story Button
            Button(
                onClick = { viewModel.generateRandom() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isGenerating
            ) {
                if (uiState.isGenerating && !uiState.showCustomInput) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(s.generatingText)
                } else {
                    Icon(Icons.Default.Casino, null)
                    Spacer(Modifier.width(10.dp))
                    Text(s.randomStoryButton, style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Custom Story Button
            OutlinedButton(
                onClick = { viewModel.toggleCustomInput() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isGenerating
            ) {
                Icon(Icons.Default.Edit, null)
                Spacer(Modifier.width(10.dp))
                Text(s.customStoryButton, style = MaterialTheme.typography.titleMedium)
            }

            // Custom input section
            AnimatedVisibility(visible = uiState.showCustomInput) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = uiState.customPrompt,
                        onValueChange = { viewModel.updateCustomPrompt(it) },
                        label = { Text(s.customPromptLabel) },
                        placeholder = { Text(s.customPromptPlaceholder) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4,
                        enabled = !uiState.isGenerating
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.generateCustom() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState.customPrompt.isNotBlank() && !uiState.isGenerating
                    ) {
                        if (uiState.isGenerating) {
                            CircularProgressIndicator(Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text(s.generatingText)
                        } else {
                            Icon(Icons.Default.AutoAwesome, null)
                            Spacer(Modifier.width(8.dp))
                            Text(s.generateCustomButton)
                        }
                    }
                }
            }

            // Error
            uiState.error?.let { error ->
                Spacer(Modifier.height(12.dp))
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(error, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Story display
            uiState.currentStory?.let { story ->
                Spacer(Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(story.title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(4.dp))
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        Text(story.content, style = MaterialTheme.typography.bodyLarge, lineHeight = MaterialTheme.typography.bodyLarge.lineHeight)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Action buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilledTonalButton(
                        onClick = { if (uiState.isSpeaking) viewModel.stopSpeaking() else viewModel.speakStory() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(if (uiState.isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow, null)
                        Spacer(Modifier.width(6.dp))
                        Text(if (uiState.isSpeaking) s.stopButton else s.readAloud)
                    }
                    OutlinedButton(onClick = { viewModel.clearStory() }) {
                        Icon(Icons.Default.Refresh, null)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
