package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PyqQuestion
import com.example.ui.StudyViewModel
import com.example.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeMockScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val isTestActive by viewModel.isTestActive.collectAsState()
    val latestResult by viewModel.latestMockResult.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBg)
    ) {
        when {
            isTestActive -> {
                MockTestSimulatorLayout(viewModel)
            }
            latestResult != null -> {
                MockResultReportLayout(viewModel)
            }
            else -> {
                PracticeAndMockSelectorLayout(viewModel)
            }
        }
    }
}

// --- Layout 1: Selector / Syllabus / PYQs Explorer ---

@Composable
fun PracticeAndMockSelectorLayout(viewModel: StudyViewModel) {
    var subTab by remember { mutableStateOf(0) } // 0: PYQs, 1: Mock Tests
    val pyqsList by viewModel.pyqs.collectAsState()

    var filterSubject by remember { mutableStateOf("All") }
    var filterDifficulty by remember { mutableStateOf("All") }

    val subjects = listOf("All", "Mathematics", "Reasoning", "English Language", "General Awareness")
    val difficulties = listOf("All", "Easy", "Medium", "Hard")

    val filteredPyqs = pyqsList.filter { q ->
        (filterSubject == "All" || q.subject == filterSubject) &&
                (filterDifficulty == "All" || q.difficulty == filterDifficulty)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sub tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardSlate)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (subTab == 0) IndigoPrimary else Color.Transparent)
                    .clickable { subTab = 0 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Syllabus PYQs",
                    fontWeight = FontWeight.Bold,
                    color = if (subTab == 0) Color.White else SoftMutedText,
                    fontSize = 13.sp
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (subTab == 1) IndigoPrimary else Color.Transparent)
                    .clickable { subTab = 1 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Mock Tests & Papers",
                    fontWeight = FontWeight.Bold,
                    color = if (subTab == 1) Color.White else SoftMutedText,
                    fontSize = 13.sp
                )
            }
        }

        if (subTab == 0) {
            // PYQ list with Filters
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Subject Filter Scroll
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    subjects.forEach { s ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (filterSubject == s) AmberSecondary else CardSlate)
                                .clickable { filterSubject = s }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = s.split(" ").first(),
                                color = if (filterSubject == s) MidnightBg else SoftWhiteText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Difficulty Filter Scroll
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    difficulties.forEach { d ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (filterDifficulty == d) TealTertiary else CardSlate)
                                .clickable { filterDifficulty = d }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = d,
                                color = if (filterDifficulty == d) MidnightBg else SoftWhiteText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Questions Column
            if (filteredPyqs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No questions found for this filter combination.", color = SoftMutedText, fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredPyqs) { q ->
                        PyqQuestionItemCard(q = q, onSolve = { ans ->
                            viewModel.solvePyqSingle(q.id, ans)
                        }, onBookmarkToggle = {
                            viewModel.togglePyqBookmark(q.id, !q.isBookmarked)
                        })
                    }
                }
            }
        } else {
            // Mock test selector screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                Icon(
                    imageVector = Icons.Default.Quiz,
                    contentDescription = null,
                    tint = AmberSecondary,
                    modifier = Modifier.size(72.dp)
                )

                Text(
                    text = "Personalized Mock Exam Simulator",
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Test yourself in strict exam environments. Our AI will automatically construct a 10-question high-yield multi-subject mock with active timer tracking, standard +2.0 mark scores, and -0.5 negative marks to prepare you for CHSL pressure.",
                    color = SoftMutedText,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardSlate)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("10 Minutes strict timer", color = SoftWhiteText, fontSize = 13.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = CrimsonError, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Standard -0.5 negative marking applied", color = SoftWhiteText, fontSize = 13.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TealTertiary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Dynamic post-exam AI mistake & guessing solver analysis", color = SoftWhiteText, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.startMockTest() },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary, contentColor = MidnightBg),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("start_mock_button")
                ) {
                    Text("START COMPACT MOCK EXAM", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                }
            }
        }
    }
}

