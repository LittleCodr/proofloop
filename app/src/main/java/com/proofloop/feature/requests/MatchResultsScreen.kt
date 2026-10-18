package com.proofloop.feature.requests

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Verified
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
import com.proofloop.data.model.MatchFitLevel

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
                        text = "EXPLAINABLE MATCHES",
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

                // Compact Mission Recap Strip
                val req = uiState.request
                if (req != null) {
                    NeoCard(
                        backgroundColor = ProofWhite,
                        shadowOffset = 2.dp,
                        cornerRadius = 6.dp,
                        contentPadding = 7.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "MISSION:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 9.sp
                                    ),
                                    color = ProofCharcoalMuted
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = req.title,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = ProofObsidianInk,
                                    maxLines = 1
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                NeoTag(text = "${req.durationMinutes}m", backgroundColor = ProofVoltCitron)
                                NeoTag(text = req.category, backgroundColor = ProofDigitalLilac)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Success Message
                AnimatedVisibility(
                    visible = uiState.successMessage != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        NeoCard(
                            backgroundColor = ProofSignalEmerald,
                            shadowOffset = 2.dp,
                            contentPadding = 8.dp
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = ProofObsidianInk, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = uiState.successMessage ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = ProofObsidianInk
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            NeoButton(
                                text = "Go to Student Dashboard →",
                                onClick = onViewHome,
                                backgroundColor = ProofObsidianInk,
                                contentColor = ProofVoltCitron
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "• PROOFLOOP INTELLIGENCE MATCHES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = ProofObsidianInk
                    )
                    Text(
                        text = "${uiState.explainableMatches.size} AVAILABLE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        color = ProofKleinBlue
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (uiState.isLoading) {
                    NeoLoadingIndicator(message = "Evaluating skill overlap, availability & capacity...")
                } else if (uiState.explainableMatches.isEmpty() && uiState.matches.isEmpty()) {
                    EmptyStateCard(
                        title = "No Matching Mentors Available",
                        message = "No opted-in alumni currently match all criteria within capacity limits. Try broadening your requested skills or adjusting your preferred availability.",
                        actionText = "Revise Request",
                        onAction = onNavigateBack
                    )
                } else {
                    uiState.explainableMatches.forEachIndexed { index, expMatch ->
                        val alumni = expMatch.alumni
                        val isAlreadyRequested = uiState.requestedAlumniIds.contains(alumni.uid)

                        NeoCard(
                            backgroundColor = ProofWhite,
                            shadowOffset = 2.dp,
                            cornerRadius = 8.dp,
                            contentPadding = 9.dp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = alumni.displayName,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                                            color = ProofObsidianInk
                                        )
                                        if (alumni.isVerifiedAlumni || alumni.isVerifiedProfile) {
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Icon(
                                                imageVector = Icons.Default.Verified,
                                                contentDescription = "Verified Partner",
                                                tint = ProofKleinBlue,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        NeoTag(
                                            text = alumni.role,
                                            backgroundColor = when (alumni.role) {
                                                "FOUNDER" -> ProofVoltCitron
                                                "FACULTY" -> ProofDigitalLilac
                                                "RECRUITER" -> ProofSolarTangerine
                                                else -> ProofSignalEmerald
                                            }
                                        )
                                    }
                                    Text(
                                        text = "${alumni.subtitle} • ${alumni.college.ifBlank { "Campus Network" }}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = ProofCharcoalMuted,
                                        maxLines = 1
                                    )
                                }

                                // Fit Level Badge
                                val (fitBg, fitText) = when (expMatch.fitLevel) {
                                    MatchFitLevel.EXCELLENT_FIT -> ProofVoltCitron to expMatch.fitLevel.label
                                    MatchFitLevel.STRONG_FIT -> ProofSignalEmerald to expMatch.fitLevel.label
                                    MatchFitLevel.GOOD_FIT -> ProofDigitalLilac to expMatch.fitLevel.label
                                    MatchFitLevel.EXPLORATORY_FIT -> ProofPaperLight to expMatch.fitLevel.label
                                }

                                Surface(
                                    shape = RoundedCornerShape(5.dp),
                                    color = fitBg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ProofBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = fitText.uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 8.5.sp
                                            ),
                                            color = ProofObsidianInk
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "${expMatch.totalScore}p",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 8.5.sp
                                            ),
                                            color = ProofObsidianInk
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Truthful Evidence Box (compact)
                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = ProofPaperLight,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ProofBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(6.dp)) {
                                    expMatch.evidenceBullets.take(2).forEach { bullet ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 1.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "✓",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontWeight = FontWeight.Black,
                                                    color = ProofKleinBlue,
                                                    fontSize = 11.sp
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = bullet,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = ProofObsidianInk,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Load & CTA Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Load: ${expMatch.activeConnectionsCount}/${alumni.maxActiveRequests} • ${alumni.availableSlots.firstOrNull() ?: "Flexible"}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
                                    color = ProofCharcoalMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // CTA: Send Request
                            if (isAlreadyRequested) {
                                NeoButton(
                                    text = "Request Pending ✓",
                                    onClick = {},
                                    backgroundColor = ProofPaperLight,
                                    contentColor = ProofCharcoalMuted,
                                    enabled = false
                                )
                            } else {
                                NeoVoltButton(
                                    text = "Send Mission Request →",
                                    onClick = {
                                        viewModel.sendConnectionRequest(alumni.uid, alumni.displayName)
                                    },
                                    icon = Icons.Default.Send
                                )
                            }
                        }

                        // Single Monetization Placement in Feed: Rendered after top match
                        if (index == 0) {
                            NeoBannerAd(
                                adTitle = "FEATURED HIRING PARTNER",
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
