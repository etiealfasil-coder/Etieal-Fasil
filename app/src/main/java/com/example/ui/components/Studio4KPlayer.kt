package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.audio.SpeechSynthesizer
import com.example.data.model.EducationalProject
import com.example.data.model.VideoScene
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioGold
import com.example.ui.theme.StudioGoldLight
import com.example.ui.theme.StudioGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class SubtitleMode {
    DUAL, AMHARIC, ENGLISH, OFF
}

enum class VoiceoverLang {
    ENGLISH, AMHARIC
}

@Composable
fun Studio4KPlayer(
    project: EducationalProject,
    speechSynthesizer: SpeechSynthesizer,
    modifier: Modifier = Modifier,
    onSceneChanged: ((Int) -> Unit)? = null
) {
    val context = LocalContext.current
    val scenes = remember(project) { project.parseScenes().ifEmpty { listOf(VideoScene(1, "Lesson Overview", "የትምህርት ማጠቃለያ", project.summaryEnglish, project.summaryAmharic, "Overview", "Cinematic Pan", 8)) } }

    var currentSceneIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }
    var isVerticalReel by remember { mutableStateOf(project.aspectRatio == "9:16") }
    var subtitleMode by remember { mutableStateOf(SubtitleMode.DUAL) }
    var voiceoverLang by remember { mutableStateOf(VoiceoverLang.ENGLISH) }
    var showControls by remember { mutableStateOf(true) }

    val currentScene = scenes.getOrNull(currentSceneIndex) ?: scenes.first()

    // Ken Burns Animation Values
    val scaleAnim = remember { Animatable(1.0f) }
    val transXAnim = remember { Animatable(0f) }
    val transYAnim = remember { Animatable(0f) }

    // Waveform animation
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse), label = "w1"
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(420, easing = LinearEasing), RepeatMode.Reverse), label = "w2"
    )
    val wave3 by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(280, easing = LinearEasing), RepeatMode.Reverse), label = "w3"
    )

    // Progress within scene
    var sceneElapsedSeconds by remember { mutableFloatStateOf(0f) }

    // Ken Burns trigger per scene
    LaunchedEffect(currentSceneIndex, isPlaying) {
        if (isPlaying) {
            val targetScale = when (currentScene.cameraMotion) {
                "Macro Detail Zoom" -> 1.35f
                "Ken Burns Macro Pan" -> 1.25f
                "Cinematic Top-Down Descent" -> 1.2f
                else -> 1.15f
            }
            val targetX = (currentScene.focusX - 0.5f) * -60f
            val targetY = (currentScene.focusY - 0.5f) * -60f

            scaleAnim.animateTo(targetScale, tween((currentScene.durationSeconds * 1000), easing = FastOutSlowInEasing))
        } else {
            scaleAnim.snapTo(1.05f)
        }
    }

    // Playback loop & TTS Trigger
    LaunchedEffect(currentSceneIndex, isPlaying, voiceoverLang) {
        if (isPlaying) {
            onSceneChanged?.invoke(currentSceneIndex)

            // Voiceover narration
            val narrationText = if (voiceoverLang == VoiceoverLang.AMHARIC) {
                currentScene.narrationAmharic.ifBlank { currentScene.narrationEnglish }
            } else {
                currentScene.narrationEnglish
            }

            speechSynthesizer.speak(
                text = narrationText,
                isAmharic = (voiceoverLang == VoiceoverLang.AMHARIC),
                utteranceId = "scene_$currentSceneIndex"
            )

            // Progress tracking
            val duration = currentScene.durationSeconds
            val stepInterval = 200L
            val steps = (duration * 1000) / stepInterval
            for (step in 0..steps) {
                if (!isPlaying) break
                sceneElapsedSeconds = (step * stepInterval) / 1000f
                delay(stepInterval)
            }

            // Auto advance
            if (isPlaying) {
                if (currentSceneIndex < scenes.size - 1) {
                    currentSceneIndex++
                } else {
                    isPlaying = false
                    currentSceneIndex = 0
                }
            }
        } else {
            speechSynthesizer.stop()
        }
    }

    val aspect = if (isVerticalReel) 9f / 16f else 16f / 9f

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black)
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
            .testTag("studio_4k_player_container")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspect)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { showControls = !showControls }
        ) {
            // Reference Image with Ken Burns transformation
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scaleAnim.value
                        scaleY = scaleAnim.value
                        translationX = transXAnim.value
                        translationY = transYAnim.value
                    }
            ) {
                if (project.photoUri != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(project.photoUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "4K Reference Video Visual",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    val drawableRes = when (project.photoDrawableResName) {
                        "ref_lalibela_1791316662397" -> R.drawable.ref_lalibela_1791316662397
                        "ref_solar_system_1791316676613" -> R.drawable.ref_solar_system_1791316676613
                        "ref_dna_cell_1791316688956" -> R.drawable.ref_dna_cell_1791316688956
                        "ref_coffee_plant_1791316704669" -> R.drawable.ref_coffee_plant_1791316704669
                        else -> R.drawable.hero_edu_studio_1791316577214
                    }
                    Image(
                        painter = painterResource(id = drawableRes),
                        contentDescription = "4K Educational Visual",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Vignette & Cinematic Shadow Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.55f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                )
            }

            // Top HUD: 4K UHD Badge & Telemetry
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .align(Alignment.TopStart),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 4K Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                        .border(1.dp, StudioGold.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(StudioGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "4K UHD • 60 FPS",
                        color = StudioGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Bitrate & Aspect Tag
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isPlaying) {
                        // Waveform Visualizer
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier
                                .height(16.dp)
                                .padding(end = 8.dp)
                        ) {
                            Box(modifier = Modifier.width(3.dp).height((16 * wave1).dp).background(StudioCyan, RoundedCornerShape(1.dp)))
                            Box(modifier = Modifier.width(3.dp).height((16 * wave2).dp).background(StudioGold, RoundedCornerShape(1.dp)))
                            Box(modifier = Modifier.width(3.dp).height((16 * wave3).dp).background(StudioCyan, RoundedCornerShape(1.dp)))
                            Box(modifier = Modifier.width(3.dp).height((16 * wave1).dp).background(StudioGreen, RoundedCornerShape(1.dp)))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isVerticalReel) "9:16 REEL" else "16:9 WIDE",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Lower Third Educational Badge (Floating above subtitles)
            if (currentScene.lowerThirdBadgeEnglish.isNotBlank()) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically { it / 2 },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 14.dp, bottom = if (subtitleMode != SubtitleMode.OFF) 95.dp else 50.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(Color(0xE60F172A), RoundedCornerShape(8.dp))
                            .border(1.dp, StudioCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(StudioCyan, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SCENE ${currentScene.sceneIndex}/${scenes.size} • ${currentScene.cameraMotion}",
                                color = StudioCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = currentScene.lowerThirdBadgeEnglish,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (currentScene.lowerThirdBadgeAmharic.isNotBlank()) {
                            Text(
                                text = currentScene.lowerThirdBadgeAmharic,
                                color = StudioGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Bilingual Subtitles Overlay
            if (subtitleMode != SubtitleMode.OFF) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = if (showControls) 45.dp else 16.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(Color(0xCC000000), RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        if (subtitleMode == SubtitleMode.DUAL || subtitleMode == SubtitleMode.AMHARIC) {
                            Text(
                                text = currentScene.narrationAmharic,
                                color = StudioGoldLight,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                lineHeight = 19.sp,
                                modifier = Modifier.testTag("subtitle_amharic")
                            )
                        }
                        if (subtitleMode == SubtitleMode.DUAL && currentScene.narrationEnglish.isNotBlank()) {
                            Spacer(modifier = Modifier.height(3.dp))
                        }
                        if (subtitleMode == SubtitleMode.DUAL || subtitleMode == SubtitleMode.ENGLISH) {
                            Text(
                                text = currentScene.narrationEnglish,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp,
                                modifier = Modifier.testTag("subtitle_english")
                            )
                        }
                    }
                }
            }

            // Transport & Playback Overlay Controls
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f))
                ) {
                    // Center Big Play / Pause & Scene Skip
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous Scene
                        IconButton(
                            onClick = {
                                if (currentSceneIndex > 0) currentSceneIndex--
                            },
                            enabled = currentSceneIndex > 0,
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FastRewind,
                                contentDescription = "Previous Scene",
                                tint = if (currentSceneIndex > 0) Color.White else Color.Gray
                            )
                        }

                        // Play/Pause Main Button
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(StudioGold, CircleShape)
                                .clickable { isPlaying = !isPlaying }
                                .testTag("play_pause_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color(0xFF1E1300),
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        // Next Scene
                        IconButton(
                            onClick = {
                                if (currentSceneIndex < scenes.size - 1) currentSceneIndex++
                            },
                            enabled = currentSceneIndex < scenes.size - 1,
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FastForward,
                                contentDescription = "Next Scene",
                                tint = if (currentSceneIndex < scenes.size - 1) Color.White else Color.Gray
                            )
                        }
                    }

                    // Bottom Bar: Scrubber + Controls
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                                )
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        // Multi-segment Scene Progress Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            scenes.forEachIndexed { index, scene ->
                                val progress = when {
                                    index < currentSceneIndex -> 1f
                                    index == currentSceneIndex -> (sceneElapsedSeconds / scene.durationSeconds.toFloat()).coerceIn(0f, 1f)
                                    else -> 0f
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color.White.copy(alpha = 0.3f))
                                        .clickable {
                                            currentSceneIndex = index
                                            sceneElapsedSeconds = 0f
                                        }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(progress)
                                            .fillMaxSize()
                                            .background(StudioGold)
                                    )
                                }
                            }
                        }

                        // Bottom Action Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Scene indicator & Timecode
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Scene ${currentSceneIndex + 1}/${scenes.size}",
                                    color = StudioGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${sceneElapsedSeconds.toInt()}s / ${currentScene.durationSeconds}s",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            // Subtitle Toggle & Audio Voice Selector & Aspect Ratio
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Voiceover Language
                                Box(
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .clickable {
                                            voiceoverLang = if (voiceoverLang == VoiceoverLang.ENGLISH) VoiceoverLang.AMHARIC else VoiceoverLang.ENGLISH
                                        }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                        .testTag("voice_toggle")
                                ) {
                                    Text(
                                        text = if (voiceoverLang == VoiceoverLang.ENGLISH) "🎙️ EN" else "🎙️ አማ",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Subtitle Mode
                                Box(
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .clickable {
                                            subtitleMode = when (subtitleMode) {
                                                SubtitleMode.DUAL -> SubtitleMode.AMHARIC
                                                SubtitleMode.AMHARIC -> SubtitleMode.ENGLISH
                                                SubtitleMode.ENGLISH -> SubtitleMode.OFF
                                                SubtitleMode.OFF -> SubtitleMode.DUAL
                                            }
                                        }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                        .testTag("subtitle_toggle")
                                ) {
                                    Text(
                                        text = "CC: ${subtitleMode.name.take(3)}",
                                        color = StudioCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Aspect ratio toggle
                                IconButton(
                                    onClick = { isVerticalReel = !isVerticalReel },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AspectRatio,
                                        contentDescription = "Toggle 16:9 / 9:16",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
