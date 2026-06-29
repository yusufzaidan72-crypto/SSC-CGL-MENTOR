package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CurrentAffair
import com.example.data.VocabWord
import com.example.ui.StudyViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyllabusLibraryScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    var selectedCategoryTab by remember { mutableStateOf(0) } // 0: Syllabus, 1: Formula Book, 2: Vocab Builder, 3: Current Affairs
    val categoryTabs = listOf("Syllabus", "Formulas", "Vocabulary", "Current Affairs")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBg)
    ) {
        // Top Scrollable Category Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedCategoryTab,
            containerColor = CardSlate,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedCategoryTab]),
                    color = AmberSecondary
                )
            }
        ) {
            categoryTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedCategoryTab == index,
                    onClick = { selectedCategoryTab = index },
                    text = {
                        Text(
                            title,
                            fontWeight = if (selectedCategoryTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    },
                    selectedContentColor = AmberSecondary,
                    unselectedContentColor = SoftMutedText
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            when (selectedCategoryTab) {
                0 -> SyllabusExplorerSection()
                1 -> FormulaBookSection()
                2 -> VocabBuilderSection(viewModel)
                3 -> CurrentAffairsSection(viewModel)
            }
        }
    }
}

// --- Tab 0: Syllabus Explorer ---

@Composable
fun SyllabusExplorerSection() {
    var expandedSubject by remember { mutableStateOf("Mathematics") }
    val subjects = listOf("Mathematics", "Reasoning", "English Language", "General Awareness")

    val syllabusData = mapOf(
        "Mathematics" to listOf(
            ChapterData("Arithmetic - Percentages", "Successive addition: A + B + AB/100. Ratio conversion is vital (16.66% = 1/6).", "Short Trick: To increase a number by 20%, multiply by 1.2 directly."),
            ChapterData("Algebra - Linear Equations", "Standard forms, quadratic solutions, factorization tricks.", "Trick: If x + 1/x = 3, then x² + 1/x² = 3² - 2 = 7."),
            ChapterData("Trigonometry", "Identities (sin²θ + cos²θ = 1). Angle values for 0°, 30°, 45°, 60°, 90°.", "Trick: Use value substitution (θ = 45°) to crack complex identities in 10s.")
        ),
        "Reasoning" to listOf(
            ChapterData("Coding-Decoding", "Letter position codes (E=5, J=10, O=15, T=20, Y=25). Opposite letters.", "Trick: SUM of opposite pairs is always 27 (A[1] + Z[26] = 27)."),
            ChapterData("Syllogisms", "Venn diagrams approach, 'Some' vs 'All' conditions, possibility rules.", "Trick: If statements are positive, a negative conclusion is always false."),
            ChapterData("Mathematical Operations", "Sign interchanging equations, BODMAS rule prioritization.", "Trick: Focus first on the DIVISION sign to filter incorrect options instantly.")
        ),
        "English Language" to listOf(
            ChapterData("Subject-Verb Agreement", "Singular subjects take singular verbs. Plural takes plural.", "Rule: Subjects joined by 'and' take a plural verb. 'Either/Or' takes verb closest to subject."),
            ChapterData("Direct & Indirect Speech", "Tense change guidelines. Present simple changes to Past simple.", "Trick: Pronoun change formula is SON (Subject-1st Person, Object-2nd Person, No change-3rd)."),
            ChapterData("Active & Passive Voice", "Subject and Object swap places. Always use 3rd form of verb.", "Trick: In passive voice, the auxiliary verb 'be' (is/are/was/were/been) is mandatory.")
        ),
        "General Awareness" to listOf(
            ChapterData("Indian Polity - Constitution", "Fundamental Rights (Articles 12-35), Directive Principles, Schedules.", "Shortcut: Remember schedules via 'TEARS OF OLD PM' mnemonic."),
            ChapterData("General Science - Physics", "Newton's laws, light reflection, lens formula, work-energy principles.", "Tricks: Concave mirror converges, Convex mirror diverges."),
            ChapterData("History - Freedom Struggle", "Important years: Revolt of 1857, Non-Cooperation (1920), Quit India (1942).", "Shortcut: Chronological memory using visual flow timelines.")
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            subjects.forEach { sub ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (expandedSubject == sub) IndigoPrimary else CardSlate)
                        .clickable { expandedSubject = sub }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = sub.split(" ").first(),
                        color = if (expandedSubject == sub) Color.White else SoftMutedText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "$expandedSubject Syllabus & Topics",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        val chapters = syllabusData[expandedSubject] ?: emptyList()
        chapters.forEach { chapter ->
            var isExpanded by remember { mutableStateOf(false) }
            val rotationState by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "rot")

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardSlate, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = chapter.title,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { isExpanded = !isExpanded }) {
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = AmberSecondary,
                                modifier = Modifier.rotate(rotationState)
                            )
                        }
                    }

                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = SoftMutedText.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Core Revision Notes:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealTertiary
                        )
                        Text(
                            text = chapter.notes,
                            fontSize = 13.sp,
                            color = SoftWhiteText,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "Short Cut Trick:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberSecondary
                        )
                        Text(
                            text = chapter.trick,
                            fontSize = 13.sp,
                            color = SoftWhiteText,
                            modifier = Modifier
                                .background(AmberSecondary.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

data class ChapterData(val title: String, val notes: String, val trick: String)

// --- Tab 1: Formula Book ---

@Composable
fun FormulaBookSection() {
    var searchQuery by remember { mutableStateOf("") }

    val formulaList = listOf(
        FormulaItem("Trigonometry Ratio Identites", "Mathematics", "sin²θ + cos²θ = 1\n1 + tan²θ = sec²θ\n1 + cot²θ = cosec²θ"),
        FormulaItem("Average Speed Formula", "Mathematics", "For equal distance parts S1, S2:\nAvg Speed = 2 * S1 * S2 / (S1 + S2)"),
        FormulaItem("Compound Interest 2yr Diff", "Mathematics", "Difference (CI - SI) for 2 years:\nDiff = P * (R / 100)²"),
        FormulaItem("Active/Passive Tense Swap", "English", "Simple Present (Write) -> is/are written\nSimple Past (Wrote) -> was/were written"),
        FormulaItem("Syllogisms Possibility Cases", "Reasoning", "If basic Venn-diagram allows a case without violating any statement, the POSSIBILITY conclusion is true."),
        FormulaItem("Mughal Dynasty Chronology", "Static GA", "Remember 'BHAJSA' mnemonic:\nB - Babur, H - Humayun, A - Akbar, J - Jahangir, S - Shah Jahan, A - Aurangzeb")
    )

    val filteredFormulas = formulaList.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.subject.contains(searchQuery, ignoreCase = true) ||
                it.content.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search formulas, tricks, or rules...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AmberSecondary) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AmberSecondary,
                unfocusedBorderColor = CardSlate,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filteredFormulas) { f ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardSlate)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = f.title,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = f.subject,
                                color = if (f.subject == "Mathematics") TealTertiary else AmberSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier
                                    .background(
                                        if (f.subject == "Mathematics") TealTertiary.copy(alpha = 0.12f) else AmberSecondary.copy(alpha = 0.12f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = f.content,
                            fontSize = 13.sp,
                            color = SoftWhiteText,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MidnightBg, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}

data class FormulaItem(val title: String, val subject: String, val content: String)

// --- Tab 2: Vocab Builder ---

@Composable
fun VocabBuilderSection(viewModel: StudyViewModel) {
    val words by viewModel.vocabWords.collectAsState()
    var currentWordIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    if (words.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AmberSecondary)
        }
    } else {
        val word = words[currentWordIndex % words.size]

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Daily SSC Flashcards",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 16.sp
            )

            // Flashcard box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clickable { isFlipped = !isFlipped }
                    .border(1.dp, if (isFlipped) TealTertiary.copy(alpha = 0.4f) else AmberSecondary.copy(alpha = 0.4f), RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardSlate)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (!isFlipped) {
                            Text(
                                text = word.type.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = AmberSecondary,
                                modifier = Modifier
                                    .background(AmberSecondary.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                            Text(
                                text = word.word,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "(Tap to Flip)",
                                fontSize = 11.sp,
                                color = SoftMutedText
                            )
                        } else {
                            Text(
                                text = "MEANING",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealTertiary
                            )
                            Text(
                                text = word.meaning,
                                fontSize = 15.sp,
                                color = SoftWhiteText,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "SYNONYMS: ${word.synonyms}",
                                fontSize = 12.sp,
                                color = SoftMutedText,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "ANTONYMS: ${word.antonyms}",
                                fontSize = 12.sp,
                                color = SoftMutedText,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "e.g. \"${word.example}\"",
                                fontSize = 12.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = AmberSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Word Controllers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Learned Tick
                IconButton(
                    onClick = {
                        viewModel.toggleVocabLearned(word.id, !word.isLearned)
                    },
                    modifier = Modifier.background(CardSlate, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Mark Learned",
                        tint = if (word.isLearned) EmeraldSuccess else SoftMutedText
                    )
                }

                // Previous/Next arrows
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = {
                            isFlipped = false
                            if (currentWordIndex > 0) currentWordIndex--
                        },
                        modifier = Modifier.background(CardSlate, CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Prev", tint = Color.White)
                    }

                    IconButton(
                        onClick = {
                            isFlipped = false
                            currentWordIndex++
                        },
                        modifier = Modifier.background(CardSlate, CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next", tint = Color.White)
                    }
                }

                // Bookmark toggle
                IconButton(
                    onClick = {
                        viewModel.toggleVocabBookmark(word.id, !word.isBookmarked)
                    },
                    modifier = Modifier.background(CardSlate, CircleShape)
                ) {
                    Icon(
                        imageVector = if (word.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (word.isBookmarked) AmberSecondary else SoftMutedText
                    )
                }
            }
        }
    }
}

// --- Tab 3: Current Affairs ---

@Composable
fun CurrentAffairsSection(viewModel: StudyViewModel) {
    val articles by viewModel.currentAffairs.collectAsState()

    if (articles.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AmberSecondary)
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = "Daily Current Affairs Brief",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            items(articles) { article ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardSlate)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = article.category.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberSecondary,
                                modifier = Modifier
                                    .background(AmberSecondary.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                            Text(
                                text = article.date,
                                fontSize = 11.sp,
                                color = SoftMutedText
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = article.title,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = article.content,
                            fontSize = 13.sp,
                            color = SoftWhiteText
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = { viewModel.toggleCurrentAffairBookmark(article.id, !article.isBookmarked) }
                            ) {
                                Icon(
                                    imageVector = if (article.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (article.isBookmarked) AmberSecondary else SoftMutedText,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
