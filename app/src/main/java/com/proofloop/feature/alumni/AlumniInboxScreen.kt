package com.proofloop.feature.alumni

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PriorityHigh
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
import com.proofloop.data.model.UserRole

@Composable
fun AlumniInboxScreen(
    onOpenWorkspaceClick: (connectionId: String) -> Unit,
    onOpenImpactLedgerClick: () -> Unit = {},
    onSignOutClick: () -> Unit,
    viewModel: AlumniInboxViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ProofPaper
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            val currentRole = uiState.currentUser?.role ?: UserRole.ALUMNI.name
            val portalSubtitle = when (currentRole) {
                "FACULTY" -> "Faculty Research Portal"
                "FOUNDER" -> "Founder Collaboration Portal"
                "RECRUITER" -> "Recruiter Talent Portal"
                else -> "Alumni Mentorship Portal"
            }

            // App Bar
            NeoTopAppBar(
                title = "PROOFLOOP",
                subtitle = portalSubtitle,
                roleTag = currentRole,
                onOpenImpactLedger = onOpenImpactLedgerClick,
                onSignOut = {
                    viewModel.logout()
                    onSignOutClick()
                }
            )

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Profile & Opt-in Control Card
                    val profile = uiState.profile
                    NeoCard(
                        backgroundColor = ProofWhite,
                        shadowOffset = 3.5.dp,
                        accentColor = ProofSignalEmerald,
                        stampText = "VERIFIED MENTOR PROFILE"
                    ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profile?.displayName ?: uiState.currentUser?.name ?: "Member",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = ProofInk
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = ProofSignalMint,
                                    border = BorderStroke(1.dp, ProofBorder)
                                ) {
                                    Text(
                                        text = "VERIFIED",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 9.sp
                                        ),
                                        color = ProofInk,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = profile?.subtitle ?: "${profile?.jobTitle ?: currentRole} • ${profile?.company ?: ""}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ProofInk
                            )
                        }


                        // Opt-in switch with live pulse dot
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (profile?.acceptingRequests == true) {
                                NeoPulseIndicator(color = ProofSignalEmerald, size = 8.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = if (profile?.acceptingRequests == true) "OPTED IN" else "PAUSED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = if (profile?.acceptingRequests == true) ProofInk else ProofCoral
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = profile?.acceptingRequests ?: true,
                                onCheckedChange = { viewModel.toggleAcceptingRequests(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ProofWhite,
                                    checkedTrackColor = ProofSignalEmerald,
                                    uncheckedThumbColor = ProofWhite,
                                    uncheckedTrackColor = ProofSurfaceMuted
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = ProofBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val activeCount = uiState.acceptedConnections.count { it.status == "ACCEPTED" }
                        NeoSegmentedCapacityMeter(
                            activeCount = activeCount,
                            maxCount = profile?.maxActiveRequests ?: 3
                        )

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (profile?.acceptingRequests == true) ProofVoltCitron else ProofSurfaceMuted,
                            border = BorderStroke(1.dp, ProofBorder)
                        ) {
                            Text(
                                text = if (profile?.acceptingRequests == true) "ACCEPTING MISSIONS" else "PAUSED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                ),
                                color = ProofInk,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Feedback Banner
                AnimatedVisibility(
                    visible = uiState.feedbackMessage != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        NeoCard(backgroundColor = ProofAcidLime, shadowOffset = 2.dp) {
                            Text(
                                text = uiState.feedbackMessage ?: "",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = ProofInk
                            )
                        }
                    }
                }

                // LOOP HEALTH OPERATIONAL ATTENTION PANEL FOR ALUMNI
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
                                text = "LOOP HEALTH • ACTIONS NEEDED (${uiState.loopHealthItems.size})",
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
                            text = "Outstanding deliverables and pending requests awaiting your confirmation.",
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
                                border = BorderStroke(1.dp, ProofBorder),
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
                                        text = "Action: ${item.actionPrompt}",
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
                                                border = BorderStroke(1.dp, ProofBorder),
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

                if (uiState.isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    NeoLoadingIndicator("Syncing alumni portal...")
                }

                Spacer(modifier = Modifier.height(20.dp))

                // SECTION 1: INCOMING REQUESTS
                Text(
                    text = "INCOMING REQUESTS (${uiState.incomingRequests.size})",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    ),
                    color = ProofInk
                )
                Text(
                    text = "Review scope and time commitment before accepting.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ProofTextMuted
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (uiState.incomingRequests.isEmpty()) {
                    EmptyStateCard(
                        title = "Inbox Clear",
                        message = "You have no pending requests from students. Opted-in alumni appear in student match results automatically."
                    )
                } else {
                    uiState.incomingRequests.forEach { conn ->
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
                                    text = "From: ${conn.studentName}",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = ProofInk
                                )
                                StatusBadge(status = conn.status)
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = conn.requestTitle,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = ProofCobalt
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Accept & Decline Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                NeoButton(
                                    text = "Accept",
                                    onClick = { viewModel.acceptRequest(conn.id) },
                                    backgroundColor = ProofAcidLime,
                                    contentColor = ProofInk,
                                    icon = Icons.Default.Check,
                                    modifier = Modifier.weight(1f)
                                )
                                NeoOutlinedButton(
                                    text = "Decline",
                                    onClick = { viewModel.declineRequest(conn.id) },
                                    backgroundColor = ProofWhite,
                                    contentColor = ProofCoral,
                                    icon = Icons.Default.Close,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Monetization Placement in Feed
                NeoBannerAd(
                    adTitle = "ALUMNI PERKS & RECRUITING SPONSOR",
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // SECTION 2: ACCEPTED CONNECTIONS & WORKSPACES
                Text(
                    text = "ACTIVE CONNECTIONS & SESSIONS (${uiState.acceptedConnections.size})",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    ),
                    color = ProofInk
                )
                Text(
                    text = "Manage agreed next actions and confirm completed outcomes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ProofTextMuted
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (uiState.acceptedConnections.isEmpty()) {
                    EmptyStateCard(
                        title = "No Active Connections",
                        message = "Accepted student connections will appear here to record agreed next actions."
                    )
                } else {
                    uiState.acceptedConnections.forEach { conn ->
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
                                    text = "Student: ${conn.studentName}",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = ProofInk
                                )
                                StatusBadge(status = conn.status)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Request: ${conn.requestTitle}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ProofTextMuted
                            )

                            Spacer(modifier = Modifier.height(8.dp))
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
                                        text = conn.agreedNextAction.ifBlank { "Tap Open Workspace to agree on a practical next step." },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ProofInk
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            NeoButton(
                                text = "Open Workspace",
                                onClick = { onOpenWorkspaceClick(conn.id) },
                                backgroundColor = ProofCobalt,
                                icon = Icons.Default.ArrowForward
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(96.dp))
            }

            // Floating Neo-Brutalist Bottom Dock
            NeoBottomNavBar(
                selectedTab = "INBOX",
                onTabSelected = { tab ->
                    when (tab) {
                        "HUB" -> { /* User can view pending missions */ }
                        "INBOX" -> { /* Current Tab */ }
                        "WORKSPACE" -> {
                            if (uiState.acceptedConnections.isNotEmpty()) {
                                onOpenWorkspaceClick(uiState.acceptedConnections.first().id)
                            }
                        }
                        "LEDGER" -> onOpenImpactLedgerClick()
                    }
                },
                pendingCount = uiState.incomingRequests.size,
                acceptedCount = uiState.acceptedConnections.size,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
