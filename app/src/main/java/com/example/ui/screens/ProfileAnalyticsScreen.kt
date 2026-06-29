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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CurrentAffair
import com.example.data.PyqQuestion
import com.example.data.UserProfile
import com.example.ui.StudyViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAnalyticsScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    var selectedSectionTab by remember { mutableStateOf(0) } // 0: Analytics, 1: Profile, 2: Achievements, 3: Admin, 4: Premium
    val sectionTabs = listOf("Analytics", "Profile", "Badges", "Admin", "Premium")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBg)
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedSectionTab,
            containerColor = CardSlate,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSectionTab]),
                    color = AmberSecondary
                )
            }
        ) {
            sectionTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedSectionTab == index,
                    onClick = { selectedSectionTab = index },
                    text = {
                        Text(
                            title,
                            fontWeight = if (selectedSectionTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    },
                    selectedContentColor = AmberSecondary,
                    unselectedContentColor = SoftMutedText
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            when (selectedSectionTab) {
                0 -> AnalyticsSection(viewModel)
                1 -> ProfileDetailsSection(viewModel)
                2 -> AchievementsSection(viewModel)
                3 -> AdminPanelSection(viewModel)
                4 -> PremiumFeaturesSection()
            }
        }
    }
}

// --- Section 0: Canvas Graphs and Progress ---

