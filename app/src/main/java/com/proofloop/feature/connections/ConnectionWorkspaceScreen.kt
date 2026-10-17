package com.proofloop.feature.connections

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
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
fun ConnectionWorkspaceScreen(
    connectionId: String,
    onNavigateBack: () -> Unit,
    viewModel: ConnectionWorkspaceViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(connectionId) {
        viewModel.loadConnection(connectionId)
    }

    val conn = uiState.connection
    val outcome = uiState.outcome
    val currentUser = uiState.currentUser

    val isStudent = currentUser?.uid == conn?.studentId
    val isAlumni = currentUser?.uid == conn?.alumniId

    val currentStep = when {
        outcome?.alumniConfirmed == true -> 5
        outcome?.studentCompleted == true -> 5
        else -> 4
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
                        text = "CONNECTION WORKSPACE",
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
                // Workflow Progress Card
                WorkflowHero(currentStep = currentStep)

                Spacer(modifier = Modifier.height(14.dp))

                // Connection Details Card
                if (conn == null && uiState.isLoading) {
                    Spacer(modifier = Modifier.height(20.dp))
                    NeoLoadingIndicator("Loading session workspace...")
                } else if (conn != null) {
                    NeoCard(
                        backgroundColor = ProofWhite,
                        shadowOffset = 3.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SESSION PARTICIPANTS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black
                                ),
                                color = ProofTextMuted
                            )
                            StatusBadge(status = conn.status)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "STUDENT",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ProofTextMuted
                                )
                                Text(
                                    text = conn.studentName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ProofInk
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "ALUMNUS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ProofTextMuted
                                )
                                Text(
                                    text = conn.alumniName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ProofCobalt
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = ProofBorder, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "ORIGINAL REQUEST:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                            color = ProofTextMuted
                        )
                        Text(
                            text = conn.requestTitle,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = ProofInk
                        )
                    }
                }

                // Feedback Banner
                androidx.compose.animation.AnimatedVisibility(
                    visible = uiState.feedbackMessage != null,
                    enter = androidx.compose.animation.expandVertically() + androidx.compose.animation.fadeIn(),
                    exit = androidx.compose.animation.shrinkVertically() + androidx.compose.animation.fadeOut()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        NeoCard(backgroundColor = ProofAcidLime, shadowOffset = 2.dp) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = ProofInk)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = uiState.feedbackMessage ?: "",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ProofInk
                                )
                            }
                        }
                    }
                }

                // Error Banner
                androidx.compose.animation.AnimatedVisibility(
                    visible = uiState.errorMessage != null,
                    enter = androidx.compose.animation.expandVertically() + androidx.compose.animation.fadeIn(),
                    exit = androidx.compose.animation.shrinkVertically() + androidx.compose.animation.fadeOut()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        NeoCard(backgroundColor = ProofCoral, shadowOffset = 2.dp) {
                            Text(
                                text = uiState.errorMessage ?: "",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = ProofWhite
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // AGREED NEXT ACTION CARD
                NeoCard(
                    backgroundColor = ProofWhite,
                    shadowOffset = 4.dp
                ) {
                    Text(
                        text = "AGREED NEXT ACTION",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = ProofCobalt
                    )
                    Text(
                        text = "Translate discussion into a concrete, verifiable step.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ProofTextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val isActionLocked = outcome?.studentCompleted == true

                    if (isActionLocked) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ProofPaper,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, ProofBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = uiState.actionText.ifBlank { "Action defined during session" },
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = ProofInk,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    } else {
                        NeoTextField(
                            value = uiState.actionText,
                            onValueChange = viewModel::onActionTextChange,
                            label = "Action Description",
                            placeholder = "e.g. Refactor CartRepository using Flow and add unit test",
                            singleLine = false,
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        NeoButton(
                            text = "Save Action",
                            onClick = viewModel::saveAgreedAction,
                            backgroundColor = ProofWhite,
                            contentColor = ProofInk,
                            icon = Icons.Default.Save,
                            shadowOffset = 2.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // FOLLOW-THROUGH OUTCOME TRACKER
                Text(
                    text = "OUTCOME FOLLOW-THROUGH & CONFIRMATION",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    ),
                    color = ProofInk
                )
                Text(
                    text = "Both parties record status independently. Confirmation requires separate alumnus authority.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ProofTextMuted
                )

                Spacer(modifier = Modifier.height(10.dp))

                // STEP 1: Student Completion
                val studentDone = outcome?.studentCompleted == true
                NeoCard(
                    backgroundColor = if (studentDone) ProofWhite else ProofWhite,
                    shadowOffset = 3.dp,
                    borderColor = if (studentDone) ProofBorder else ProofBorder
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "STEP 1: STUDENT COMPLETION",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = ProofInk
                            )
                            Text(
                                text = "Student self-reported completion of agreed action.",
                                style = MaterialTheme.typography.bodySmall,
                                color = ProofTextMuted
                            )
                        }
                        if (studentDone) {
                            StatusBadge(status = "COMPLETED")
                        } else {
                            StatusBadge(status = "PENDING")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!studentDone) {
                        if (isStudent) {
                            NeoButton(
                                text = "Mark Action Completed",
                                onClick = viewModel::studentMarkComplete,
                                backgroundColor = ProofCobalt,
                                contentColor = ProofWhite,
                                icon = Icons.Default.CheckCircle
                            )
                        } else {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ProofSurfaceMuted,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Waiting for ${conn?.studentName ?: "student"} to mark the action completed.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ProofTextMuted,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ProofCobalt, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Student reported completion. Awaiting alumnus confirmation.",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = ProofInk
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // STEP 2: Alumni Confirmation
                val alumniDone = outcome?.alumniConfirmed == true
                NeoCard(
                    backgroundColor = if (alumniDone) ProofWhite else ProofWhite,
                    shadowOffset = 3.dp,
                    borderColor = ProofBorder
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "STEP 2: ALUMNI CONFIRMATION",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = ProofInk
                            )
                            Text(
                                text = "Independent second-party confirmation by alumnus.",
                                style = MaterialTheme.typography.bodySmall,
                                color = ProofTextMuted
                            )
                        }
                        if (alumniDone) {
                            StatusBadge(status = "CONFIRMED")
                        } else {
                            StatusBadge(status = "PENDING")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!alumniDone) {
                        if (!studentDone) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ProofSurfaceMuted,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Confirmation unlocks once the student marks the action completed.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ProofTextMuted,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        } else {
                            if (isAlumni) {
                                NeoButton(
                                    text = "Confirm Interaction Outcome",
                                    onClick = viewModel::alumniConfirmOutcome,
                                    backgroundColor = ProofAcidLime,
                                    contentColor = ProofInk,
                                    icon = Icons.Default.Check
                                )
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = ProofLavender,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Awaiting ${conn?.alumniName ?: "alumnus"} to independently confirm the interaction outcome.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = ProofInk,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ProofAcidLime,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, ProofBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ProofInk)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "FULL LOOP CLOSED & CONFIRMED",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Black
                                        ),
                                        color = ProofInk
                                    )
                                    Text(
                                        text = "Both student completion and alumnus confirmation recorded in Firestore.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ProofInk
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // HONESTY DISCLAIMER
                NeoCard(
                    backgroundColor = ProofPaper,
                    shadowOffset = 2.dp,
                    borderColor = ProofBorder
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = ProofInk,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ProofLoop tracks follow-through on agreed guidance. Second-party alumni confirmation verifies that the interaction took place and resulted in practical action, not a guarantee of employment, internship, or career outcome.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ProofTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
