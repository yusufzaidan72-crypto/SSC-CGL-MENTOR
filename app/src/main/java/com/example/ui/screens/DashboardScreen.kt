package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StudyTask
import com.example.data.UserProfile
import com.example.ui.StudyViewModel
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: StudyViewModel,
    onNavigateToTab: (Int) -> Unit, // Callback to switch tabs
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val tasks by viewModel.todayTasks.collectAsState()
    val isGenerating by viewModel.isGeneratingPlan.collectAsState()

    val currentProfile = profile ?: UserProfile()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Welcome Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Jai Hind, ${currentProfile.name}!",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Let's crack SSC CHSL ${currentProfile.targetYear} 🎯",
                    style = MaterialTheme.typography.bodyMedium.copy(color = SoftMutedText)
                )
            }

            // Streak Flame
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(AmberSecondary.copy(alpha = 0.15f))
                    .border(1.dp, AmberSecondary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = AmberSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${currentProfile.streak} Days",
                    color = AmberSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // Selection Probability Gauge & Core Analytics Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, IndigoPrimary.copy(alpha = 0.4f), RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardSlate)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Interactive Selection Chance Gauge Chart
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    val animatedProbability by animateFloatAsState(
                        targetValue = currentProfile.selectionProbability,
                        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
                        label = "gauge"
                    )

                    Canvas(modifier = Modifier.size(100.dp)) {
                        val strokeWidth = 10.dp.toPx()
                        val diameter = size.minDimension - strokeWidth
                        val rect = Size(diameter, diameter)
                        val offset = strokeWidth / 2f

                        // Background track
                        drawArc(
                            color = SoftMutedText.copy(alpha = 0.15f),
                            startAngle = 135f,
                            sweepAngle = 270f,
                            useCenter = false,
                            topLeft = Offset(offset, offset),
                            size = rect,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Progress track
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(CrimsonError, AmberSecondary, EmeraldSuccess)
                            ),
                            startAngle = 135f,
                            sweepAngle = 270f * animatedProbability,
                            useCenter = false,
                            topLeft = Offset(offset, offset),
                            size = rect,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(currentProfile.selectionProbability * 100).toInt()}%",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Selection Chance",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = SoftMutedText
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Stats Metrics
                Column(
                    modifier = Modifier.weight(1.2f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val (level, progress) = viewModel.getXpProgress(currentProfile.xp)
                    
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Level $level", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${currentProfile.xp % 100}/100 XP", color = TealTertiary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            color = TealTertiary,
                            trackColor = SoftMutedText.copy(alpha = 0.1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Target Score", color = SoftMutedText, fontSize = 10.sp)
                            Text("${currentProfile.targetScore}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Weak Spot", color = SoftMutedText, fontSize = 10.sp)
                            Text(currentProfile.weakSubjects.split(" ").first(), color = CrimsonError, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }

        // Action Options row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { onNavigateToTab(1) }, // Doubt Solver
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.HelpCenter, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Doubt Solver", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = { onNavigateToTab(3) }, // Mock Tests
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberSecondary),
                border = BorderStroke(1.dp, AmberSecondary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Take Mock", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Personal AI Daily study planner header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Your AI Study Planner",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Customized schedule for today",
                    style = MaterialTheme.typography.bodySmall.copy(color = SoftMutedText)
                )
            }

            IconButton(
                onClick = { viewModel.regenerateStudyPlan() },
                enabled = !isGenerating,
                modifier = Modifier.background(CardSlate, CircleShape)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = AmberSecondary)
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = "Regenerate Plan", tint = AmberSecondary)
                }
            }
        }

        // Study tasks list
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardSlate),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CircularProgressIndicator(color = AmberSecondary)
                    Text("AI is constructing your timetable...", color = SoftMutedText, fontSize = 12.sp)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                tasks.forEach { task ->
                    StudyTaskCard(
                        task = task,
                        onCompleteToggle = { isCompleted ->
                            viewModel.completeTask(task.id, isCompleted)
                        }
                    )
                }
            }
        }

        // Spaced Repetition Reminders Alert Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TealTertiary.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSlate)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Update,
                    contentDescription = null,
                    tint = TealTertiary,
                    modifier = Modifier.size(28.dp)
                )
                Column {
                    Text(
                        text = "Spaced Repetition Active",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Revise 'Constitutional Articles' & ' कंपाउंड इंटरेस्ट ' today to boost long-term memory retention by 4x.",
                        color = SoftMutedText,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Daily Motivation Quote Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FormatQuote,
                    contentDescription = null,
                    tint = AmberSecondary,
                    modifier = Modifier
                        .size(36.dp)
                        .rotate(180f)
                )
                Text(
                    text = "\"Success is not final, failure is not fatal: it is the courage to continue that counts. Your selection is just a daily ritual away!\"",
                    color = SoftWhiteText,
                    fontSize = 14.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "— AI Mentor Daily Catalyst",
                    color = AmberSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StudyTaskCard(
    task: StudyTask,
    onCompleteToggle: (Boolean) -> Unit
) {
    val slotIcon = when (task.timeSlot.split(" ").first()) {
        "Morning" -> Icons.Default.LightMode
        "Afternoon" -> Icons.Default.WbCloudy
        "Evening" -> Icons.Default.FilterDrama
        else -> Icons.Default.DarkMode
    }

    val iconColor = if (task.isCompleted) EmeraldSuccess else when (task.timeSlot.split(" ").first()) {
        "Morning" -> AmberSecondary
        "Afternoon" -> TealTertiary
        "Evening" -> BronzeCaution
        else -> IndigoPrimary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (task.isCompleted) EmeraldSuccess.copy(alpha = 0.3f) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) CardSlate.copy(alpha = 0.6f) else CardSlate
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = slotIcon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.subject,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = iconColor,
                        modifier = Modifier.background(iconColor.copy(alpha = 0.08f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = task.timeSlot,
                        fontSize = 10.sp,
                        color = SoftMutedText
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.topic,
                    fontWeight = FontWeight.Bold,
                    color = if (task.isCompleted) SoftMutedText else Color.White,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = task.description,
                    color = SoftMutedText,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onCompleteToggle(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = EmeraldSuccess,
                    uncheckedColor = SoftMutedText
                ),
                modifier = Modifier.testTag("task_checkbox_${task.id}")
            )
        }
    }
}
