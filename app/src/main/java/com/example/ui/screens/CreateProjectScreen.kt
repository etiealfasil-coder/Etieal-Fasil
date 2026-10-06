package com.example.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.CropLandscape
import androidx.compose.material.icons.filled.CropPortrait
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.ai.GeminiEducationalService
import com.example.data.model.CuratedEducationalPresets
import com.example.data.model.EducationalProject
import com.example.data.model.ReferencePhotoPreset
import com.example.data.repository.EducationalRepository
import android.content.Context
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioGold
import com.example.ui.theme.StudioGoldLight
import com.example.ui.theme.StudioGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@Composable
fun CreateProjectScreen(
    repository: EducationalRepository,
    geminiService: GeminiEducationalService,
    onProjectCreated: (Long) -> Unit,
    isAmharicUi: Boolean = false
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPreset by remember { mutableStateOf<ReferencePhotoPreset?>(CuratedEducationalPresets.presets.first()) }
    var customUserPrompt by remember { mutableStateOf("") }

    val categories = listOf(
        "History & Heritage" to "ታሪክና ቅርስ",
        "Astronomy & Physics" to "ስነ-ፈለክና ፊዚክስ",
        "Biology & Genetics" to "ስነ-ህይወትና ጄኔቲክስ",
        "Botany & Agriculture" to "ስነ-እጽዋትና ግብርና",
        "Science & Tech" to "ሳይንስና ቴክኖሎጂ"
    )
    var selectedCategoryIndex by remember { mutableStateOf(0) }

    val audiences = listOf(
        "Primary" to "የመጀመሪያ ደረጃ",
        "High School" to "ሁለተኛ ደረጃ",
        "University" to "ከፍተኛ ትምህርት",
        "General Public" to "አጠቃላይ ህዝብ"
    )
    var selectedAudienceIndex by remember { mutableStateOf(1) }

    var selectedAspectRatio by remember { mutableStateOf("16:9") }

    var isGenerating by remember { mutableStateOf(false) }
    var generationStatusStep by remember { mutableStateOf(0) }

    // Media Picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            selectedPreset = null
        }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            // Save bitmap to temporary cache file
            try {
                val file = File(context.cacheResolverDir(), "camera_ref_${System.currentTimeMillis()}.jpg")
                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
                fos.flush()
                fos.close()
                selectedImageUri = Uri.fromFile(file)
                selectedPreset = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Scanning animation for AI generation
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart),
        label = "scanY"
    )

    val generationStepsEn = listOf(
        "Analyzing reference photo visual geometry & focal subjects...",
        "Identifying educational concepts & STEM pedagogical rules...",
        "Drafting bilingual Amharic Fidel & English narration...",
        "Generating 4K Ken Burns camera directions & Veo prompts...",
        "Finalizing interactive comprehension quiz & glossary..."
    )
    val generationStepsAm = listOf(
        "የማጣቀሻ ፎቶውን ዝርዝር እና መዋቅር በመተንተን ላይ...",
        "የትምህርት ፅንሰ-ሀሳቦችን እና የሳይንስ ህጎችን በማዘጋጀት ላይ...",
        "የአማርኛ ፊደል እና የእንግሊዝኛ የድምፅ ትረካ በማቀናጀት ላይ...",
        "የ4K የካሜራ እንቅስቃሴዎችን እና የቪዲዮ ትዕዛዞችን በማዋቀር ላይ...",
        "የቃላት ዝርዝር እና የፈተና ጥያቄዎችን በማጠናቀቅ ላይ..."
    )

    LaunchedEffect(isGenerating) {
        if (isGenerating) {
            for (step in generationStepsEn.indices) {
                generationStatusStep = step
                delay(800)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("create_project_screen")
    ) {
        // Hero Studio Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF1F2937), RoundedCornerShape(16.dp))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(StudioGold.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = StudioGold,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = if (isAmharicUi) "የ4K ትምህርታዊ ቪዲዮ ስቱዲዮ" else "4K AI Educational Video Studio",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isAmharicUi) "ፎቶዎችን በማጣቀሻነት በመጠቀም በአማርኛ እና እንግሊዝኛ ቪዲዮ ይፍጠሩ" else "Transform reference photos into 4K lessons in Amharic & English",
                        color = StudioCyan,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section 1: Reference Photo Selection
        Text(
            text = if (isAmharicUi) "1. የማጣቀሻ ፎቶ ይምረጡ (Reference Photo)" else "1. Choose Reference Photo",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (isAmharicUi) "ካሜራ፣ ጋለሪ ወይም ከታች ከተዘጋጁት ታሪካዊና ሳይንሳዊ ፎቶዎች ይምረጡ" else "Take a photo, pick from gallery, or select curated historical/STEM references",
            color = Color.Gray,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Action Buttons: Camera & Gallery
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("pick_gallery_button")
            ) {
                Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, tint = StudioCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isAmharicUi) "ጋለሪ" else "Gallery", color = Color.White, fontSize = 12.sp)
            }

            Button(
                onClick = { cameraLauncher.launch(null) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("open_camera_button")
            ) {
                Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, tint = StudioGold, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isAmharicUi) "ካሜራ" else "Camera", color = Color.White, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Curated Reference Presets Carousel
        Text(
            text = if (isAmharicUi) "የተመረጡ ማጣቀሻዎች (Curated Presets):" else "Curated Educational References:",
            color = StudioGoldLight,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(CuratedEducationalPresets.presets) { preset ->
                val isSelected = (selectedPreset?.id == preset.id && selectedImageUri == null)
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF1E293B) else Color(0xFF111827)
                    ),
                    modifier = Modifier
                        .width(150.dp)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) StudioGold else Color(0xFF1F2937),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            selectedPreset = preset
                            selectedImageUri = null
                            if (customUserPrompt.isBlank()) {
                                customUserPrompt = preset.descriptionEnglish
                            }
                        }
                ) {
                    Column {
                        Box(modifier = Modifier.height(85.dp).fillMaxWidth()) {
                            Image(
                                painter = painterResource(id = preset.drawableRes),
                                contentDescription = preset.titleEnglish,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(20.dp)
                                        .background(StudioGold, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = if (isAmharicUi) preset.titleAmharic else preset.titleEnglish,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = preset.category,
                                color = StudioCyan,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Reference Preview with Scan Radar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, StudioCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Selected Reference",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (selectedPreset != null) {
                Image(
                    painter = painterResource(id = selectedPreset!!.drawableRes),
                    contentDescription = selectedPreset!!.titleEnglish,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Radar Scan Overlay when Generating
            if (isGenerating) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                ) {
                    // Scanning Line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .align(Alignment.TopCenter)
                            .padding(top = (180 * scanLineY).dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color.Transparent, StudioCyan, StudioGold, Color.Transparent)
                                )
                            )
                    )
                }
            }

            // Reference Info Pill
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
                    .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (selectedImageUri != null) "User Photo Reference • 4K Source" else (selectedPreset?.titleEnglish ?: "Reference Selected"),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 2: Educational Parameters
        Text(
            text = if (isAmharicUi) "2. የትምህርት መስክና ደረጃ (Curriculum Settings)" else "2. Educational Parameters",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category Pills
        Text(
            text = if (isAmharicUi) "የትምህርት ዘርፍ (Topic Category):" else "Topic Category:",
            color = Color.LightGray,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEachIndexed { index, (en, am) ->
                FilterChip(
                    selected = (selectedCategoryIndex == index),
                    onClick = { selectedCategoryIndex = index },
                    label = { Text(if (isAmharicUi) am else en, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StudioGold,
                        selectedLabelColor = Color(0xFF1E1300),
                        containerColor = Color(0xFF1F2937),
                        labelColor = Color.LightGray
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Audience Level Pills
        Text(
            text = if (isAmharicUi) "የትምህርት ደረጃ (Target Audience):" else "Target Audience Level:",
            color = Color.LightGray,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            audiences.forEachIndexed { index, (en, am) ->
                FilterChip(
                    selected = (selectedAudienceIndex == index),
                    onClick = { selectedAudienceIndex = index },
                    label = { Text(if (isAmharicUi) am else en, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StudioCyan,
                        selectedLabelColor = Color(0xFF00363D),
                        containerColor = Color(0xFF1F2937),
                        labelColor = Color.LightGray
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Aspect Ratio: 16:9 vs 9:16
        Text(
            text = if (isAmharicUi) "የቪዲዮ ቅርጽ (Aspect Ratio):" else "Video Format (Aspect Ratio):",
            color = Color.LightGray,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilterChip(
                selected = (selectedAspectRatio == "16:9"),
                onClick = { selectedAspectRatio = "16:9" },
                leadingIcon = { Icon(Icons.Default.CropLandscape, contentDescription = null, modifier = Modifier.size(16.dp)) },
                label = { Text("16:9 Widescreen (Classroom / UHD TV)", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = StudioGold,
                    selectedLabelColor = Color(0xFF1E1300)
                ),
                modifier = Modifier.weight(1f)
            )

            FilterChip(
                selected = (selectedAspectRatio == "9:16"),
                onClick = { selectedAspectRatio = "9:16" },
                leadingIcon = { Icon(Icons.Default.CropPortrait, contentDescription = null, modifier = Modifier.size(16.dp)) },
                label = { Text("9:16 Vertical Reel", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = StudioGold,
                    selectedLabelColor = Color(0xFF1E1300)
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section 3: Custom Pedagogical Instructions / Prompt
        Text(
            text = if (isAmharicUi) "3. ተጨማሪ የትምህርት ትኩረት (Pedagogical Focus)" else "3. Custom Learning Goals & Focus",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = customUserPrompt,
            onValueChange = { customUserPrompt = it },
            placeholder = {
                Text(
                    if (isAmharicUi) "ምሳሌ፡ የላሊበላን ድንቅ የምህንድስና ስራ እና የውሃ ቦይ ስርዓት በአማርኛ እና እንግሊዝኛ አብራራ..." else "e.g. Explain how monolithic volcanic carving and hydraulic engineering protected the stone...",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StudioGold,
                unfocusedBorderColor = Color(0xFF334155),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF0F172A),
                unfocusedContainerColor = Color(0xFF0F172A)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .testTag("prompt_input_field")
        )

        Spacer(modifier = Modifier.height(20.dp))

        // AI Generation In-Flight Status
        if (isGenerating) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, StudioCyan, RoundedCornerShape(12.dp))
                    .padding(bottom = 14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            color = StudioGold,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isAmharicUi) "የ4K ቪዲዮ በዝግጅት ላይ ነው..." else "Generating 4K Bilingual Video...",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isAmharicUi) generationStepsAm.getOrElse(generationStatusStep) { "" } else generationStepsEn.getOrElse(generationStatusStep) { "" },
                        color = StudioCyan,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Primary Generate Button
        Button(
            onClick = {
                if (isGenerating) return@Button
                isGenerating = true

                coroutineScope.launch {
                    val promptText = customUserPrompt.ifBlank {
                        selectedPreset?.descriptionEnglish ?: "Educational lesson analyzing reference photograph in Amharic and English"
                    }
                    val category = categories[selectedCategoryIndex].first
                    val audience = audiences[selectedAudienceIndex].first
                    val presetName = selectedPreset?.drawableResName

                    try {
                        val generatedProject = geminiService.generateEducationalVideoProject(
                            userPrompt = promptText,
                            topicCategory = category,
                            targetAudience = audience,
                            aspectRatio = selectedAspectRatio,
                            imageUri = selectedImageUri,
                            presetDrawableResName = presetName
                        )

                        val newId = repository.insertProject(generatedProject)
                        isGenerating = false
                        Toast.makeText(context, "4K Educational Video Generated!", Toast.LENGTH_SHORT).show()
                        onProjectCreated(newId)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        isGenerating = false
                        Toast.makeText(context, "Generation error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            },
            enabled = !isGenerating,
            colors = ButtonDefaults.buttonColors(containerColor = StudioGold),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("generate_video_button")
        ) {
            Icon(imageVector = Icons.Default.Videocam, contentDescription = null, tint = Color(0xFF1E1300))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isAmharicUi) "የ4K ትምህርታዊ ቪዲዮ ፍጠር (Generate 4K)" else "Generate 4K Educational Video",
                color = Color(0xFF1E1300),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

private fun Context.cacheResolverDir(): File {
    val dir = File(cacheDir, "ref_photos")
    if (!dir.exists()) dir.mkdirs()
    return dir
}