// --- Question Item card for PYQs Tab ---

@Composable
fun PyqQuestionItemCard(
    q: PyqQuestion,
    onSolve: (String) -> Unit,
    onBookmarkToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSlate)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header: Subject, Year
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = q.subject.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberSecondary,
                        modifier = Modifier
                            .background(AmberSecondary.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${q.year} - ${q.shift}",
                        fontSize = 10.sp,
                        color = SoftMutedText
                    )
                }

                IconButton(onClick = onBookmarkToggle) {
                    Icon(
                        imageVector = if (q.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (q.isBookmarked) AmberSecondary else SoftMutedText,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Question Text
            Text(
                text = q.question,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 14.sp
            )

            // Options list
            val options = listOf("A" to q.optionA, "B" to q.optionB, "C" to q.optionC, "D" to q.optionD)
            options.forEach { (label, value) ->
                val isSelected = q.userAnswer == label
                val isCorrect = q.correctOption == label
                val cardBorderColor = when {
                    q.userAnswer != null && isCorrect -> EmeraldSuccess
                    isSelected && !isCorrect -> CrimsonError
                    else -> Color.Transparent
                }

                val containerBgColor = when {
                    q.userAnswer != null && isCorrect -> EmeraldSuccess.copy(alpha = 0.12f)
                    isSelected && !isCorrect -> CrimsonError.copy(alpha = 0.12f)
                    else -> MidnightBg
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = q.userAnswer == null) { onSolve(label) }
                        .border(1.dp, cardBorderColor, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = containerBgColor)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) AmberSecondary else SoftMutedText.copy(alpha = 0.2f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MidnightBg else Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            value,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // AI explanation box if solved
            if (q.userAnswer != null && q.aiExplanation != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MidnightBg)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("AI Mentor Explanation", fontWeight = FontWeight.Bold, color = AmberSecondary, fontSize = 12.sp)
                        }
                        Text(
                            text = q.aiExplanation,
                            fontSize = 12.sp,
                            color = SoftWhiteText
                        )
                    }
                }
            }
        }
    }
}

// --- Layout 2: Active Mock Test Taking Simulator ---

