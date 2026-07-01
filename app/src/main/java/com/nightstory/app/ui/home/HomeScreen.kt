package com.nightstory.app.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nightstory.app.ui.strings.LocalStrings

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val s = LocalStrings.current
    val uriHandler = LocalUriHandler.current

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

            // Random Story Button
            Button(
                onClick = { viewModel.generateRandom() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !uiState.isGenerating
            ) {
                if (uiState.isGenerating && !uiState.showCustomInput) {
                    CircularProgressIndicator(Modifier.size(22.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
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
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !uiState.isGenerating
            ) {
                Icon(Icons.Default.Edit, null)
                Spacer(Modifier.width(10.dp))
                Text(s.customStoryButton, style = MaterialTheme.typography.titleMedium)
            }

            // Custom input
            AnimatedVisibility(visible = uiState.showCustomInput) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        uiState.customPrompt, { viewModel.updateCustomPrompt(it) },
                        label = { Text(s.customPromptLabel) }, placeholder = { Text(s.customPromptPlaceholder) },
                        modifier = Modifier.fillMaxWidth(), minLines = 2, maxLines = 4,
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
                    Spacer(Modifier.height(16.dp))
                    val context = LocalContext.current
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "${story.title}\n\n${story.content}")
                            }
                            context.startActivity(Intent.createChooser(intent, s.share))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Share, null)
                        Spacer(Modifier.width(8.dp))
                        Text(s.share)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // New story button
                OutlinedButton(onClick = { viewModel.clearStory() }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Refresh, null)
                    Spacer(Modifier.width(8.dp))
                    Text("داستان جدید")
                }
            }

            Spacer(Modifier.height(48.dp))

            // ===== FOOTER =====
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(12.dp))

            Text(
                text = "${s.appName} v1.2.3",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "ساخته شده با ❤️",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "GitHub",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { uriHandler.openUri("https://github.com/Bahram-PAB") }
                )
                Text(
                    text = "LinkedIn",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { uriHandler.openUri("https://www.linkedin.com/in/bahram-pouralibaba-1a992239") }
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
