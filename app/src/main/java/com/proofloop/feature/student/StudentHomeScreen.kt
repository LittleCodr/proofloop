package com.proofloop.feature.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.animation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proofloop.core.ui.components.*
import com.proofloop.core.ui.theme.*

@Composable
fun StudentHomeScreen(
    onRequestHelpClick: () -> Unit,
    onViewMatchesClick: (requestId: String) -> Unit,
    onOpenWorkspaceClick: (connectionId: String) -> Unit,
    onSignOutClick: () -> Unit,
    viewModel: StudentHomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Determine current workflow stage
    val currentWorkflowStep = when {
        uiState.completedOutcomes.isNotEmpty() -> 5
        uiState.acceptedConnections.isNotEmpty() -> 4
        uiState.pendingConnections.isNotEmpty() -> 3
        uiState.activeRequests.isNotEmpty() -> 2
        else -> 1
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ProofPaper
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // App Bar
            NeoTopAppBar(
                title = "PROOFLOOP",
                subtitle = "Student Dashboard",
                roleTag = "STUDENT",
                onSignOut = {
                    viewModel.logout()
                    onSignOutClick()
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Welcome Card
                NeoCard(
                    backgroundColor = ProofWhite,
                    shadowOffset = 3.dp
                ) {
                    Text(
                        text = "WELCOME, ${uiState.currentUser?.name?.uppercase() ?: "STUDENT"}",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        ),
                        color = ProofInk
                    )
                    Text(
                        text = "Ask for specific, time-bounded guidance from opted-in alumni.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ProofTextMuted
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    NeoButton(
                        text = "Start Loop Mission",
                        onClick = onRequestHelpClick,
                        backgroundColor = ProofCobalt,
                        icon = Icons.Default.Add
                    )
                }

                AnimatedVisibility(
                    visible = uiState.isLoading,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(14.dp))
                        NeoLoadingIndicator("Updating student dashboard...")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Workflow Progress Card
                WorkflowHero(currentStep = currentWorkflowStep)

                Spacer(modifier = Modifier.height(20.dp))

                // SECTION 1: ACCEPTED CONNECTIONS & AGREED ACTIONS
                Text(
                    text = "ACCEPTED CONNECTIONS & NEXT ACTIONS",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    ),
                    color = ProofInk
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (uiState.acceptedConnections.isEmpty()) {
                    EmptyStateCard(
                        title = "No Active Sessions",
                        message = "When an alumnus accepts your request, your session workspace and agreed next action will appear here."
                    )
                } else {
                    uiState.acceptedConnections.forEach { connection ->
                        NeoCard(
                            backgroundColor = ProofWhite,
                            shadowOffset = 3.dp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = connection.alumniName,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = ProofInk
                                )
                                StatusBadge(status = connection.status)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Request: ${connection.requestTitle}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = ProofCobalt
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Agreed next action highlight
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = ProofLavender,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "AGREED NEXT ACTION:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                        color = ProofInk
                                    )
                                    Text(
                                        text = connection.agreedNextAction.ifBlank { "Tap to define and agree on a practical next step." },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ProofInk
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            NeoButton(
                                text = "Open Workspace",
                                onClick = { onOpenWorkspaceClick(connection.id) },
                                backgroundColor = ProofAcidLime,
                                contentColor = ProofInk,
                                icon = Icons.Default.ArrowForward
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // SECTION 2: PENDING CONNECTION REQUESTS
                Text(
                    text = "PENDING CONNECTION REQUESTS",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    ),
                    color = ProofInk
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (uiState.pendingConnections.isEmpty()) {
                    EmptyStateCard(
                        title = "No Pending Responses",
                        message = "You have no outstanding requests awaiting alumni response."
                    )
                } else {
                    uiState.pendingConnections.forEach { connection ->
                        NeoCard(
                            backgroundColor = ProofWhite,
                            shadowOffset = 2.dp,
                            modifier = Modifier.padding(bottom = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "To: ${connection.alumniName}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ProofInk
                                )
                                StatusBadge(status = connection.status)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "For: ${connection.requestTitle}",
                                style = MaterialTheme.typography.bodySmall,
                                color = ProofTextMuted
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Waiting for the alumnus to review scope and accept/decline.",
                                style = MaterialTheme.typography.bodySmall,
                                color = ProofTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Monetization Placement in Feed (1 Test Banner Ad per screen)
                NeoBannerAd(
                    adTitle = "CAMPUS SPONSOR • CAREER FELLOWSHIP",
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // SECTION 3: OPEN HELP REQUESTS
                Text(
                    text = "YOUR OPEN HELP REQUESTS",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    ),
                    color = ProofInk
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (uiState.activeRequests.isEmpty()) {
                    EmptyStateCard(
                        title = "No Open Requests",
                        message = "Start with one specific thing you need help with (e.g. portfolio feedback, mock interview).",
                        actionText = "Create Help Request",
                        onAction = onRequestHelpClick
                    )
                } else {
                    uiState.activeRequests.forEach { req ->
                        NeoCard(
                            backgroundColor = ProofWhite,
                            shadowOffset = 3.dp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = req.title,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = ProofInk,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                StatusBadge(status = req.status)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = req.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = ProofInk,
                                maxLines = 3
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Category & duration tags
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                NeoTag(text = req.category, backgroundColor = ProofLavender)
                                NeoTag(text = "${req.durationMinutes} mins", backgroundColor = ProofSurfaceMuted)
                            }

                            // Skills tags
                            if (req.requiredSkills.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    req.requiredSkills.take(3).forEach { skill ->
                                        NeoTag(
                                            text = skill,
                                            backgroundColor = ProofWhite,
                                            borderColor = ProofBorder
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            NeoButton(
                                text = "View Matched Alumni",
                                onClick = { onViewMatchesClick(req.id) },
                                backgroundColor = ProofCobalt,
                                icon = Icons.Default.ArrowForward
                            )
                        }
                    }
                }

                // SECTION 4: OUTCOMES HISTORY (if present)
                if (uiState.completedOutcomes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "COMPLETED OUTCOMES & EVIDENCE",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = ProofInk
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    uiState.completedOutcomes.forEach { outcome ->
                        NeoCard(
                            backgroundColor = ProofWhite,
                            shadowOffset = 2.dp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = outcome.actionDescription,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ProofInk,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                StatusBadge(status = if (outcome.alumniConfirmed) "CONFIRMED" else "COMPLETED")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