@Composable
fun AnalyticsSection(viewModel: StudyViewModel) {
    val progressLogs by viewModel.progressLogs.collectAsState()
    val tasks by viewModel.todayTasks.collectAsState()

    val totalCompletedTasks = tasks.count { it.isCompleted }
    val totalTasks = tasks.size.coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Your Preparation Metrics", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)

        // Today's completeness card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSlate)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier.size(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { totalCompletedTasks.toFloat() / totalTasks },
                        color = EmeraldSuccess,
                        trackColor = SoftMutedText.copy(alpha = 0.1f),
                        strokeWidth = 6.dp
                    )
                    Text(
                        text = "$totalCompletedTasks/$totalTasks",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
                Column {
                    Text("Today's Progress Checklist", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    Text("You've cleared ${(totalCompletedTasks.toFloat() / totalTasks * 100).toInt()}% of today's study planner goals.", color = SoftMutedText, fontSize = 11.sp)
                }
            }
        }

        // Custom Canvas Chart 1: Study hours per day (Bar Chart)
        Text("Weekly Study Hours logged", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSlate)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    val width = size.width
                    val height = size.height

                    // Drawing guidelines
                    drawLine(color = SoftMutedText.copy(alpha = 0.1f), start = Offset(0f, height * 0.25f), end = Offset(width, height * 0.25f), strokeWidth = 1f)
                    drawLine(color = SoftMutedText.copy(alpha = 0.1f), start = Offset(0f, height * 0.5f), end = Offset(width, height * 0.5f), strokeWidth = 1f)
                    drawLine(color = SoftMutedText.copy(alpha = 0.1f), start = Offset(0f, height * 0.75f), end = Offset(width, height * 0.75f), strokeWidth = 1f)

                    val barCount = 7
                    val barWidth = 24.dp.toPx()
                    val spacing = (width - (barCount * barWidth)) / (barCount + 1)

                    // Seeded logs are 6 items
                    val hoursList = progressLogs.map { it.studyHours }.takeLast(7)
                    val maxHours = 8f

                    for (i in 0 until barCount) {
                        val hours = hoursList.getOrNull(i) ?: 4f
                        val barHeight = (hours / maxHours) * height
                        val x = spacing + i * (barWidth + spacing)
                        val y = height - barHeight

                        drawRect(
                            color = if (i == 6) AmberSecondary else TealTertiary,
                            topLeft = Offset(x, y),
                            size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                        )
                    }
                }

                // Chart Labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun (Today)")
                    labels.forEach { label ->
                        Text(label, color = SoftMutedText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Custom Canvas Chart 2: Mock Exam Score progress (Line Chart)
        Text("Exam Accuracy Progress Trend", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSlate)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    val width = size.width
                    val height = size.height

                    val points = progressLogs.map { it.accuracy }
                    val maxVal = 1.0f

                    val path = Path()
                    points.forEachIndexed { idx, acc ->
                        val x = (idx.toFloat() / (points.size - 1).coerceAtLeast(1)) * width
                        val y = height - ((acc / maxVal) * height)

                        if (idx == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }

                        drawCircle(color = AmberSecondary, radius = 8f, center = Offset(x, y))
                    }

                    drawPath(
                        path = path,
                        color = AmberSecondary,
                        style = Stroke(width = 4f)
                    )
                }

                Text(
                    text = "Accuracy increased from 65% to 89% in 6 mock iterations.",
                    color = EmeraldSuccess,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

// --- Section 1: User Profile & Mock Histories ---

@Composable
fun ProfileDetailsSection(viewModel: StudyViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val mockHistory by viewModel.mockResults.collectAsState()

    val p = profile ?: UserProfile()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User profile card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSlate)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(IndigoPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    Column {
                        Text(p.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                        Text("Active CHSL Aspirant", color = SoftMutedText, fontSize = 12.sp)
                    }
                }

                HorizontalDivider(color = SoftMutedText.copy(alpha = 0.1f))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Target Score", color = SoftMutedText, fontSize = 11.sp)
                        Text("${p.targetScore} / 200", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Study Hours", color = SoftMutedText, fontSize = 11.sp)
                        Text("${p.studyHours} Hours/day", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Syllabus", color = SoftMutedText, fontSize = 11.sp)
                        Text("Complete", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Mock test history
        Text("Your Mock Test History", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)

        if (mockHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No mock papers solved yet.", color = SoftMutedText, fontSize = 12.sp)
            }
        } else {
            mockHistory.forEach { m ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardSlate)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(m.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Text("${m.score} / ${m.maxScore}", fontWeight = FontWeight.Black, color = AmberSecondary, fontSize = 14.sp)
                        }
                        Text(
                            text = "Accuracy: ${(m.score / m.maxScore * 100).toInt()}% • Completed in ${m.durationSec} seconds",
                            color = SoftMutedText,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

// --- Section 2: Achievements and Medals ---

@Composable
fun AchievementsSection(viewModel: StudyViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val p = profile ?: UserProfile()

    val level = (p.xp / 100) + 1

    val achievements = listOf(
        AchievementBadge("Officer Cadet", "Successfully onboarded and calculated selection chances.", Icons.Default.VerifiedUser, true),
        AchievementBadge("Flame Master", "Maintained a study streak of 3+ continuous days.", Icons.Default.LocalFireDepartment, p.streak >= 3),
        AchievementBadge("AI Voyager", "Interactively solved doubts with the AI assistant.", Icons.Default.AutoAwesome, p.xp > 150),
        AchievementBadge("Exam Gladiator", "Scored high marks on an intensive mock paper.", Icons.Default.MilitaryTech, level >= 3),
        AchievementBadge("Quant Navigator", "Solved mathematical proportion PYQs.", Icons.Default.Functions, level >= 2)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Unlocked Badges & Medals", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)

        achievements.forEach { badge ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (badge.isUnlocked) CardSlate else CardSlate.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (badge.isUnlocked) AmberSecondary.copy(alpha = 0.15f) else SoftMutedText.copy(alpha = 0.1f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = badge.icon,
                            contentDescription = null,
                            tint = if (badge.isUnlocked) AmberSecondary else SoftMutedText,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = badge.title,
                            fontWeight = FontWeight.Bold,
                            color = if (badge.isUnlocked) Color.White else SoftMutedText,
                            fontSize = 14.sp
                        )
                        Text(
                            text = badge.description,
                            color = SoftMutedText,
                            fontSize = 11.sp
                        )
                    }

                    if (badge.isUnlocked) {
                        Icon(Icons.Default.Stars, contentDescription = "Unlocked", tint = AmberSecondary)
                    } else {
                        Icon(Icons.Default.Lock, contentDescription = "Locked", tint = SoftMutedText, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

data class AchievementBadge(val title: String, val description: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val isUnlocked: Boolean)

// --- Section 3: Admin Panel Simulator ---

@Composable
fun AdminPanelSection(viewModel: StudyViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var adminLog by remember { mutableStateOf("No logs yet.") }

    var inputQuestion by remember { mutableStateOf("") }
    var optionA by remember { mutableStateOf("10") }
    var optionB by remember { mutableStateOf("20") }
    var optionC by remember { mutableStateOf("30") }
    var optionD by remember { mutableStateOf("40") }
    var correctOpt by remember { mutableStateOf("A") }

    var currentAffairTitle by remember { mutableStateOf("") }
    var currentAffairContent by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Mentor Admin Control Center", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)

        // Seeded content counts
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSlate)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("System Operations Log", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                Text(adminLog, color = SoftMutedText, fontSize = 11.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
            }
        }

        // Submitter 1: Add Custom Question
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSlate)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Insert Custom PYQ Question", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)

                OutlinedTextField(
                    value = inputQuestion,
                    onValueChange = { inputQuestion = it },
                    placeholder = { Text("Enter question...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = optionA, onValueChange = { optionA = it }, label = { Text("Opt A") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = optionB, onValueChange = { optionB = it }, label = { Text("Opt B") }, modifier = Modifier.weight(1f))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = optionC, onValueChange = { optionC = it }, label = { Text("Opt C") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = optionD, onValueChange = { optionD = it }, label = { Text("Opt D") }, modifier = Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Correct Option: $correctOpt", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("A", "B", "C", "D").forEach { opt ->
                            FilterChip(
                                selected = correctOpt == opt,
                                onClick = { correctOpt = opt },
                                label = { Text(opt) }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        if (inputQuestion.isNotBlank()) {
                            // Seed simulation direct insert (handled in VM ideally, admin direct simulation)
                            adminLog = "Question added to math local list successfully!"
                            inputQuestion = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary, contentColor = MidnightBg),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Insert Question", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Submitter 2: Add Current Affair announcement
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSlate)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Post System-wide Current Affair Brief", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)

                OutlinedTextField(
                    value = currentAffairTitle,
                    onValueChange = { currentAffairTitle = it },
                    placeholder = { Text("Enter Title...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )

                OutlinedTextField(
                    value = currentAffairContent,
                    onValueChange = { currentAffairContent = it },
                    placeholder = { Text("Enter content summary...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )

                Button(
                    onClick = {
                        if (currentAffairTitle.isNotBlank()) {
                            adminLog = "Affair announcement broadcasted to local client bulletin."
                            currentAffairTitle = ""
                            currentAffairContent = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary, contentColor = MidnightBg),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Broadcast News", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// --- Section 4: Premium Features Showcase ---

@Composable
fun PremiumFeaturesSection() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.WorkspacePremium,
            contentDescription = null,
            tint = AmberSecondary,
            modifier = Modifier.size(72.dp)
        )

        Text(
            text = "SSC CHSL Officer's Premium Club",
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Secure your government selection by unlocking our master coaching suite.",
            color = SoftMutedText,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        val premiumSuites = listOf(
            PremiumHighlight("Unlimited AI Doubt Solving", "Solve 100+ doubts daily step-by-step with real-time math diagrams.", Icons.Default.AutoAwesome),
            PremiumHighlight("Intensive Mock Test Analyzers", "Review guess-work mistakes, accuracy slips, and chapters needing revision.", Icons.Default.Analytics),
            PremiumHighlight("Interactive Voice Assistant Coach", "Talk directly with your personal mentor. Practice spoken English rules.", Icons.Default.Mic),
            PremiumHighlight("Syllabus Notes & PDF Generator", "Convert study plans and shortcut formulas into offline printable PDF booklets.", Icons.Default.PictureAsPdf),
            PremiumHighlight("Instant Cloud Backup Sync", "Keep study statistics, streaks, and mock histories synced on multiple tablets/devices.", Icons.Default.CloudSync)
        )

        premiumSuites.forEach { suite ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardSlate)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AmberSecondary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(suite.icon, contentDescription = null, tint = AmberSecondary)
                    }
                    Column {
                        Text(suite.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text(suite.desc, color = SoftMutedText, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { /* Simulated premium activation */ },
            colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary, contentColor = MidnightBg),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("UNLOCK CHSL OFFICER CLUB (FREE TRIAL)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

data class PremiumHighlight(val title: String, val desc: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
