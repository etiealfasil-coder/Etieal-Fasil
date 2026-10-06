package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.EducationalProject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@Database(entities = [EducationalProject::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun educationalProjectDao(): EducationalProjectDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "temhert_4k_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialProjects(database.educationalProjectDao())
                }
            }
        }

        private suspend fun populateInitialProjects(dao: EducationalProjectDao) {
            // Project 1: Lalibela Rock Architecture
            val lalibelaScenes = JSONArray().apply {
                put(JSONObject().apply {
                    put("sceneIndex", 1)
                    put("titleEnglish", "Architectural Marvel in Stone")
                    put("titleAmharic", "የድንጋይ ላይ ጥበብ ድንቅ ምህንድስና")
                    put("narrationEnglish", "Rising from the red volcanic tuff of Roha, Saint George's Church was carved completely downwards from a single solid bedrock in the 12th century.")
                    put("narrationAmharic", "በ12ኛው ክፍለ ዘመን የተገነባው የቤተ ጊዮርጊስ ቤተ ክርስቲያን፣ ከአንድ ወጥ ቀይ የእሳተ ገሞራ አለት ወደ ታች ተፈልፍሎ የተሰራ አስደናቂ የምህንድስና ስራ ነው።")
                    put("visualDescription", "4K wide angle aerial slow descend tracking down the cruciform roof of Bete Giyorgis, morning golden hour light casting sharp shadows along chiselled volcanic rock.")
                    put("cameraMotion", "Cinematic Top-Down Descent")
                    put("durationSeconds", 7)
                    put("lowerThirdBadgeEnglish", "Monolithic Architecture • 12th Century")
                    put("lowerThirdBadgeAmharic", "የውቅር ህንፃ ጥበብ • 12ኛው ክ/ዘመን")
                    put("focusX", 0.5)
                    put("focusY", 0.4)
                })
                put(JSONObject().apply {
                    put("sceneIndex", 2)
                    put("titleEnglish", "Hydraulic & Structural Engineering")
                    put("titleAmharic", "የውሃ ቦይ እና መዋቅራዊ ምህንድስና")
                    put("narrationEnglish", "The builders designed sophisticated drainage trenches and subterranean passages to protect the porous stone from centuries of erosion.")
                    put("narrationAmharic", "የጥንት ግንበኞች አለቱ በውሃ እንዳይሸረሸር ጥልቅ የፍሳሽ ማስወገጃ ቦዮችንና የምድር ውስጥ መተላለፊያ መንገዶችን በከፍተኛ ብልሃት ነድፈዋል።")
                    put("visualDescription", "Macro 4K tracking shot gliding through narrow subterranean trenches, highlighting hand-chiseled drainage channels and geometric windows.")
                    put("cameraMotion", "Corridor Dolly In")
                    put("durationSeconds", 8)
                    put("lowerThirdBadgeEnglish", "Hydraulic Drainage System")
                    put("lowerThirdBadgeAmharic", "የፍሳሽ ማስወገጃ ቴክኖሎጂ")
                    put("focusX", 0.3)
                    put("focusY", 0.6)
                })
                put(JSONObject().apply {
                    put("sceneIndex", 3)
                    put("titleEnglish", "Mathematical Precision & Acoustics")
                    put("titleAmharic", "የሂሳብ ስሌት እና የድምፅ ውቅር")
                    put("narrationEnglish", "Every pillar, cross-vault, and arch follows harmonious golden ratios, creating rich acoustics for traditional Ethiopian liturgical chants.")
                    put("narrationAmharic", "እያንዳንዱ ምሰሶ እና ቅስት ትክክለኛ የሂሳብ ስሌትን የተከተለ ሲሆን፣ ለባህላዊ የዜማ ማስተጋባት ምቹ የሆነ ውስጣዊ ድምፅ ይፈጥራል።")
                    put("visualDescription", "Interior low angle pan illuminated by shafts of sunlight through Greek cross windows, dust motes dancing in the sacred volumetric light.")
                    put("cameraMotion", "Slow Interior Orbit")
                    put("durationSeconds", 7)
                    put("lowerThirdBadgeEnglish", "Acoustic Resonance & Geometry")
                    put("lowerThirdBadgeAmharic", "የድምፅ ቅላጼ እና ጂኦሜትሪ")
                    put("focusX", 0.6)
                    put("focusY", 0.5)
                })
                put(JSONObject().apply {
                    put("sceneIndex", 4)
                    put("titleEnglish", "Living Heritage & World Legacy")
                    put("titleAmharic", "ሕያው ቅርስ እና የዓለም ተምሳሌት")
                    put("narrationEnglish", "Today, Lalibela stands as a UNESCO World Heritage site and an eternal symbol of human innovation, faith, and architectural mastery.")
                    put("narrationAmharic", "ዛሬ ላሊበላ የሰው ልጅ የፈጠራ ችሎታ፣ ፅናት እና ጥልቅ ጥበብ መገለጫ ሆኖ በዩኔስኮ የተመዘገበ ህያው የዓለም ቅርስ ነው።")
                    put("visualDescription", "Sunset 4K panoramic pullback revealing the complete monolithic trench and church nestled against Lasta mountain ridge.")
                    put("cameraMotion", "Panoramic Sunset Pullback")
                    put("durationSeconds", 8)
                    put("lowerThirdBadgeEnglish", "UNESCO World Heritage Site")
                    put("lowerThirdBadgeAmharic", "የዩኔስኮ ዓለም አቀፍ ቅርስ")
                    put("focusX", 0.5)
                    put("focusY", 0.5)
                })
            }.toString()

            val lalibelaTerms = JSONArray().apply {
                put(JSONObject().apply {
                    put("termEnglish", "Monolithic")
                    put("termAmharic", "አንድ ወጥ አለት (ሞኖሊቲክ)")
                    put("phonetic", "Mō-nō-li-tik")
                    put("definitionEnglish", "Formed or carved from a single massive block of stone without separate joints.")
                    put("definitionAmharic", "ያለ ምንም ማያያዣ ጭቃ ከአንድ ወጥ ግዙፍ አለት የተፈለፈለ ወይም የተሰራ።")
                })
                put(JSONObject().apply {
                    put("termEnglish", "Basalt Tuff")
                    put("termAmharic", "የእሳተ ገሞራ አለት (ተፍ)")
                    put("phonetic", "Ba-zawlt Tuf")
                    put("definitionEnglish", "Volcanic rock formed from consolidated ash, softer when quarried but hardening over time.")
                    put("definitionAmharic", "ከእሳተ ገሞራ አመድና ፈሳሽ የተፈጠረ አለት፣ ሲፈልፍሉት ለስላሳ ሆኖ ከጊዜ በኋላ የሚጠነክር።")
                })
                put(JSONObject().apply {
                    put("termEnglish", "Hydraulic Engineering")
                    put("termAmharic", "የውሃ ምህንድስና")
                    put("phonetic", "Hī-drô-lik En-jə-nir-ing")
                    put("definitionEnglish", "Branch of engineering concerned with the flow and conveyance of water fluids.")
                    put("definitionAmharic", "የውሃ ፍሰትን እና የፍሳሽ ማስወገጃን በስርዓት ለመቆጣጠር የሚደረግ የምህንድስና ጥበብ።")
                })
            }.toString()

            val lalibelaQuizOptions = JSONArray().apply {
                put("Carved downwards from a single solid volcanic rock")
                put("Built with imported marble blocks from Rome")
                put("Assembled with modern iron reinforced concrete")
                put("Constructed with sun-dried mud bricks")
            }.toString()

            val project1 = EducationalProject(
                titleEnglish = "Lalibela Rock-Hewn Architecture: Ancient Engineering",
                titleAmharic = "የላሊበላ ውቅር አብያተ ክርስቲያናት፡ የጥንት ምህንድስና ድንቅ ስራ",
                topicCategory = "History & Heritage",
                targetAudience = "High School & University",
                photoDrawableResName = "ref_lalibela_1791316662397",
                summaryEnglish = "Explore how 12th-century Ethiopian architects excavated monolithic cruciform churches from solid volcanic rock using advanced hydraulic drainage and geometric precision.",
                summaryAmharic = "በ12ኛው ክፍለ ዘመን የነበሩ የኢትዮጵያ መሃንዲሶች ከአንድ ወጥ የእሳተ ገሞራ አለት ላይ ድንቅ ህንፃዎችን እንዴት እንዳነፁ የሚያሳይ የ4K ትምህርታዊ ቪዲዮ።",
                veoPrompt4k = "Cinematic 4K UHD, 60fps, photorealistic documentary style, Bete Giyorgis rock-hewn church in Lalibela, slow aerial camera drop down revealing carved cruciform roof, ancient chiselled textures, golden sunlight, 3840x2160 resolution, award winning National Geographic documentary cinematography.",
                scenesJson = lalibelaScenes,
                keyTermsJson = lalibelaTerms,
                quizQuestionEnglish = "What makes the construction of Bete Giyorgis in Lalibela architecturally unique?",
                quizQuestionAmharic = "የላሊበላ ቤተ ጊዮርጊስ ግንባታ በምህንድስናው ልዩ የሚያደርገው ምንድን ነው?",
                quizOptionsJson = lalibelaQuizOptions,
                quizCorrectIndex = 0,
                isFavorite = true,
                isRendered = true
            )

            // Project 2: Solar System
            val solarScenes = JSONArray().apply {
                put(JSONObject().apply {
                    put("sceneIndex", 1)
                    put("titleEnglish", "The Center of Gravitational Power")
                    put("titleAmharic", "የስበት ኃይል ማዕከል")
                    put("narrationEnglish", "At the heart of our celestial neighborhood burns the Sun, a nuclear furnace containing ninety-nine point eight percent of the solar system's total mass.")
                    put("narrationAmharic", "በፀሐይ ስርዓታችን መሃል የሚገኘው ግዙፉ ፀሐይ፣ የጠቅላላው ስርዓት 99.8 በመቶ የሚሆነውን ክብደት የያዘ የኒውክሌር ኃይል ማመንጫ ነው።")
                    put("visualDescription", "Ultra high definition 4K solar flare erupting into deep space, solar corona glowing with magnetic loops.")
                    put("cameraMotion", "Dynamic Solar Orbit")
                    put("durationSeconds", 7)
                    put("lowerThirdBadgeEnglish", "Nuclear Fusion • 5,500°C Surface")
                    put("lowerThirdBadgeAmharic", "የኒውክሌር ውህደት • 5,500° ሴልሺየስ")
                    put("focusX", 0.5)
                    put("focusY", 0.5)
                })
                put(JSONObject().apply {
                    put("sceneIndex", 2)
                    put("titleEnglish", "Kepler's Laws & Elliptical Orbits")
                    put("titleAmharic", "የኬፕለር ህጎች እና ኤሊፕሳዊ ምህዋር")
                    put("narrationEnglish", "Planets do not orbit in perfect circles, but in ellipses, accelerating as they swing closer to perihelion according to Kepler's laws.")
                    put("narrationAmharic", "ፕላኔቶች በክብ ሳይሆን በኤሊፕስ (በተዘረጋ ክብ) ቅርጽ ይጓዛሉ፤ ወደ ፀሐይ ሲቀርቡም ፍጥነታቸው በኬፕለር ህግ መሰረት ይጨምራል።")
                    put("visualDescription", "Luminous neon elliptical orbit lines tracing Earth, Mars, and Jupiter gliding through starry vacuum.")
                    put("cameraMotion", "Top-down Planetary Glide")
                    put("durationSeconds", 8)
                    put("lowerThirdBadgeEnglish", "Kepler's Laws of Planetary Motion")
                    put("lowerThirdBadgeAmharic", "የኬፕለር የፕላኔቶች እንቅስቃሴ ህግ")
                    put("focusX", 0.6)
                    put("focusY", 0.3)
                })
                put(JSONObject().apply {
                    put("sceneIndex", 3)
                    put("titleEnglish", "Rocky Terrestrial vs Gas Giants")
                    put("titleAmharic", "ዓለታማ እና ጋዛማ ፕላኔቶች")
                    put("narrationEnglish", "The inner four worlds are dense rocky spheres, while beyond the asteroid belt, majestic giants like Jupiter and Saturn hold swirling gas storms and icy rings.")
                    put("narrationAmharic", "የውስጠኛው ክፍል ፕላኔቶች ድንጋያማ ሲሆኑ፣ ከአስትሮይድ ቀበቶ ባሻገር ያሉት እንደ ጁፒተርና ሳተርን ያሉ ግዙፎች በጋዝ አውሎ ነፋሳትና የበረዶ ቀለበቶች የተሞሉ ናቸው።")
                    put("visualDescription", "Photorealistic 4K flyby along Saturn's icy ring plane, with sunlight scattering through billions of ice crystals.")
                    put("cameraMotion", "Ring Plane Flyby")
                    put("durationSeconds", 7)
                    put("lowerThirdBadgeEnglish", "Ring Dynamics & Magnetosphere")
                    put("lowerThirdBadgeAmharic", "የቀለበት ውቅር እና ማግኔቲክ መስክ")
                    put("focusX", 0.7)
                    put("focusY", 0.5)
                })
                put(JSONObject().apply {
                    put("sceneIndex", 4)
                    put("titleEnglish", "Earth's Habitable Goldilocks Zone")
                    put("titleAmharic", "የምድር ለሕይወት ምቹ ምህዋር (ጎልዲሎክስ)")
                    put("narrationEnglish", "Positioned at the perfect distance where liquid water can thrive, our Earth cradles life beneath a protective ozone blanket.")
                    put("narrationAmharic", "ምድር ፈሳሽ ውሃ እንዲኖር በሚያስችል ፍጹም ርቀት ላይ ተቀምጣለች፤ ይህም ሕይወት እንዲቀጥል ምቹ ሁኔታ ፈጥሯል።")
                    put("visualDescription", "Breathtaking 4K Earthrise showing azure oceans, white cloud swirls, and atmospheric limb shining against infinite cosmos.")
                    put("cameraMotion", "Earthrise Cinematic Reveal")
                    put("durationSeconds", 8)
                    put("lowerThirdBadgeEnglish", "Habitable Zone • Liquid Water")
                    put("lowerThirdBadgeAmharic", "ለሕይወት ምቹ ቀጠና • ፈሳሽ ውሃ")
                    put("focusX", 0.3)
                    put("focusY", 0.5)
                })
            }.toString()

            val solarTerms = JSONArray().apply {
                put(JSONObject().apply {
                    put("termEnglish", "Elliptical Orbit")
                    put("termAmharic", "ኤሊፕሳዊ ምህዋር")
                    put("phonetic", "I-lip-ti-kəl Ôr-bit")
                    put("definitionEnglish", "An oval-shaped path that an object in space takes around another celestial body.")
                    put("definitionAmharic", "አንድ የጠፈር አካል በሌላ አካል ዙሪያ የሚዞርበት እንቁላል መሰል የተዘረጋ ክብ መንገድ።")
                })
                put(JSONObject().apply {
                    put("termEnglish", "Habitable Zone")
                    put("termAmharic", "ለሕይወት ምቹ ቀጠና (ጎልዲሎክስ)")
                    put("phonetic", "Ha-bi-tə-bəl Zōn")
                    put("definitionEnglish", "The orbital region around a star where liquid water can exist on a planet's surface.")
                    put("definitionAmharic", "በአንድ ኮከብ ዙሪያ ፈሳሽ ውሃ በፕላኔት ገጽ ላይ ሊኖር የሚችልበት የሙቀት እና የርቀት ቀጠና።")
                })
            }.toString()

            val solarQuizOptions = JSONArray().apply {
                put("In perfect circles without speed variations")
                put("In elliptical paths governed by gravitational laws")
                put("In random zigzag directions")
                put("In stationary positions without moving")
            }.toString()

            val project2 = EducationalProject(
                titleEnglish = "The Solar System: Gravity, Orbits & Deep Space",
                titleAmharic = "የፀሐይ ሥርዓት፡ የስበት ህግ፣ ምህዋር እና የጠፈር ምስጢራት",
                topicCategory = "Astronomy & Physics",
                targetAudience = "Primary & High School",
                photoDrawableResName = "ref_solar_system_1791316676613",
                summaryEnglish = "A 4K educational journey examining Kepler's orbital mechanics, gravitational pull of the Sun, and what makes Earth uniquely habitable in the cosmos.",
                summaryAmharic = "የፀሐይ ስበት፣ የፕላኔቶች ኤሊፕሳዊ ምህዋር እና ምድር ለሕይወት ምቹ የሆነችበትን ምክንያት የሚያስረዳ ዘመናዊ የ4K የትምህርት ቪዲዮ።",
                veoPrompt4k = "Photorealistic 4K UHD astronomy film, 60fps, cosmic journey across the solar system, glowing Sun corona, hyper-detailed rendering of Saturn's ice rings and Earth's atmosphere, cinematic NASA quality space visual, 3840x2160.",
                scenesJson = solarScenes,
                keyTermsJson = solarTerms,
                quizQuestionEnglish = "According to Kepler's laws, what shape do planetary orbits follow around the Sun?",
                quizQuestionAmharic = "በኬፕለር ህግ መሰረት ፕላኔቶች በፀሐይ ዙሪያ የሚዞሩበት የምህዋር ቅርጽ ምን ይመስላል?",
                quizOptionsJson = solarQuizOptions,
                quizCorrectIndex = 1,
                isFavorite = true,
                isRendered = true
            )

            // Project 3: DNA & Cell Biology
            val dnaScenes = JSONArray().apply {
                put(JSONObject().apply {
                    put("sceneIndex", 1)
                    put("titleEnglish", "The Molecular Spiral of Life")
                    put("titleAmharic", "የሕይወት ሞለኪውላዊ ስፒራል")
                    put("narrationEnglish", "Inside every living cell resides DNA, an elegant twisted double helix carrying the genetic blueprint for all biological structures.")
                    put("narrationAmharic", "በእያንዳንዱ ህያው ህዋስ ውስጥ የሚገኘው ዲ ኤን ኤ (DNA)፣ የሁሉንም ፍጥረታት የዘረ-መል መመሪያ የያዘ የተጠማዘዘ ድርብ ሄሊክስ ነው።")
                    put("visualDescription", "Hyper-detailed 4K macro camera plunging into microscopic cytoplasm, revealing phosphorescent glowing sugar-phosphate backbones.")
                    put("cameraMotion", "Microscopic Dive")
                    put("durationSeconds", 7)
                    put("lowerThirdBadgeEnglish", "Deoxyribonucleic Acid (DNA)")
                    put("lowerThirdBadgeAmharic", "ዲኦክሲራይቦኑክሊክ አሲድ (ዲ ኤን ኤ)")
                    put("focusX", 0.5)
                    put("focusY", 0.5)
                })
                put(JSONObject().apply {
                    put("sceneIndex", 2)
                    put("titleEnglish", "Nucleotide Complementary Base Pairing")
                    put("titleAmharic", "የኑክሊዮታይድ ጥንዶች መስተጋብር")
                    put("narrationEnglish", "Four chemical bases encode life: Adenine pairs exclusively with Thymine, while Cytosine locks with Guanine via precise hydrogen bonds.")
                    put("narrationAmharic", "አራቱ ኬሚካላዊ መሰረቶች ሕይወትን ይገልጻሉ፡ አደኒን ከታይሚን ጋር፣ ሳይቶሲን ደግሞ ከጓኒን ጋር በሃይድሮጅን ትስስር ይገናኛሉ።")
                    put("visualDescription", "3D molecular visualization of chemical rungs snapping into place with glowing energy lines denoting hydrogen bonds.")
                    put("cameraMotion", "Base Pair Helix Rotation")
                    put("durationSeconds", 8)
                    put("lowerThirdBadgeEnglish", "A-T & C-G Base Pairing Rules")
                    put("lowerThirdBadgeAmharic", "የኤ-ቲ እና ሲ-ጂ ጥንዶች ህግ")
                    put("focusX", 0.4)
                    put("focusY", 0.4)
                })
                put(JSONObject().apply {
                    put("sceneIndex", 3)
                    put("titleEnglish", "Replication & Cellular Mitosis")
                    put("titleAmharic", "የሕዋስ መከፋፈል እና ማባዛት")
                    put("narrationEnglish", "Before a cell divides, enzymes unzip the double strands to synthesize exact replicas, ensuring genetic continuity across generations.")
                    put("narrationAmharic", "ህዋስ ከመከፋፈሉ በፊት፣ ኢንዛይሞች የዲ ኤን ኤ ክሮችን በመክፈት ትክክለኛ አምሳያቸውን ያባዛሉ፤ ይህም የዘር ውርስ እንዳይቋረጥ ያደርጋል።")
                    put("visualDescription", "DNA polymerase enzyme animated traversing along the unzipped strand creating new daughter helices.")
                    put("cameraMotion", "Enzyme Tracking Shot")
                    put("durationSeconds", 8)
                    put("lowerThirdBadgeEnglish", "DNA Polymerase Replication")
                    put("lowerThirdBadgeAmharic", "የዲ ኤን ኤ ፖሊመሬዝ ማባዛት")
                    put("focusX", 0.6)
                    put("focusY", 0.6)
                })
            }.toString()

            val dnaTerms = JSONArray().apply {
                put(JSONObject().apply {
                    put("termEnglish", "Double Helix")
                    put("termAmharic", "ድርብ ሄሊክስ")
                    put("phonetic", "Dub-əl Hē-liks")
                    put("definitionEnglish", "The spiral arrangement of two complementary strands of DNA.")
                    put("definitionAmharic", "የሁለት ተጓዳኝ የዲ ኤን ኤ ክሮች የተጠማዘዘ የመሰላል ቅርጽ ውቅር።")
                })
                put(JSONObject().apply {
                    put("termEnglish", "Nucleotide")
                    put("termAmharic", "ኑክሊዮታይድ")
                    put("phonetic", "Noo-klē-ə-tīd")
                    put("definitionEnglish", "The basic building block of nucleic acids, consisting of a sugar, phosphate, and nitrogen base.")
                    put("definitionAmharic", "የዲ ኤን ኤ እና አር ኤን ኤ መሰረታዊ ህንፃ አሃድ፤ ከስኳር፣ ፎስፌት እና ናይትሮጅን የተሰራ።")
                })
            }.toString()

            val dnaQuizOptions = JSONArray().apply {
                put("Adenine with Guanine, Thymine with Cytosine")
                put("Adenine with Thymine, Cytosine with Guanine")
                put("All bases connect randomly without order")
                put("Bases do not connect to each other")
            }.toString()

            val project3 = EducationalProject(
                titleEnglish = "DNA & Genetics: The Molecular Blueprint of Life",
                titleAmharic = "ዲ ኤን ኤ (DNA) እና ዘረ-መል፡ የሕይወት ሞለኪውላዊ መመሪያ",
                topicCategory = "Biology & Genetics",
                targetAudience = "High School & University",
                photoDrawableResName = "ref_dna_cell_1791316688956",
                summaryEnglish = "Uncover how the double helix structure, nucleotide base pairing, and cellular replication power all living organisms.",
                summaryAmharic = "የዲ ኤን ኤ ድርብ ሄሊክስ ውቅር፣ የኑክሊዮታይድ ጥንዶች እና የሕዋስ መከፋፈል በህይወት ባላቸው ፍጥረታት ውስጥ እንዴት እንደሚሰሩ የሚያሳይ ትምህርታዊ ቪዲዮ።",
                veoPrompt4k = "Microscopic 4K UHD biological visualization, 60fps, glowing bioluminescent DNA double helix rotating in cellular cytoplasm, scientific accuracy, medical laboratory render, 3840x2160.",
                scenesJson = dnaScenes,
                keyTermsJson = dnaTerms,
                quizQuestionEnglish = "Which nucleotide base pairs correctly with Adenine in DNA?",
                quizQuestionAmharic = "በዲ ኤን ኤ (DNA) ውስጥ ከአደኒን (A) ጋር የሚጣመረው የትኛው ነው?",
                quizOptionsJson = dnaQuizOptions,
                quizCorrectIndex = 1,
                isFavorite = false,
                isRendered = true
            )

            // Project 4: Ethiopian Coffee Botany
            val coffeeScenes = JSONArray().apply {
                put(JSONObject().apply {
                    put("sceneIndex", 1)
                    put("titleEnglish", "Origin in the Cloud Forests of Kaffa")
                    put("titleAmharic", "የከፋ ደን እና የቡና መገኛ")
                    put("narrationEnglish", "In the misty highland forests of Ethiopia, wild Coffea arabica developed under dense canopy shades, flourishing in fertile volcanic soils.")
                    put("narrationAmharic", "በኢትዮጵያ የከፋ ጭጋጋማ የደጋ ደኖች ውስጥ፣ የዱር ቡና (Coffea arabica) ለም በሆነ የእሳተ ገሞራ አፈር እና በጥላ ስር ተፈጥሯዊ እድገቱን ጀመረ።")
                    put("visualDescription", "High definition 4K macro shot of morning dew droplets glinting on lush green coffee leaves in misty mountain light.")
                    put("cameraMotion", "Dewdrop Macro Slide")
                    put("durationSeconds", 7)
                    put("lowerThirdBadgeEnglish", "Coffea Arabica • Indigenous Flora")
                    put("lowerThirdBadgeAmharic", "የዱር ቡና ተክል • የሀገር በቀል እጽዋት")
                    put("focusX", 0.5)
                    put("focusY", 0.3)
                })
                put(JSONObject().apply {
                    put("sceneIndex", 2)
                    put("titleEnglish", "The Botany of the Cherry & Caffeine Defense")
                    put("titleAmharic", "የቡና ፍሬ ስነ-ህይወት እና የካፌይን ጥበቃ")
                    put("narrationEnglish", "The plant produces caffeine as a natural chemical defense against pests, while encapsulating two precious seeds inside a nutrient-rich pulp.")
                    put("narrationAmharic", "የቡና ተክል ተባዮችን ለመከላከል በተፈጥሮ ካፌይን ያመነጫል፤ በውስጡም ሁለት የዘር ፍሬዎችን በጣፋጭ ስጋ ይሸፍናል።")
                    put("visualDescription", "Cross-section 4K graphic revealing the exocarp, mesocarp mucilage, parchment, and twin green coffee seeds.")
                    put("cameraMotion", "Cross-Section Exploded Reveal")
                    put("durationSeconds", 8)
                    put("lowerThirdBadgeEnglish", "Alkaloid Defense • Seed Anatomy")
                    put("lowerThirdBadgeAmharic", "የካፌይን ተፈጥሯዊ መከላከያ • የፍሬ ውቅር")
                    put("focusX", 0.5)
                    put("focusY", 0.6)
                })
                put(JSONObject().apply {
                    put("sceneIndex", 3)
                    put("titleEnglish", "Highland Agroforestry & Climate Balance")
                    put("titleAmharic", "የደጋ እርሻ ደን እና የአየር ንብረት ሚዛን")
                    put("narrationEnglish", "Shade-grown coffee fosters biodiversity, protects local water tables, and represents sustainable agriculture passed down through generations.")
                    put("narrationAmharic", "በጥላ ስር የሚበቅል ቡና የብዝሃ-ህይወትን ይጠብቃል፣ የከርሰ ምድር ውሃን ይንከባከባል፣ እንዲሁም ዘላቂ የተፈጥሮ ጥበቃ እርሻ ምሳሌ ነው።")
                    put("visualDescription", "Sweeping 4K drone panorama of multi-canopy agroforestry farm with native trees shielding healthy coffee shrubs.")
                    put("cameraMotion", "Canopy Drone Sweep")
                    put("durationSeconds", 8)
                    put("lowerThirdBadgeEnglish", "Sustainable Agroforestry")
                    put("lowerThirdBadgeAmharic", "ዘላቂ የተፈጥሮ ደን እርሻ")
                    put("focusX", 0.5)
                    put("focusY", 0.5)
                })
            }.toString()

            val coffeeTerms = JSONArray().apply {
                put(JSONObject().apply {
                    put("termEnglish", "Agroforestry")
                    put("termAmharic", "የደን እርሻ (አግሮፎረስትሪ)")
                    put("phonetic", "A-grō-fôr-ə-strē")
                    put("definitionEnglish", "Land use management combining agricultural crops with trees and shrubs.")
                    put("definitionAmharic", "እርሻን ከዛፎችና ቁጥቋጦዎች ጋር በማቀናጀት አካባቢን ሳይጎዱ የሚከናወን ዘመናዊ የምርት ዘዴ።")
                })
                put(JSONObject().apply {
                    put("termEnglish", "Alkaloid")
                    put("termAmharic", "አልካሎይድ (እንደ ካፌይን ያለ ኬሚካል)")
                    put("phonetic", "Al-kə-loid")
                    put("definitionEnglish", "Naturally occurring organic compounds that have physiological actions on humans and insects.")
                    put("definitionAmharic", "በተክሎች ውስጥ የሚፈጠሩና በሰውና በነፍሳት ላይ የፊዚዮሎጂ ተጽዕኖ የሚያሳድሩ የተፈጥሮ ኬሚካሎች።")
                })
            }.toString()

            val coffeeQuizOptions = JSONArray().apply {
                put("As a natural defense mechanism against pests and herbivores")
                put("To change the color of the soil")
                put("To attract heavy rainfall")
                put("It does not produce caffeine")
            }.toString()

            val project4 = EducationalProject(
                titleEnglish = "Coffea Arabica Botany: Highland Agroforestry",
                titleAmharic = "የቡና እጽዋት ስነ-ህይወት፡ የደጋ ደን እርሻ እና ዘላቂ ልማት",
                topicCategory = "Botany & Agriculture",
                targetAudience = "General Public & Students",
                photoDrawableResName = "ref_coffee_plant_1791316704669",
                summaryEnglish = "Discover the botanical secrets of Coffea arabica, from natural caffeine defense to high-altitude biodiversity in Ethiopian cloud forests.",
                summaryAmharic = "የቡና ተክል ስነ-ህይወት፣ የተፈጥሮ ካፌይን መከላከያ ባህሪው እና በጥላ ስር የሚበቅል ቡና ለአካባቢ ጥበቃ የሚሰጠውን ጥቅም የሚያብራራ ቪዲዮ።",
                veoPrompt4k = "Crisp 4K macro botanical cinematography, 60fps, Coffea arabica branch with deep red ripe cherries, morning sunlight through misty cloud forest, vivid green leaves, documentary realism, 3840x2160.",
                scenesJson = coffeeScenes,
                keyTermsJson = coffeeTerms,
                quizQuestionEnglish = "Why does the coffee plant naturally produce caffeine in its leaves and beans?",
                quizQuestionAmharic = "የቡና ተክል በተፈጥሮው ካፌይን የሚያመነጨው ለምን ዓላማ ነው?",
                quizOptionsJson = coffeeQuizOptions,
                quizCorrectIndex = 0,
                isFavorite = false,
                isRendered = true
            )

            dao.insertProject(project1)
            dao.insertProject(project2)
            dao.insertProject(project3)
            dao.insertProject(project4)
        }
    }
}
