package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechSynthesizer
import com.example.data.model.EducationalProject
import com.example.data.repository.EducationalRepository
import com.example.ui.components.Export4KDialog
import com.example.ui.components.Studio4KPlayer
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioGold
import com.example.ui.theme.StudioGoldLight
import com.example.ui.theme.StudioGreen
import com.example.ui.theme.StudioRed
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: Long,
    repository: EducationalRepository,
    speechSynthesizer: SpeechSynthesizer,
    onBack: () -> Unit,
    isAmharicUi: Boolean = false
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val projectState by repository.getProjectById(projectId).collectAsState(initial = null)

    BackHandler {
        speechSynthesizer.stop()
        onBack()
    }

    if (projectState == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(StudioDarkBg),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading 4K Project...", color = Color.White)
        }
        return
    }

    val project = projectState!!
    val scenes = remember(project) { project.parseScenes() }
    val keyTerms = remember(project) { project.parseKeyTerms() }
    val quizOptions = remember(project) { project.parseQuizOptions() }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showExportDialog by remember { mutableStateOf(false) }

    // Quiz interactive state
    var selectedQuizAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var isQuizAnswerSubmitted by remember { mutableStateOf(false) }

    val tabTitles = if (isAmharicUi) {
        listOf("ክፍሎች", "ትረካና ጽሑፍ", "ቃላት", "ፈተና", "Veo 4K")
    } else {
        listOf("Scenes", "Script", "Glossary", "Quiz", "Veo 4K")
    }

    if (showExportDialog) {
        Export4KDialog(
            project = project,
            onDismiss = { showExportDialog = false }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isAmharicUi) project.titleAmharic else project.titleEnglish,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${project.topicCategory} • 4K UHD",
                            color = StudioGold,
                            fontSize = 10.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        speechSynthesizer.stop()
                        onBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Favorite Toggle
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                repository.setFavorite(project.id, !project.isFavorite)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (project.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Favorite",
                            tint = if (project.isFavorite) StudioGold else Color.Gray
                        )
                    }

                    // Export 4K Action
                    Button(
                        onClick = { showExportDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = StudioGold),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(34.dp)
                            .testTag("export_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = Color(0xFF1E1300),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "4K Export",
                            color = Color(0xFF1E1300),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0F172A)
                )
            )
        },
        containerColor = StudioDarkBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Interactive 4K Video Player Engine
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Studio4KPlayer(
                    project = project,
                    speechSynthesizer = speechSynthesizer
                )
            }

            // Tabs for Pedagogical Breakdown
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color(0xFF0F172A),
                contentColor = StudioGold,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = (selectedTabIndex == index),
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) StudioGold else Color.Gray
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTabIndex) {
                // Tab 0: Scenes Breakdown
                0 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(scenes) { index, scene ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFF1F2937), RoundedCornerShape(12.dp))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .background(StudioGold, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${scene.sceneIndex}",
                                                    color = Color.Black,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (isAmharicUi) scene.titleAmharic else scene.titleEnglish,
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        // Camera Motion Pill
                                        Box(
                                            modifier = Modifier
                                                .background(StudioCyan.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                                .border(1.dp, StudioCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = scene.cameraMotion,
                                                color = StudioCyan,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Spoken Amharic Narration
                                    Text(
                                        text = scene.narrationAmharic,
                                        color = StudioGoldLight,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )

                                    // Spoken English Narration
                                    Text(
                                        text = scene.narrationEnglish,
                                        color = Color.LightGray,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Visual Director Cue
                                    Text(
                                        text = "🎬 Visual 4K Cue: ${scene.visualDescription}",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                // Tab 1: Bilingual Script Comparative Reader
                1 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, StudioGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "አማርኛ እና እንግሊዝኛ የተሟላ ትረካ",
                                            color = StudioGold,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        IconButton(
                                            onClick = {
                                                val fullScript = buildString {
                                                    append(project.titleEnglish).append("\n").append(project.titleAmharic).append("\n\n")
                                                    scenes.forEach { scene ->
                                                        append("Scene ").append(scene.sceneIndex).append(":\n")
                                                        append("AM: ").append(scene.narrationAmharic).append("\n")
                                                        append("EN: ").append(scene.narrationEnglish).append("\n\n")
                                                    }
                                                }
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                clipboard.setPrimaryClip(ClipData.newPlainText("Script", fullScript))
                                                Toast.makeText(context, "Bilingual script copied!", Toast.LENGTH_SHORT).show()
                                            }
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Script", tint = Color.LightGray)
                                        }
                                    }

                                    scenes.forEachIndexed { i, scene ->
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "ክፍል ${i + 1}: ${scene.titleAmharic} (${scene.titleEnglish})",
                                            color = StudioCyan,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = scene.narrationAmharic,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = scene.narrationEnglish,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 12.sp,
                                            lineHeight = 17.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Tab 2: Educational Glossary
                2 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(keyTerms) { _, item ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFF1F2937), RoundedCornerShape(12.dp))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${item.termAmharic} • ${item.termEnglish}",
                                            color = StudioGold,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (item.phonetic.isNotBlank()) {
                                            Text(
                                                text = "[${item.phonetic}]",
                                                color = StudioCyan,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = item.definitionAmharic,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = item.definitionEnglish,
                                        color = Color.LightGray,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Tab 3: Interactive Comprehension Quiz
                3 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, StudioGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, tint = StudioGold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isAmharicUi) "የቪዲዮው ማጠቃለያ ፈተና" else "Video Comprehension Check",
                                        color = StudioGold,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                if (project.quizQuestionAmharic.isNotBlank()) {
                                    Text(
                                        text = project.quizQuestionAmharic,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                Text(
                                    text = project.quizQuestionEnglish,
                                    color = Color.LightGray,
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                quizOptions.forEachIndexed { index, option ->
                                    val isSelected = (selectedQuizAnswerIndex == index)
                                    val isCorrect = (index == project.quizCorrectIndex)

                                    val bgColor = when {
                                        !isQuizAnswerSubmitted && isSelected -> Color(0xFF1E293B)
                                        isQuizAnswerSubmitted && isCorrect -> Color(0xFF064E3B)
                                        isQuizAnswerSubmitted && isSelected && !isCorrect -> Color(0xFF450A0A)
                                        else -> Color(0xFF0F172A)
                                    }

                                    val borderColor = when {
                                        !isQuizAnswerSubmitted && isSelected -> StudioGold
                                        isQuizAnswerSubmitted && isCorrect -> StudioGreen
                                        isQuizAnswerSubmitted && isSelected && !isCorrect -> StudioRed
                                        else -> Color(0xFF1E293B)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(bgColor)
                                            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                                            .clickable(enabled = !isQuizAnswerSubmitted) {
                                                selectedQuizAnswerIndex = index
                                            }
                                            .padding(12.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${'A' + index}.",
                                                color = StudioGold,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = option,
                                                color = Color.White,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                if (!isQuizAnswerSubmitted) {
                                    Button(
                                        onClick = {
                                            if (selectedQuizAnswerIndex != null) {
                                                isQuizAnswerSubmitted = true
                                            }
                                        },
                                        enabled = (selectedQuizAnswerIndex != null),
                                        colors = ButtonDefaults.buttonColors(containerColor = StudioGold),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Submit Answer", color = Color(0xFF1E1300), fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    val wasCorrect = (selectedQuizAnswerIndex == project.quizCorrectIndex)
                                    Text(
                                        text = if (wasCorrect) "🎉 Correct! ትክክለኛ መልስ ነው!" else "Incorrect. The correct answer was option ${'A' + project.quizCorrectIndex}.",
                                        color = if (wasCorrect) StudioGreen else StudioRed,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Tab 4: Veo 3.1 4K Direct Prompt
                4 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, StudioCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Veo 3.1 4K Ultra HD Prompt",
                                        color = StudioCyan,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Veo Prompt", project.veoPrompt4k))
                                            Toast.makeText(context, "Veo 4K prompt copied!", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = StudioCyan)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = project.veoPrompt4k,
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
