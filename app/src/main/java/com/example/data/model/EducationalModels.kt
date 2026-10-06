package com.example.data.model

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.R
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "educational_projects")
data class EducationalProject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titleEnglish: String,
    val titleAmharic: String,
    val topicCategory: String, // "History & Heritage", "Astronomy & Physics", "Biology & Genetics", "Botany & Agriculture", "Science & Tech"
    val targetAudience: String, // "Primary", "High School", "University", "General Public"
    val photoUri: String? = null,
    val photoDrawableResName: String? = null,
    val summaryEnglish: String,
    val summaryAmharic: String,
    val aspectRatio: String = "16:9", // "16:9" or "9:16"
    val resolution: String = "4K UHD (3840x2160)",
    val frameRate: String = "60 FPS",
    val veoPrompt4k: String,
    val scenesJson: String, // Serialized List<VideoScene>
    val keyTermsJson: String, // Serialized List<BilingualGlossaryItem>
    val quizQuestionEnglish: String = "",
    val quizQuestionAmharic: String = "",
    val quizOptionsJson: String = "",
    val quizCorrectIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val isRendered: Boolean = true
) {
    fun parseScenes(): List<VideoScene> {
        val list = mutableListOf<VideoScene>()
        if (scenesJson.isBlank()) return list
        try {
            val array = JSONArray(scenesJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    VideoScene(
                        sceneIndex = obj.optInt("sceneIndex", i + 1),
                        titleEnglish = obj.optString("titleEnglish", "Scene ${i + 1}"),
                        titleAmharic = obj.optString("titleAmharic", "ክፍል ${i + 1}"),
                        narrationEnglish = obj.optString("narrationEnglish", ""),
                        narrationAmharic = obj.optString("narrationAmharic", ""),
                        visualDescription = obj.optString("visualDescription", ""),
                        cameraMotion = obj.optString("cameraMotion", "Ken Burns Slow Zoom"),
                        durationSeconds = obj.optInt("durationSeconds", 6),
                        lowerThirdBadgeEnglish = obj.optString("lowerThirdBadgeEnglish", ""),
                        lowerThirdBadgeAmharic = obj.optString("lowerThirdBadgeAmharic", ""),
                        focusX = obj.optDouble("focusX", 0.5).toFloat(),
                        focusY = obj.optDouble("focusY", 0.5).toFloat()
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun parseKeyTerms(): List<BilingualGlossaryItem> {
        val list = mutableListOf<BilingualGlossaryItem>()
        if (keyTermsJson.isBlank()) return list
        try {
            val array = JSONArray(keyTermsJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    BilingualGlossaryItem(
                        termEnglish = obj.optString("termEnglish", ""),
                        termAmharic = obj.optString("termAmharic", ""),
                        phonetic = obj.optString("phonetic", ""),
                        definitionEnglish = obj.optString("definitionEnglish", ""),
                        definitionAmharic = obj.optString("definitionAmharic", "")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun parseQuizOptions(): List<String> {
        val list = mutableListOf<String>()
        if (quizOptionsJson.isBlank()) return list
        try {
            val array = JSONArray(quizOptionsJson)
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
        } catch (_: Exception) {}
        return list
    }
}

data class VideoScene(
    val sceneIndex: Int,
    val titleEnglish: String,
    val titleAmharic: String,
    val narrationEnglish: String,
    val narrationAmharic: String,
    val visualDescription: String,
    val cameraMotion: String, // e.g. "Slow Zoom In", "Pan Left to Right", "Dramatic Reveal", "Macro Detail Zoom"
    val durationSeconds: Int = 6,
    val lowerThirdBadgeEnglish: String = "",
    val lowerThirdBadgeAmharic: String = "",
    val focusX: Float = 0.5f,
    val focusY: Float = 0.5f
)

data class BilingualGlossaryItem(
    val termEnglish: String,
    val termAmharic: String,
    val phonetic: String,
    val definitionEnglish: String,
    val definitionAmharic: String
)

data class ReferencePhotoPreset(
    val id: String,
    val titleEnglish: String,
    val titleAmharic: String,
    val category: String,
    @field:DrawableRes val drawableRes: Int,
    val drawableResName: String,
    val descriptionEnglish: String,
    val descriptionAmharic: String,
    val defaultTargetAudience: String
)

object CuratedEducationalPresets {
    val presets = listOf(
        ReferencePhotoPreset(
            id = "lalibela",
            titleEnglish = "Lalibela Rock-Hewn Architecture",
            titleAmharic = "የላሊበላ ውቅር አብያተ ክርስቲያናት",
            category = "History & Heritage",
            drawableRes = R.drawable.ref_lalibela_1791316662397,
            drawableResName = "ref_lalibela_1791316662397",
            descriptionEnglish = "12th-century monolithic architectural engineering carved out of solid volcanic basalt.",
            descriptionAmharic = "በ12ኛው ክፍለ ዘመን ከዳገታማ እሳተ ገሞራ አለት የተፈለፈሉ ታሪካዊና ድንቅ የምህንድስና ቅርሶች።",
            defaultTargetAudience = "High School & University"
        ),
        ReferencePhotoPreset(
            id = "solar_system",
            titleEnglish = "The Solar System & Planetary Orbits",
            titleAmharic = "የፀሐይ ሥርዓት እና የፕላኔቶች ምህዋር",
            category = "Astronomy & Physics",
            drawableRes = R.drawable.ref_solar_system_1791316676613,
            drawableResName = "ref_solar_system_1791316676613",
            descriptionEnglish = "Gravitational equilibrium, planetary motion, celestial physics and orbital mechanics.",
            descriptionAmharic = "የስበት ሚዛን፣ የፕላኔቶች እንቅስቃሴ፣ የስነ-ፈለክ ሳይንስ እና የጠፈር ምህዋር ህጎች።",
            defaultTargetAudience = "Primary & High School"
        ),
        ReferencePhotoPreset(
            id = "dna_cell",
            titleEnglish = "DNA Double Helix & Cell Biology",
            titleAmharic = "ዲ ኤን ኤ (DNA) ድርብ ሄሊክስ እና የሕዋስ ሳይንስ",
            category = "Biology & Genetics",
            drawableRes = R.drawable.ref_dna_cell_1791316688956,
            drawableResName = "ref_dna_cell_1791316688956",
            descriptionEnglish = "Genetic coding, nucleotide base pairs, cellular replication and molecular biology.",
            descriptionAmharic = "የጄኔቲክ ኮድ፣ የኑክሊዮታይድ ጥንዶች፣ የሕዋስ መከፋፈል እና ሞለኪውላዊ ስነ-ህይወት።",
            defaultTargetAudience = "High School & University"
        ),
        ReferencePhotoPreset(
            id = "coffee_botany",
            titleEnglish = "Coffea Arabica Botany & Highland Ecology",
            titleAmharic = "የቡና እጽዋት ስነ-ህይወት እና የደጋ ስነ-ምህዳር",
            category = "Botany & Agriculture",
            drawableRes = R.drawable.ref_coffee_plant_1791316704669,
            drawableResName = "ref_coffee_plant_1791316704669",
            descriptionEnglish = "Photosynthesis, high-altitude biodiversity, caffeine alkaloid defense and agricultural science.",
            descriptionAmharic = "የፎቶሲንተሲስ ሂደት፣ የከፍተኛ ቦታ ብዝሃ-ህይወት፣ እና ዘመናዊ የግብርና ሳይንስ።",
            defaultTargetAudience = "General Public & Students"
        )
    )
}
