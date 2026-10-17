package com.proofloop.feature.requests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proofloop.core.ui.components.*
import com.proofloop.core.ui.theme.*

@Composable
fun MatchResultsScreen(
    requestId: String,
    onNavigateBack: () -> Unit,
    onViewHome: () -> Unit,
    viewModel: MatchResultsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(requestId) {
        viewModel.loadMatches(requestId)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ProofPaper
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ProofPaper,
                border = androidx.compose.foundation.BorderStroke(1.dp, ProofBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ProofInk
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MATCHED ALUMNI",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        ),
                        color = ProofInk
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Workflow hero (Step 2: MATCH)
                WorkflowHero(currentStep = 2)

                Spacer(modifier = Modifier.height(14.dp))

                // Request recap card
                val req = uiState.request
                if (req != null) {
                    NeoCard(
                        backgroundColor = ProofWhite,
                        shadowOffset = 2.dp
                    ) {
                        Text(
                            text = "YOUR REQUEST",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                            color = ProofTextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = req.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = ProofInk
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            NeoTag(text = req.category, backgroundColor = ProofLavender)
                            NeoTag(text = "${req.durationMinutes}m", backgroundColor = ProofSurfaceMuted)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Success Message
                if (uiState.successMessage != null) {
                    NeoCard(
                        backgroundColor = ProofAcidLime,
                        shadowOffset = 2.dp
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = ProofInk)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.successMessage ?: "",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = ProofInk
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        NeoButton(
                            text = "Back to Student Dashboard",
                            onClick = onViewHome,
                            backgroundColor = ProofInk,
                            contentColor = ProofWhite
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = "RANKED ALUMNI & EXPLANATIONS",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    ),
                    color = ProofInk
                )
                Text(
                    text = "Scores are calculated using the 5-factor deterministic formula. Every reason is truthful.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ProofTextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ProofCobalt)
                    }
                } else if (uiState.matches.isEmpty()) {
                    EmptyStateCard(
                        title = "No Strong Matches Yet",
                        message = "No opted-in alumni matched all required criteria. Try adding fewer required skills or choosing another help category.",
                        actionText = "Edit Request",
                        onAction = onNavigateBack
                    )
                } else {
                    uiState.matches.forEach { match ->
                        val alumni = match.alumni
                        val isAlreadyRequested = uiState.requestedAlumniIds.contains(alumni.uid)

                        NeoCard(
                            backgroundColor = ProofWhite,
                            shadowOffset = 4.dp,
                            modifier = Modifier.padding(bottom = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = alumni.displayName,
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                            color = ProofInk
                                        )
                                        if (alumni.isDemonstrationProfile) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            NeoTag(
                                                text = "DEMO PROFILE",
                                                backgroundColor = ProofLavender
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${alumni.jobTitle} • ${alumni.company}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = ProofCobalt
                                    )
                                    Text(
                                        text = "${alumni.college} ('${alumni.graduationYear})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ProofTextMuted
                                    )
                                }

                                // Match Score Badge (Neo-Brutalist Pill)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (match.totalScore >= 70) ProofAcidLime else ProofLavender,
                                    border = androidx.compose.foundation.BorderStroke(2.dp, ProofBorder)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "${match.totalScore}%",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Black
                                            ),
                                            color = ProofInk
                                        )
                                        Text(
                                            text = "MATCH",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            ),
                                            color = ProofInk
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Truthful Match Reasons Box
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ProofPaper,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ProofBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "WHY THIS PERSON MATCHES:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                        color = ProofInk
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    match.reasons.forEach { reason ->
                                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                            Text(
                                                text = "•",
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Black),
                                                color = ProofCobalt
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = reason,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = ProofInk
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Availability Slots
                            if (alumni.availableSlots.isNotEmpty()) {
                                Text(
                                    text = "Available: ${alumni.availableSlots.joinToString(" • ")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ProofTextMuted
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            // CTA: Send Request
                            if (isAlreadyRequested) {
                                NeoButton(
                                    text = "Request Sent ✓",
                                    onClick = {},
                                    backgroundColor = ProofSurfaceMuted,
                                    contentColor = ProofInk,
                                    enabled = false
                                )
                            } else {
                                NeoButton(
                                    text = "Send Connection Request",
                                    onClick = {
                                        viewModel.sendConnectionRequest(alumni.uid, alumni.displayName)
                                    },
                                    backgroundColor = ProofCobalt,
                                    contentColor = ProofWhite,
                                    icon = Icons.Default.Send
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
