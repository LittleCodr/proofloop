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

                // Request recap card
                val req = uiState.request
                if (req != null) {
                    NeoCard(
                        backgroundColor = ProofWhite,
                        shadowOffset = 2.dp
                    ) {
                        Text(
                            text = "ACTIVE MISSION",
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
                            NeoTag(text = "${req.durationMinutes}m duration", backgroundColor = ProofSurfaceMuted)
                            if (req.blueprintId.isNotBlank()) {
                                NeoTag(text = "Blueprint Active", backgroundColor = ProofSignalMint)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Success Message
                AnimatedVisibility(
                    visible = uiState.successMessage != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        NeoCard(
                            backgroundColor = ProofSignalMint,
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
                                text = "Go to Student Dashboard",
                                onClick = onViewHome,
                                backgroundColor = ProofInk,
                                contentColor = ProofWhite
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                Text(
                    text = "PROOF LOOP INTELLIGENCE",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    ),
                    color = ProofInk
                )
                Text(
                    text = "Every recommendation explains why a mentor fits using verifiable profile facts, without invented claims.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ProofTextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

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
                                        if (alumni.isVerifiedAlumni || alumni.isVerifiedProfile) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.Verified,
                                                contentDescription = "Verified Partner",
                                                tint = ProofCobalt,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = when (alumni.role) {
                                                "FOUNDER" -> ProofAcidLime
                                                "FACULTY" -> ProofLavender
                                                "RECRUITER" -> ProofCoral
                                                else -> ProofSignalMint
                                            },
                                            border = androidx.compose.foundation.BorderStroke(1.dp, ProofBorder)
                                        ) {
                                            Text(
                                                text = alumni.role,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 9.sp
                                                ),
                                                color = ProofInk,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = alumni.subtitle,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = ProofInk
                                    )
                                    Text(
                                        text = alumni.college.ifBlank { "Professional Community" },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ProofTextMuted
                                    )
                                }

                                // Fit Level Badge
                                val (fitBg, fitText) = when (expMatch.fitLevel) {
                                    MatchFitLevel.EXCELLENT_FIT -> ProofSignalMint to expMatch.fitLevel.label
                                    MatchFitLevel.STRONG_FIT -> ProofElectricLilac to expMatch.fitLevel.label
                                    MatchFitLevel.GOOD_FIT -> ProofLavender to expMatch.fitLevel.label
                                    MatchFitLevel.EXPLORATORY_FIT -> ProofSurfaceMuted to expMatch.fitLevel.label
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = fitBg,
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ProofBorder)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = fitText.uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 10.sp
                                            ),
                                            color = ProofInk
                                        )
                                        Text(
                                            text = "${expMatch.totalScore} pts",
                                            style = MaterialTheme.typography.bodySmall.copy(
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

                            // Truthful Evidence Box
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ProofPaper,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ProofBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "MATCH EVIDENCE:",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Black
                                        ),
                                        color = ProofInk
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    expMatch.evidenceBullets.forEach { bullet ->
                                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                            Text(
                                                text = "✓",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontWeight = FontWeight.Black,
                                                    color = ProofCobalt
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = bullet,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = ProofInk
                                            )
                                        }
                                    }
                                }
                            }

                            // Missing Info Notice (if any)
                            if (expMatch.missingInfoNotice != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(ProofSurfaceMuted, RoundedCornerShape(4.dp))
                                        .border(1.dp, ProofBorder, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = ProofTextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = expMatch.missingInfoNotice,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ProofTextMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Mentor Capacity & Availability
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Active load: ${expMatch.activeConnectionsCount}/${alumni.maxActiveRequests} missions",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                    color = ProofTextMuted
                                )

                                if (alumni.availableSlots.isNotEmpty()) {
                                    Text(
                                        text = alumni.availableSlots.first(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ProofTextMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // CTA: Send Request
                            if (isAlreadyRequested) {
                                NeoButton(
                                    text = "Request Pending ✓",
                                    onClick = {},
                                    backgroundColor = ProofSurfaceMuted,
                                    contentColor = ProofInk,
                                    enabled = false
                                )
                            } else {
                                NeoButton(
                                    text = "Send Mission Request",
                                    onClick = {
                                        viewModel.sendConnectionRequest(alumni.uid, alumni.displayName)
                                    },
                                    backgroundColor = ProofElectricLilac,
                                    contentColor = ProofInk,
                                    icon = Icons.Default.Send
                                )
                            }
                        }

                        // Single Monetization Placement in Feed: Rendered after top match
                        if (index == 0) {
                            NeoBannerAd(
                                adTitle = "FEATURED HIRING PARTNER",
                                modifier = Modifier.padding(bottom = 14.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
