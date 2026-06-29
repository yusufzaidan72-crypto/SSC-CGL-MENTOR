package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.StudyViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(1) }
    var name by remember { mutableStateOf("CHSL Warrior") }
    var studyHours by remember { mutableStateOf(5f) }
    var targetYear by remember { mutableStateOf(2026) }
    var targetScore by remember { mutableStateOf(155f) }
    var weakSubject by remember { mutableStateOf("General Awareness") }
    var strongSubject by remember { mutableStateOf("Mathematics") }
    var isBeginner by remember { mutableStateOf(true) }

    val subjects = listOf("Mathematics", "Reasoning", "English Language", "General Awareness")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(MidnightBg, CardSlate)
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = AmberSecondary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SSC CHSL AI MENTOR",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AmberSecondary,
                        letterSpacing = 2.sp
                    )
                )
            }

            // Card Container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, IndigoPrimary.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.9f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Step Indicator
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..3) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        if (i <= step) AmberSecondary else SoftMutedText.copy(alpha = 0.3f)
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Step Contents
                    when (step) {
                        1 -> {
                            Text(
                                text = "Welcome, Future Officer!",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                ),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Let's personalize your AI learning roadmap. What should your mentor call you?",
                                style = MaterialTheme.typography.bodyMedium.copy(color = SoftMutedText),
                                textAlign = TextAlign.Center
                            )

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Your Name") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = AmberSecondary) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AmberSecondary,
                                    unfocusedBorderColor = IndigoPrimary,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("name_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Are you a beginner or experienced?",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { isBeginner = true }
                                        .border(
                                            width = 2.dp,
                                            color = if (isBeginner) AmberSecondary else Color.Transparent,
                                            shape = RoundedCornerShape(16.dp)
                                        ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isBeginner) IndigoPrimary.copy(alpha = 0.4f) else CardSlate
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Default.School, contentDescription = null, tint = if (isBeginner) AmberSecondary else SoftMutedText)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Beginner", fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("New to CHSL", fontSize = 10.sp, color = SoftMutedText)
                                    }
                                }

                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { isBeginner = false }
                                        .border(
                                            width = 2.dp,
                                            color = if (!isBeginner) AmberSecondary else Color.Transparent,
                                            shape = RoundedCornerShape(16.dp)
                                        ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (!isBeginner) IndigoPrimary.copy(alpha = 0.4f) else CardSlate
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = if (!isBeginner) AmberSecondary else SoftMutedText)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Experienced", fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("Know the syllabus", fontSize = 10.sp, color = SoftMutedText)
                                    }
                                }
                            }
                        }

                        2 -> {
                            Text(
                                text = "Define Your Goals",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "We'll align your targets to calculate your selection probability.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = SoftMutedText),
                                textAlign = TextAlign.Center
                            )

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Target Exam Year: $targetYear",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(2025, 2026, 2027).forEach { year ->
                                        FilterChip(
                                            selected = targetYear == year,
                                            onClick = { targetYear = year },
                                            label = { Text(year.toString()) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = AmberSecondary,
                                                selectedLabelColor = MidnightBg
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Target Score", color = Color.White, fontWeight = FontWeight.Bold)
                                    Text("${targetScore.toInt()} / 200", color = AmberSecondary, fontWeight = FontWeight.Bold)
                                }
                                Slider(
                                    value = targetScore,
                                    onValueChange = { targetScore = it },
                                    valueRange = 120f..190f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = AmberSecondary,
                                        activeTrackColor = AmberSecondary,
                                        inactiveTrackColor = SoftMutedText.copy(alpha = 0.3f)
                                    )
                                )
                                Text(
                                    text = "Safe selection score is typically 150+ in Tier 1.",
                                    fontSize = 11.sp,
                                    color = SoftMutedText
                                )
                            }

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Daily Study Committment", color = Color.White, fontWeight = FontWeight.Bold)
                                    Text("${studyHours.toInt()} Hours", color = AmberSecondary, fontWeight = FontWeight.Bold)
                                }
                                Slider(
                                    value = studyHours,
                                    onValueChange = { studyHours = it },
                                    valueRange = 2f..12f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = AmberSecondary,
                                        activeTrackColor = AmberSecondary,
                                        inactiveTrackColor = SoftMutedText.copy(alpha = 0.3f)
                                    )
                                )
                            }
                        }

                        3 -> {
                            Text(
                                text = "Syllabus Strengths",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Your AI mentor will plan high-focus study slots around your weakest areas.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = SoftMutedText),
                                textAlign = TextAlign.Center
                            )

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Select Your Weakest Subject:", color = Color.White, fontWeight = FontWeight.Bold)
                                subjects.forEach { sub ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { weakSubject = sub }
                                            .border(
                                                width = 1.dp,
                                                color = if (weakSubject == sub) CrimsonError else Color.Transparent,
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (weakSubject == sub) CrimsonError.copy(alpha = 0.15f) else CardSlate
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = weakSubject == sub,
                                                onClick = { weakSubject = sub },
                                                colors = RadioButtonDefaults.colors(selectedColor = CrimsonError)
                                            )
                                            Text(sub, color = Color.White, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Select Your Strongest Subject:", color = Color.White, fontWeight = FontWeight.Bold)
                                subjects.forEach { sub ->
                                    if (sub != weakSubject) {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { strongSubject = sub }
                                                .border(
                                                    width = 1.dp,
                                                    color = if (strongSubject == sub) EmeraldSuccess else Color.Transparent,
                                                    shape = RoundedCornerShape(12.dp)
                                                ),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (strongSubject == sub) EmeraldSuccess.copy(alpha = 0.15f) else CardSlate
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = strongSubject == sub,
                                                    onClick = { strongSubject = sub },
                                                    colors = RadioButtonDefaults.colors(selectedColor = EmeraldSuccess)
                                                )
                                                Text(sub, color = Color.White, fontWeight = FontWeight.SemiBold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (step > 1) {
                            TextButton(
                                onClick = { step-- },
                                colors = ButtonDefaults.textButtonColors(contentColor = SoftWhiteText)
                            ) {
                                Text("Back")
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        Button(
                            onClick = {
                                if (step < 3) {
                                    step++
                                } else {
                                    viewModel.completeOnboarding(
                                        name = name.ifBlank { "Aspirant" },
                                        studyHours = studyHours.toInt(),
                                        targetYear = targetYear,
                                        targetScore = targetScore.toInt(),
                                        weakSubject = weakSubject,
                                        strongSubject = strongSubject,
                                        isBeginner = isBeginner
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary, contentColor = MidnightBg),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("next_button")
                        ) {
                            Text(
                                text = if (step == 3) "Generate AI Study Plan" else "Continue",
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = if (step == 3) Icons.Default.AutoAwesome else Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Trust badge
            Text(
                text = "🛡️ 24x7 Private AI Mentor • Ad-Free Ecosystem",
                color = SoftMutedText,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
