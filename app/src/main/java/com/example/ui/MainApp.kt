package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechSynthesizer
import com.example.data.ai.GeminiEducationalService
import com.example.data.repository.EducationalRepository
import com.example.ui.screens.CreateProjectScreen
import com.example.ui.screens.ProjectDetailScreen
import com.example.ui.screens.ProjectLibraryScreen
import com.example.ui.screens.VisionTutorScreen
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioGold

sealed class Screen {
    data object Studio : Screen()
    data object Library : Screen()
    data object Tutor : Screen()
    data class Detail(val projectId: Long) : Screen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    repository: EducationalRepository,
    geminiService: GeminiEducationalService,
    speechSynthesizer: SpeechSynthesizer
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Studio) }
    var isAmharicUi by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (currentScreen !is Screen.Detail) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = if (isAmharicUi) "ትምህርት 4K ስቱዲዮ" else "Temhert 4K Studio",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    actions = {
                        // Quick Language Toggle
                        IconButton(
                            onClick = { isAmharicUi = !isAmharicUi },
                            modifier = Modifier.testTag("app_language_toggle")
                        ) {
                            Text(
                                text = if (isAmharicUi) "EN" else "አማ",
                                color = StudioGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF0F172A)
                    )
                )
            }
        },
        bottomBar = {
            if (currentScreen !is Screen.Detail) {
                NavigationBar(
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color.White
                ) {
                    NavigationBarItem(
                        selected = (currentScreen is Screen.Studio),
                        onClick = { currentScreen = Screen.Studio },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Studio",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = if (isAmharicUi) "ስቱዲዮ" else "Studio",
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1E1300),
                            selectedTextColor = StudioGold,
                            indicatorColor = StudioGold,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_studio")
                    )

                    NavigationBarItem(
                        selected = (currentScreen is Screen.Library),
                        onClick = { currentScreen = Screen.Library },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = "Library",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = if (isAmharicUi) "ቤተ-መጽሐፍት" else "Library",
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1E1300),
                            selectedTextColor = StudioGold,
                            indicatorColor = StudioGold,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_library")
                    )

                    NavigationBarItem(
                        selected = (currentScreen is Screen.Tutor),
                        onClick = { currentScreen = Screen.Tutor },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "AI Tutor",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = if (isAmharicUi) "አጋዥ" else "AI Tutor",
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00363D),
                            selectedTextColor = StudioCyan,
                            indicatorColor = StudioCyan,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_tutor")
                    )
                }
            }
        },
        containerColor = StudioDarkBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    is Screen.Studio -> {
                        CreateProjectScreen(
                            repository = repository,
                            geminiService = geminiService,
                            onProjectCreated = { newId ->
                                currentScreen = Screen.Detail(newId)
                            },
                            isAmharicUi = isAmharicUi
                        )
                    }

                    is Screen.Library -> {
                        ProjectLibraryScreen(
                            repository = repository,
                            onProjectClick = { id ->
                                currentScreen = Screen.Detail(id)
                            },
                            onNewProjectClick = {
                                currentScreen = Screen.Studio
                            },
                            isAmharicUi = isAmharicUi
                        )
                    }

                    is Screen.Tutor -> {
                        VisionTutorScreen(
                            isAmharicUi = isAmharicUi
                        )
                    }

                    is Screen.Detail -> {
                        ProjectDetailScreen(
                            projectId = screen.projectId,
                            repository = repository,
                            speechSynthesizer = speechSynthesizer,
                            onBack = {
                                currentScreen = Screen.Library
                            },
                            isAmharicUi = isAmharicUi
                        )
                    }
                }
            }
        }
    }
}
