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
                    // Profile & Opt-in High-Density Control Bar (saves ~150dp)
                    val profile = uiState.profile
                    NeoCard(
                        backgroundColor = ProofWhite,
                        shadowOffset = 2.dp,
                        cornerRadius = 8.dp,
                        accentColor = ProofSignalEmerald,
                        contentPadding = 8.dp
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
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp
                                        ),
                                        color = ProofObsidianInk
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(3.dp),
                                        color = ProofSignalEmerald,
                                        border = BorderStroke(1.dp, ProofBorder)
                                    ) {
                                        Text(
                                            text = "VERIFIED",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 8.sp
                                            ),
                                            color = ProofObsidianInk,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                val activeCount = uiState.acceptedConnections.count { it.status == "ACCEPTED" }
                                Text(
                                    text = "${profile?.jobTitle ?: currentRole} • $activeCount/${profile?.maxActiveRequests ?: 3} Capacity",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = ProofCharcoalMuted
                                )
                            }

                            // Opt-in switch with live radar pulse
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (profile?.acceptingRequests == true) {
                                    NeoPulseIndicator(color = ProofSignalEmerald, size = 6.dp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = if (profile?.acceptingRequests == true) "OPEN" else "OFF",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp
                                    ),
                                    color = if (profile?.acceptingRequests == true) ProofObsidianInk else ProofSolarTangerine
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Switch(
                                    checked = profile?.acceptingRequests ?: true,
                                    onCheckedChange = { viewModel.toggleAcceptingRequests(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = ProofWhite,
                                        checkedTrackColor = ProofSignalEmerald,
                                        uncheckedThumbColor = ProofWhite,
                                        uncheckedTrackColor = ProofSurfaceMuted
                                    ),
                                    modifier = Modifier.height(28.dp)
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
                        Spacer(modifier = Modifier.height(8.dp))
                        NeoCard(backgroundColor = ProofVoltCitron, shadowOffset = 2.dp, contentPadding = 8.dp) {
                            Text(
                                text = uiState.feedbackMessage ?: "",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = ProofObsidianInk
                            )
                        }
                    }
                }

                // LOOP HEALTH OPERATIONAL ATTENTION PANEL FOR ALUMNI
                if (uiState.loopHealthItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    NeoCard(
                        backgroundColor = ProofPaperLight,
                        borderColor = ProofObsidianInk,
                        shadowOffset = 2.dp,
                        contentPadding = 8.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PriorityHigh,
                                contentDescription = null,
                                tint = ProofSolarTangerine,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LOOP HEALTH • ACTIONS NEEDED (${uiState.loopHealthItems.size})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = ProofObsidianInk
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        uiState.loopHealthItems.forEach { item ->
                            val severityBg = when (item.severity) {
                                LoopHealthSeverity.URGENT -> ProofSolarTangerine.copy(alpha = 0.2f)
                                LoopHealthSeverity.ATTENTION -> ProofVoltCitron.copy(alpha = 0.35f)
                                LoopHealthSeverity.INFO -> ProofWhite
                            }

                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = severityBg,
                                border = BorderStroke(1.dp, ProofBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                                            color = ProofObsidianInk
                                        )
                                        NeoTag(
                                            text = item.severity.name,
                                            backgroundColor = if (item.severity == LoopHealthSeverity.URGENT) ProofSolarTangerine else ProofPaperLight
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = ProofObsidianInk
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Action: ${item.actionPrompt}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 10.sp,
                                            color = ProofKleinBlue
                                        )
                                    )

                                    if (item.connectionId != null) {
                                        Spacer(modifier = Modifier.height(4.dp))
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
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                                    color = ProofObsidianInk,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
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
                    Spacer(modifier = Modifier.height(10.dp))
                    NeoLoadingIndicator("Syncing alumni portal...")
                }

                Spacer(modifier = Modifier.height(10.dp))

                // SECTION 1: INCOMING REQUESTS
                Text(
                    text = "• INCOMING REQUESTS (${uiState.incomingRequests.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = ProofObsidianInk
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (uiState.incomingRequests.isEmpty()) {
                    EmptyStateCard(
                        title = "Inbox Clear",
                        message = "You have no pending requests from students. Opted-in alumni appear in student match results automatically."
                    )
                } else {
                    uiState.incomingRequests.forEach { conn ->
                        NeoCard(
                            backgroundColor = ProofWhite,
                            shadowOffset = 2.dp,
                            contentPadding = 8.dp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "From: ${conn.studentName}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                                    color = ProofObsidianInk
                                )
                                StatusBadge(status = conn.status)
                            }

                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = conn.requestTitle,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = ProofKleinBlue,
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Accept & Decline Buttons (compact 36dp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                NeoVoltButton(
                                    text = "Accept",
                                    onClick = { viewModel.acceptRequest(conn.id) },
                                    icon = Icons.Default.Check,
                                    modifier = Modifier.weight(1f)
                                )
                                NeoOutlinedButton(
                                    text = "Decline",
                                    onClick = { viewModel.declineRequest(conn.id) },
                                    backgroundColor = ProofWhite,
                                    contentColor = ProofSolarTangerine,
                                    icon = Icons.Default.Close,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Monetization Placement in Feed
                NeoBannerAd(
                    adTitle = "ALUMNI PERKS & RECRUITING SPONSOR",
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // SECTION 2: ACCEPTED CONNECTIONS & WORKSPACES
                Text(
                    text = "• ACTIVE SESSIONS (${uiState.acceptedConnections.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = ProofObsidianInk
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (uiState.acceptedConnections.isEmpty()) {
                    EmptyStateCard(
                        title = "No Active Connections",
                        message = "Accepted student connections will appear here to record agreed next actions."
                    )
                } else {
                    uiState.acceptedConnections.forEach { conn ->
                        NeoCard(
                            backgroundColor = ProofWhite,
                            shadowOffset = 2.dp,
                            contentPadding = 8.dp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Student: ${conn.studentName}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                                    color = ProofObsidianInk
                                )
                                StatusBadge(status = conn.status)
                            }

                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Request: ${conn.requestTitle}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = ProofCharcoalMuted,
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = ProofVoltCitron.copy(alpha = 0.35f),
                                border = BorderStroke(1.dp, ProofBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "ACTION: ",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 9.sp
                                        ),
                                        color = ProofObsidianInk
                                    )
                                    Text(
                                        text = conn.agreedNextAction.ifBlank { "Tap Open Workspace to record next step." },
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = ProofObsidianInk,
                                        maxLines = 1,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            NeoVoltButton(
                                text = "Open Workspace →",
                                onClick = { onOpenWorkspaceClick(conn.id) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
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