@Composable
fun MockTestSimulatorLayout(viewModel: StudyViewModel) {
    val questions by viewModel.activeTestQuestions.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val answers by viewModel.selectedAnswers.collectAsState()
    val timerRemaining by viewModel.testTimeRemaining.collectAsState()

    if (questions.isEmpty()) return

    val q = questions[currentIndex]

    val minutes = timerRemaining / 60
    val seconds = timerRemaining % 60
    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Exam header with timer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("CHSL Mock Simulator", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                Text("Question ${currentIndex + 1} of ${questions.size}", color = SoftMutedText, fontSize = 12.sp)
            }

            // Live Timer CountDown
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (timerRemaining < 60) CrimsonError.copy(alpha = 0.2f) else CardSlate)
                    .border(1.dp, if (timerRemaining < 60) CrimsonError else TealTertiary, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = if (timerRemaining < 60) CrimsonError else TealTertiary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formattedTime,
                    color = if (timerRemaining < 60) CrimsonError else TealTertiary,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
        }

        // Section tags tracker
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            questions.forEachIndexed { idx, question ->
                val isAnswered = answers.containsKey(question.id)
                val isCurrent = currentIndex == idx
                val boxBg = when {
                    isCurrent -> AmberSecondary
                    isAnswered -> TealTertiary
                    else -> CardSlate
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(boxBg)
                        .clickable { /* Navigation inside active questions is simulated */ },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        (idx + 1).toString(),
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrent || isAnswered) MidnightBg else Color.White,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Active Question Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = CardSlate)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = q.subject.uppercase(),
                    color = AmberSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = q.question,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Option Choice Buttons
                val opts = listOf("A" to q.optionA, "B" to q.optionB, "C" to q.optionC, "D" to q.optionD)
                opts.forEach { (lbl, valStr) ->
                    val isChosen = answers[q.id] == lbl

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectMockAnswer(q.id, lbl) }
                            .border(
                                width = 1.dp,
                                color = if (isChosen) AmberSecondary else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isChosen) AmberSecondary.copy(alpha = 0.15f) else MidnightBg
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isChosen,
                                onClick = { viewModel.selectMockAnswer(q.id, lbl) },
                                colors = RadioButtonDefaults.colors(selectedColor = AmberSecondary)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(valStr, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Bottom Simulator controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { viewModel.cancelActiveTest() }) {
                Text("Cancel Test", color = CrimsonError)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (currentIndex > 0) {
                    IconButton(
                        onClick = { viewModel.navigateMockQuestion(-1) },
                        modifier = Modifier.background(CardSlate, CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                }

                if (currentIndex < questions.size - 1) {
                    IconButton(
                        onClick = { viewModel.navigateMockQuestion(1) },
                        modifier = Modifier.background(CardSlate, CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
                    }
                } else {
                    Button(
                        onClick = { viewModel.submitMockTest() },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("submit_test_button")
                    ) {
                        Text("SUBMIT EXAM", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// --- Layout 3: Post-Exam Scorecard and AI Analyst Report ---

@Composable
fun MockResultReportLayout(viewModel: StudyViewModel) {
    val result by viewModel.latestMockResult.collectAsState()
    val mockRes = result ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.MilitaryTech,
            contentDescription = null,
            tint = AmberSecondary,
            modifier = Modifier.size(72.dp)
        )

        Text(
            text = "Mock Report: ${mockRes.title}",
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            fontSize = 18.sp
        )

        // Scorecard Card Grid
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSlate)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("EXAM SCORECARD", color = SoftMutedText, fontWeight = FontWeight.Bold, fontSize = 11.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${mockRes.score} / ${mockRes.maxScore}", fontWeight = FontWeight.Black, color = AmberSecondary, fontSize = 22.sp)
                        Text("Your Score", fontSize = 10.sp, color = SoftMutedText)
                    }

                    VerticalDivider(color = SoftMutedText.copy(alpha = 0.2f), modifier = Modifier.height(40.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${(mockRes.score / mockRes.maxScore * 100).toInt()}%", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        Text("Accuracy", fontSize = 10.sp, color = SoftMutedText)
                    }

                    VerticalDivider(color = SoftMutedText.copy(alpha = 0.2f), modifier = Modifier.height(40.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val predictedPercentile = 75 + (mockRes.score / mockRes.maxScore * 24).toInt()
                        Text("${predictedPercentile}th", fontWeight = FontWeight.Bold, color = TealTertiary, fontSize = 16.sp)
                        Text("Est. Percentile", fontSize = 10.sp, color = SoftMutedText)
                    }
                }

                HorizontalDivider(color = SoftMutedText.copy(alpha = 0.1f))

                // Breakdown counts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text("✅ Correct: ${mockRes.correctAnswers}", color = EmeraldSuccess, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("❌ Wrong: ${mockRes.wrongAnswers}", color = CrimsonError, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("⚪ Skipped: ${mockRes.skippedAnswers}", color = SoftMutedText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // AI Mentor Performance Diagnosis Report
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AmberSecondary.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CardSlate),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI Mentor Analysis & Remnants Diagnosis", fontWeight = FontWeight.Bold, color = AmberSecondary, fontSize = 14.sp)
                }

                if (mockRes.aiFeedback.isBlank()) {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AmberSecondary)
                    }
                } else {
                    Text(
                        text = mockRes.aiFeedback,
                        fontSize = 13.sp,
                        color = SoftWhiteText,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Close report and return to Selector
        Button(
            onClick = { viewModel.cancelActiveTest() }, // Simply resets test states to null
            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("ACKNOWLEDGE & ADJUST TIMETABLE", fontWeight = FontWeight.Bold)
        }
    }
}
