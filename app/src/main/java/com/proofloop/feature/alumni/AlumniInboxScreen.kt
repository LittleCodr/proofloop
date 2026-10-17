package com.proofloop.feature.alumni

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
            // App Bar
            NeoTopAppBar(
                title = "PROOFLOOP",
                subtitle = "Alumni Portal",
                roleTag = "ALUMNI",
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
                // Profile & Opt-in Control Card
                val profile = uiState.profile
                NeoCard(
                    backgroundColor = ProofWhite,
                    shadowOffset = 3.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profile?.displayName ?: uiState.currentUser?.name ?: "Alumnus",
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
                                text = "${profile?.jobTitle ?: "Alumnus"} • ${profile?.company ?: ""}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ProofInk
                            )
                        }

                        // Opt-in switch
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                                onCheckedChange = { viewModel.toggleAcceptingRequests() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ProofWhite,
                                    checkedTrackColor = ProofSignalMint,
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
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "ACTIVE SESSIONS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = ProofTextMuted
                            )
                            val activeCount = uiState.acceptedConnections.count { it.status == "ACCEPTED" }
                            Text(
                                text = "$activeCount / ${profile?.maxActiveRequests ?: 3} max",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black
                                ),
                                color = if (activeCount >= (profile?.maxActiveRequests ?: 3)) ProofCoral else ProofInk
                            )
                        }

                        Column {
                            Text(
                                text = "AVAILABILITY",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = ProofTextMuted
                            )
                            Text(
                                text = if (profile?.acceptingRequests == true) "Accepting Missions" else "Paused by Mentor",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (profile?.acceptingRequests == true) ProofInk else ProofCoral
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

                // Monetization Placement in Feed (1 Test Banner Ad per screen)
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

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
