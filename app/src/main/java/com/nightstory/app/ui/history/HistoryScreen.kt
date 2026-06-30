package com.nightstory.app.ui.history

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nightstory.app.data.db.StoryEntity
import com.nightstory.app.ui.strings.LocalStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = viewModel()) {
    val stories by viewModel.stories.collectAsState()
    var storyToDelete by remember { mutableStateOf<StoryEntity?>(null) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    val s = LocalStrings.current

    storyToDelete?.let { story ->
        AlertDialog(onDismissRequest = { storyToDelete = null }, title = { Text(s.deleteStory) },
            text = { Text(s.deleteStoryConfirm.format(story.title)) },
            confirmButton = { TextButton(onClick = { viewModel.deleteStory(story); storyToDelete = null }) { Text(s.delete) } },
            dismissButton = { TextButton(onClick = { storyToDelete = null }) { Text(s.cancel) } })
    }
    if (showDeleteAllDialog) {
        AlertDialog(onDismissRequest = { showDeleteAllDialog = false }, title = { Text(s.deleteAll) },
            text = { Text(s.deleteAllConfirm) },
            confirmButton = { TextButton(onClick = { viewModel.deleteAll(); showDeleteAllDialog = false }) { Text(s.delete, color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { showDeleteAllDialog = false }) { Text(s.cancel) } })
    }

    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(s.storyLibrary, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                    Text(s.storiesSaved.format(stories.size), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (stories.isNotEmpty()) IconButton(onClick = { showDeleteAllDialog = true }) { Icon(Icons.Default.DeleteSweep, s.deleteAll, tint = MaterialTheme.colorScheme.error) }
            }
            Spacer(Modifier.height(16.dp))
            if (stories.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("\uD83D\uDCD6", style = MaterialTheme.typography.displayLarge)
                        Spacer(Modifier.height(16.dp))
                        Text(s.noStoriesYet, style = MaterialTheme.typography.titleLarge)
                        Text(s.noStoriesHint, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 32.dp)) {
                    items(stories, key = { it.id }) { story ->
                        StoryCard(story, { storyToDelete = story })
                    }
                }
            }
        }
    }
}

@Composable
fun StoryCard(story: StoryEntity, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy \u2022 h:mm a", Locale.getDefault()) }
    val s = LocalStrings.current

    Card(modifier = Modifier.fillMaxWidth().animateContentSize(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(story.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(dateFormat.format(Date(story.createdAt)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, s.deleteStory, tint = MaterialTheme.colorScheme.error) }
            }
            if (expanded) {
                Spacer(Modifier.height(8.dp)); HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant); Spacer(Modifier.height(8.dp))
                Text(story.content, style = MaterialTheme.typography.bodyMedium)
            }
            TextButton(onClick = { expanded = !expanded }, modifier = Modifier.align(Alignment.End)) {
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp)); Text(if (expanded) s.showLess else s.readMore)
            }
        }
    }
}
