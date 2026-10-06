package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.EducationalProject
import com.example.data.repository.EducationalRepository
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioGold
import com.example.ui.theme.StudioGoldLight
import com.example.ui.theme.StudioGreen
import kotlinx.coroutines.launch

@Composable
fun ProjectLibraryScreen(
    repository: EducationalRepository,
    onProjectClick: (Long) -> Unit,
    onNewProjectClick: () -> Unit,
    isAmharicUi: Boolean = false
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val allProjects by repository.allProjects.collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filterOptions = if (isAmharicUi) {
        listOf("All" to "ሁሉም", "Favorites" to "ተወዳጆች", "History" to "ታሪክ", "Astronomy" to "ስነ-ፈለክ", "Biology" to "ስነ-ህይወት", "Botany" to "ስነ-እጽዋት")
    } else {
        listOf("All" to "All", "Favorites" to "Favorites", "History" to "History", "Astronomy" to "Astronomy", "Biology" to "Biology", "Botany" to "Botany")
    }

    val filteredProjects = allProjects.filter { proj ->
        val matchesSearch = searchQuery.isBlank() ||
                proj.titleEnglish.contains(searchQuery, ignoreCase = true) ||
                proj.titleAmharic.contains(searchQuery, ignoreCase = true) ||
                proj.topicCategory.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Favorites" -> proj.isFavorite
            "History" -> proj.topicCategory.contains("History", ignoreCase = true)
            "Astronomy" -> proj.topicCategory.contains("Astronomy", ignoreCase = true)
            "Biology" -> proj.topicCategory.contains("Biology", ignoreCase = true)
            "Botany" -> proj.topicCategory.contains("Botany", ignoreCase = true)
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(16.dp)
            .testTag("project_library_screen")
    ) {
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = if (isAmharicUi) "ትምህርቶችን ይፈልጉ (በአማርኛ ወይም English)..." else "Search educational lessons (Amharic or English)...",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = StudioGold)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = StudioGold,
                unfocusedBorderColor = Color(0xFF1E293B),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF111827),
                unfocusedContainerColor = Color(0xFF111827)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("library_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            filterOptions.forEach { (key, label) ->
                FilterChip(
                    selected = (selectedFilter == key),
                    onClick = { selectedFilter = key },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StudioGold,
                        selectedLabelColor = Color(0xFF1E1300),
                        containerColor = Color(0xFF1F2937),
                        labelColor = Color.LightGray
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredProjects.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(StudioCyan.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = null,
                            tint = StudioCyan,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (isAmharicUi) "ምንም የተቀመጠ ቪዲዮ አልተገኘም" else "No Educational Videos Found",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isAmharicUi) "አዲስ የማጣቀሻ ፎቶ በመጠቀም 4K ቪዲዮ ማዘጋጀት ይችላሉ!" else "Create a 4K lesson using a reference photo in the Studio tab!",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            // Project Cards List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProjects, key = { it.id }) { project ->
                    val scenes = project.parseScenes()
                    val totalDuration = scenes.sumOf { it.durationSeconds }

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF1F2937), RoundedCornerShape(14.dp))
                            .clickable { onProjectClick(project.id) }
                            .testTag("project_item_${project.id}")
                    ) {
                        Column {
                            // Thumbnail with Badges
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                            ) {
                                if (project.photoUri != null) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(project.photoUri)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = project.titleEnglish,
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
                                        contentDescription = project.titleEnglish,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                // 4K UHD Badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(8.dp)
                                        .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(6.dp))
                                        .border(1.dp, StudioGold, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "4K UHD 60FPS",
                                        color = StudioGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                // Quick Play Action
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(44.dp)
                                        .background(StudioGold.copy(alpha = 0.9f), CircleShape)
                                        .clickable { onProjectClick(project.id) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = Color.Black,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                // Runtime tag
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp)
                                        .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${scenes.size} Scenes • ${totalDuration}s",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            // Info Body
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isAmharicUi) project.titleAmharic else project.titleEnglish,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isAmharicUi) project.titleEnglish else project.titleAmharic,
                                    color = StudioGoldLight,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${project.topicCategory} • ${project.targetAudience}",
                                        color = StudioCyan,
                                        fontSize = 11.sp
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    repository.setFavorite(project.id, !project.isFavorite)
                                                }
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (project.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                                contentDescription = "Favorite",
                                                tint = if (project.isFavorite) StudioGold else Color.Gray,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    repository.deleteProject(project)
                                                }
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Delete",
                                                tint = Color.Gray,
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
    }
}
