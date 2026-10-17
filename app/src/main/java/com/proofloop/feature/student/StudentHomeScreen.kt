package com.proofloop.feature.student

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Schedule
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
import com.proofloop.data.model.LoopHealthSeverity

@Composable
fun StudentHomeScreen(
    onRequestHelpClick: () -> Unit,
    onViewMatchesClick: (requestId: String) -> Unit,
    onOpenWorkspaceClick: (connectionId: String) -> Unit,
    onOpenImpactLedgerClick: () -> Unit = {},
    onSignOutClick: () -> Unit,
    viewModel: StudentHomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val blueprintScrollState = rememberScrollState()

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
                onOpenImpactLedger = onOpenImpactLedgerClick,
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
                        backgroundColor = ProofElectricLilac,
                        contentColor = ProofInk,
                        icon = Icons.Default.Add
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // MISSION BLUEPRINTS SHELF
                if (uiState.availableBlueprints.isNotEmpty()) {
                    NeoCard(
                        backgroundColor = ProofWhite,
                        shadowOffset = 2.dp
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = null,
                                tint = ProofPlum,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "START FROM A MISSION BLUEPRINT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                ),
                                color = ProofPlum
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(blueprintScrollState),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.availableBlueprints.take(4).forEach { bp ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = ProofPaper,
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ProofBorder),
                                    modifier = Modifier
                                        .width(170.dp)
                                        .clickable { onRequestHelpClick() }
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "${bp.suggestedDurationMinutes}m • ${bp.category}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp
                                            ),
                                            color = ProofTextMuted
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = bp.title,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            ),
                                            color = ProofInk,
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
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

                // Workflow Progress Card
                WorkflowHero(currentStep = currentWorkflowStep)

                // LOOP HEALTH OPERATIONAL ATTENTION PANEL
                if (uiState.loopHealthItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    NeoCard(
                        backgroundColor = ProofPaper,
                        borderColor = ProofInk,
                        shadowOffset = 3.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PriorityHigh,
                                contentDescription = null,
                                tint = ProofCoral,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LOOP HEALTH • OPERATIONAL ATTENTION (${uiState.loopHealthItems.size})",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                ),
                                color = ProofInk
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Bottlenecks detected to help keep your mentorship moving forward smoothly.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ProofTextMuted
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        uiState.loopHealthItems.forEach { item ->
                            val severityBg = when (item.severity) {
                                LoopHealthSeverity.URGENT -> ProofCoral.copy(alpha = 0.2f)
                                LoopHealthSeverity.ATTENTION -> ProofElectricLilac.copy(alpha = 0.35f)
                                LoopHealthSeverity.INFO -> ProofWhite
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = severityBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ProofBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = ProofInk
                                        )
                                        NeoTag(
                                            text = item.severity.name,
                                            backgroundColor = if (item.severity == LoopHealthSeverity.URGENT) ProofCoral else ProofPaper
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ProofInk
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Next step: ${item.actionPrompt}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = ProofPlum
                                        )
                                    )

                                    if (item.connectionId != null) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = ProofWhite,
                                                border = androidx.compose.foundation.BorderStroke(1.dp, ProofBorder),
                                                modifier = Modifier.clickable { onOpenWorkspaceClick(item.connectionId) }
                                            ) {
                                                Text(
                                                    text = "Open Workspace →",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = ProofInk,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

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
                                text = connection.requestTitle,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = ProofInk
                            )

                            if (connection.sessionAt.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Session",
                                        tint = ProofInk,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${connection.sessionAt} ${if (connection.timeZone.isNotEmpty()) "(${connection.timeZone})" else ""}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = ProofInk
                                    )
                                }
                            }

                            if (connection.agreedNextAction.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = ProofAcidLime,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ProofBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "AGREED NEXT ACTION:",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                            color = ProofInk
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = connection.agreedNextAction,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = ProofInk
                                        )
                                        if (connection.nextActionDueDate.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Due: ${connection.nextActionDueDate} • Owner: ${connection.nextActionOwner.ifEmpty { "Student" }}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = ProofInk
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            NeoButton(
                                text = "Open Connection Workspace",
                                onClick = { onOpenWorkspaceClick(connection.id) },
                                backgroundColor = ProofElectricLilac,
                                contentColor = ProofInk,
                                icon = Icons.Default.ArrowForward
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // SECTION 2: PENDING RESPONSES
                Text(
                    text = "AWAITING ALUMNI RESPONSE",
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

                // Monetization Placement in Feed
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
                                if (req.blueprintId.isNotBlank()) {
                                    NeoTag(text = "Blueprint Active", backgroundColor = ProofSignalMint)
                                }
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
                                backgroundColor = ProofElectricLilac,
                                contentColor = ProofInk,
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
