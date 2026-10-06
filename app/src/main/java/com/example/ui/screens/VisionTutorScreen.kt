package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.data.model.CuratedEducationalPresets
import com.example.data.model.ReferencePhotoPreset
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioGold
import com.example.ui.theme.StudioGoldLight
import com.example.ui.theme.StudioGreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class TutorMessage(
    val sender: String, // "user" or "tutor"
    val textEnglish: String,
    val textAmharic: String = ""
)

@Composable
fun VisionTutorScreen(
    isAmharicUi: Boolean = false
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedPreset by remember { mutableStateOf<ReferencePhotoPreset>(CuratedEducationalPresets.presets.first()) }
    var userQuestion by remember { mutableStateOf("") }
    var isAnswering by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            TutorMessage(
                sender = "tutor",
                textEnglish = "Welcome to the 4K Vision Tutor! Ask any pedagogical or scientific question about our reference photos in Amharic or English.",
                textAmharic = "እንኳን ወደ 4K የትምህርት አጋዥ ስቱዲዮ በደህና መጡ! ስለመረጡት የማጣቀሻ ፎቶ ማንኛውንም ሳይንሳዊ ወይም ታሪካዊ ጥያቄ በአማርኛ ወይም በእንግሊዝኛ መጠየቅ ይችላሉ።"
            )
        )
    }

    val suggestedQuestions = if (isAmharicUi) {
        listOf(
            "በላሊበላ የውሃ ፍሳሽ ቦይ ለምን ተሰራ?",
            "ፕላኔቶች በኤሊፕስ ምህዋር የሚጓዙት ለምንድን ነው?",
            "የዲ ኤን ኤ (DNA) ድርብ ሄሊክስ እንዴት ይባዛል?",
            "የቡና እጽዋት ካፌይን ለምን ያመነጫል?"
        )
    } else {
        listOf(
            "How was hydraulic drainage carved at Lalibela?",
            "Why are planetary orbits elliptical?",
            "How does DNA replicate before cell division?",
            "Why do coffee plants produce caffeine?"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(14.dp)
            .testTag("vision_tutor_screen")
    ) {
        // Preset selector chips
        Text(
            text = if (isAmharicUi) "የማጣቀሻ ፎቶ ይምረጡ:" else "Select Reference Photo to Inspect:",
            color = StudioGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(CuratedEducationalPresets.presets) { preset ->
                val isSelected = (preset.id == selectedPreset.id)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) StudioGold else Color(0xFF1E293B))
                        .clickable { selectedPreset = preset }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isAmharicUi) preset.titleAmharic else preset.titleEnglish,
                        color = if (isSelected) Color(0xFF1E1300) else Color.LightGray,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Selected Reference Photo Banner
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = selectedPreset.drawableRes),
                    contentDescription = selectedPreset.titleEnglish,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = if (isAmharicUi) selectedPreset.titleAmharic else selectedPreset.titleEnglish,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isAmharicUi) selectedPreset.descriptionAmharic else selectedPreset.descriptionEnglish,
                        color = StudioGoldLight,
                        fontSize = 11.sp,
                        maxLines = 2
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick suggested questions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            suggestedQuestions.take(2).forEach { q ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B))
                        .clickable {
                            userQuestion = q
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = StudioGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(q, color = Color.LightGray, fontSize = 10.sp, maxLines = 1)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Messages Conversation List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val isUser = (msg.sender == "user")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isUser) StudioGold.copy(alpha = 0.2f) else Color(0xFF111827))
                            .border(
                                1.dp,
                                if (isUser) StudioGold else Color(0xFF1F2937),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isUser) Icons.Default.School else Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = if (isUser) StudioGold else StudioCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isUser) "Student (ተማሪ)" else "4K AI Tutor (የትምህርት አጋዥ)",
                                    color = if (isUser) StudioGold else StudioCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            if (msg.textAmharic.isNotBlank()) {
                                Text(
                                    text = msg.textAmharic,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                            }
                            Text(
                                text = msg.textEnglish,
                                color = if (msg.textAmharic.isNotBlank()) Color(0xFF94A3B8) else Color.White,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chat Input Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = userQuestion,
                onValueChange = { userQuestion = it },
                placeholder = {
                    Text(
                        if (isAmharicUi) "ጥያቄዎን በአማርኛ ወይም English ይጻፉ..." else "Ask about this reference in Amharic or English...",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
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
                    .weight(1f)
                    .height(48.dp)
                    .testTag("tutor_question_input")
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    val q = userQuestion.trim()
                    if (q.isBlank() || isAnswering) return@IconButton
                    userQuestion = ""
                    messages.add(TutorMessage(sender = "user", textEnglish = q))
                    isAnswering = true

                    coroutineScope.launch {
                        val answer = askTutor(q, selectedPreset)
                        isAnswering = false
                        messages.add(answer)
                    }
                },
                enabled = userQuestion.isNotBlank() && !isAnswering,
                modifier = Modifier
                    .size(44.dp)
                    .background(StudioGold, CircleShape)
            ) {
                if (isAnswering) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color(0xFF1E1300),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private suspend fun askTutor(question: String, preset: ReferencePhotoPreset): TutorMessage = withContext(Dispatchers.IO) {
    val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (_: Exception) { "" }

    if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
        try {
            val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build()
            val prompt = """
                You are a bilingual Ethiopian & international STEM educator.
                The student asks about this reference: "${preset.titleEnglish}" (${preset.descriptionEnglish}).
                Question: "$question"
                Provide a concise educational answer in both Amharic and English.
                Respond with valid JSON:
                {
                   "answerAmharic": "Short clear pedagogical answer in Amharic fidel",
                   "answerEnglish": "Short clear pedagogical answer in English"
                }
            """.trimIndent()

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().put(JSONObject().put("parts", JSONArray().put(JSONObject().put("text", prompt)))))
                put("generationConfig", JSONObject().put("responseMimeType", "application/json"))
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string()
                if (body != null) {
                    val root = JSONObject(body)
                    val text = root.getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text")
                    val parsed = JSONObject(text)
                    return@withContext TutorMessage(
                        sender = "tutor",
                        textEnglish = parsed.optString("answerEnglish", "Concept explained."),
                        textAmharic = parsed.optString("answerAmharic", "")
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Contextual fallback response
    val qLower = question.lowercase()
    val (am, en) = when {
        qLower.contains("lalibela") || qLower.contains("drainage") || qLower.contains("ውሃ") ->
            Pair(
                "በላሊበላ የውሃ ፍሳሽ ቦይ የተሰራው ዝናብ ሲጥል ለስላሳው የእሳተ ገሞራ አለት በውሃ መሸርሸር እንዳይፈርስ እና የከርሰ ምድር ውሃ ወደ ህንፃው እንዳይገባ ለመከላከል ነው።",
                "The drainage system at Lalibela was engineered to divert heavy rainwater and underground seepage away from the porous volcanic rock, preventing centuries of catastrophic structural erosion."
            )
        qLower.contains("orbit") || qLower.contains("kepler") || qLower.contains("ፕላኔት") ->
            Pair(
                "ፕላኔቶች በኤሊፕሳዊ መንገድ የሚጓዙት በፀሐይ የስበት ኃይል እና በፕላኔቷ ወደፊት የመጓዝ ፍጥነት ሚዛን ምክንያት ነው። ወደ ፀሐይ ሲቀርቡም ፍጥነታቸው ይጨምራል።",
                "Planets follow elliptical orbits because of the gravitational balance between the Sun's mass and the planet's forward momentum. As described by Kepler, velocity increases closer to the perihelion."
            )
        qLower.contains("dna") || qLower.contains("helix") || qLower.contains("ዲ ኤን ኤ") ->
            Pair(
                "ዲ ኤን ኤ (DNA) ከመከፋፈሉ በፊት ኢንዛይሞች ክሮቹን ይከፍቱታል፤ ከዚያም አደኒን ከታይሚን፣ ሳይቶሲን ከጓኒን ጋር በመገናኘት አዲስ አምሳያ ይፈጥራሉ።",
                "During DNA replication, the double helix unzips and specialized enzymes match complementary base pairs (Adenine with Thymine, Cytosine with Guanine) to forge an exact twin helix."
            )
        else ->
            Pair(
                "ይህ የማጣቀሻ ፎቶ በተፈጥሮ እና በሰው ልጅ ጥበብ ውስጥ ያሉ መሰረታዊ የሳይንስ እና የታሪክ ህጎችን ያሳየናል።",
                "This reference photo embodies fundamental scientific structures and historical innovation, designed to deepen inquiry across both Amharic and global educational contexts."
            )
    }

    return@withContext TutorMessage(sender = "tutor", textEnglish = en, textAmharic = am)
}
