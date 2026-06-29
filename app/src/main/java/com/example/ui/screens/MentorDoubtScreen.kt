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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Doubt
import com.example.ui.StudyViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentorDoubtScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    var doubtText by remember { mutableStateOf("") }
    val solvedDoubts by viewModel.doubts.collectAsState()
    val isSolving by viewModel.isSolvingDoubt.collectAsState()
    val lastCreatedDoubt by viewModel.lastCreatedDoubt.collectAsState()

    var showUploadSimulationDialog by remember { mutableStateOf(false) }
    var uploadedFileType by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Chat Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AmberSecondary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = AmberSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column {
                Text(
                    text = "AI 24x7 Mentor Solver",
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = "Active • English, Maths, Reasoning & GA",
                    color = EmeraldSuccess,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Active solver answer panel if just completed
        if (lastCreatedDoubt != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AmberSecondary.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSlate)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Doubt Solved!", fontWeight = FontWeight.Bold, color = AmberSecondary, fontSize = 13.sp)
                        }
                        IconButton(onClick = { viewModel.clearLastDoubt() }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = SoftMutedText)
                        }
                    }

                    Text(
                        text = "Your Question:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftMutedText
                    )
                    Text(
                        text = lastCreatedDoubt!!.questionText,
                        fontSize = 13.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )

                    HorizontalDivider(color = SoftMutedText.copy(alpha = 0.15f))

                    Text(
                        text = "AI Step-by-Step Explanation:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealTertiary
                    )
                    Text(
                        text = lastCreatedDoubt!!.answerText ?: "",
                        fontSize = 13.sp,
                        color = SoftWhiteText,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Doubt Input section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSlate)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Submit a New Doubt",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )

                OutlinedTextField(
                    value = doubtText,
                    onValueChange = { doubtText = it },
                    placeholder = { Text("Ask anything or type a past question...") },
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("doubt_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberSecondary,
                        unfocusedBorderColor = MidnightBg,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Attachments line
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Simulated Image attachment
                        IconButton(
                            onClick = {
                                uploadedFileType = "Image"
                                showUploadSimulationDialog = true
                            },
                            modifier = Modifier.background(MidnightBg, CircleShape)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = "Attach Image", tint = AmberSecondary)
                        }

                        // Simulated PDF attachment
                        IconButton(
                            onClick = {
                                uploadedFileType = "PDF File"
                                showUploadSimulationDialog = true
                            },
                            modifier = Modifier.background(MidnightBg, CircleShape)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = "Attach PDF", tint = TealTertiary)
                        }

                        // Simulated Voice mic
                        IconButton(
                            onClick = {
                                uploadedFileType = "Voice Note"
                                showUploadSimulationDialog = true
                            },
                            modifier = Modifier.background(MidnightBg, CircleShape)
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = "Voice Assistant", tint = BronzeCaution)
                        }
                    }

                    Button(
                        onClick = {
                            if (doubtText.isNotBlank()) {
                                viewModel.askDoubtStepByStep(doubtText)
                                doubtText = ""
                            }
                        },
                        enabled = doubtText.isNotBlank() && !isSolving,
                        colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary, contentColor = MidnightBg),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("solve_doubt_button")
                    ) {
                        if (isSolving) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = MidnightBg)
                        } else {
                            Text("Solve Now", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        // Previous doubts solved archive list
        Text(
            text = "Doubt History & Past Solutions",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        if (solvedDoubts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.HistoryEdu, contentDescription = null, tint = SoftMutedText, modifier = Modifier.size(48.dp))
                    Text("Your solved doubt history will appear here.", color = SoftMutedText, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(solvedDoubts) { doubt ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardSlate)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Q: ${doubt.questionText}",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                            HorizontalDivider(color = SoftMutedText.copy(alpha = 0.1f))
                            Text(
                                text = doubt.answerText ?: "",
                                color = SoftWhiteText,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Attachment Simulator Dialog
    if (showUploadSimulationDialog) {
        AlertDialog(
            onDismissRequest = { showUploadSimulationDialog = false },
            title = { Text("$uploadedFileType Loaded!") },
            text = {
                Text(
                    text = when (uploadedFileType) {
                        "Image" -> "We have successfully simulated scanning your textbook image. The math question has been extracted into your text box. Tap 'Solve Now' to solve."
                        "PDF File" -> "The official previous year question paper PDF has been synchronized. Selected doubt extracted to the input box."
                        else -> "Your voice note 'How do I solve Compound Interest differences of 3 years' was recorded and translated."
                    },
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showUploadSimulationDialog = false
                    doubtText = when (uploadedFileType) {
                        "Image" -> "A train running at 54 km/h crosses a pole in 15 seconds. Find its length in meters."
                        "PDF File" -> "Solve SSC CHSL 2024 Math Paper Question: In an election between two candidates, one got 55% of the total valid votes. 20% votes were invalid. If total votes = 7500, find valid votes for the other candidate."
                        else -> "Explain the 3-year difference formula between Compound Interest and Simple Interest."
                    }
                }) {
                    Text("Insert Question Text", color = AmberSecondary)
                }
            },
            containerColor = CardSlate
        )
    }
}
