package com.proofloop.feature.connections

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
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
import com.proofloop.data.model.ConnectionStatus
import com.proofloop.data.model.SessionStatus

@Composable
fun ConnectionWorkspaceScreen(
    connectionId: String,
    onNavigateBack: () -> Unit,
    viewModel: ConnectionWorkspaceViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var showScheduleDialog by remember { mutableStateOf(false) }
    var showRescheduleDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    var scheduleInputTime by remember { mutableStateOf("Saturday 3:00 PM") }
    var rescheduleReason by remember { mutableStateOf("Academic conflict / deadline shift") }
    var cancelReason by remember { mutableStateOf("Scope no longer applicable") }

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
            // Workspace Top App Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ProofWhite,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, ProofBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .size(30.dp)
                                .background(ProofPaper, RoundedCornerShape(5.dp))
                                .border(1.5.dp, ProofBorder, RoundedCornerShape(5.dp))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = ProofObsidianInk,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "WORKSPACE",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black
                                ),
                                color = ProofObsidianInk
                            )
                            Text(
                                text = "Two-Party Locked Commitment",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = ProofCharcoalMuted
                            )
                        }
                    }

                    // Live Sync Pill
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = ProofPaperLight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ProofBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            NeoPulseIndicator(color = ProofSignalEmerald, size = 6.dp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "FIRESTORE SYNC",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 8.5.sp
                                ),
                                color = ProofObsidianInk
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // 5-Stage Mission-to-Outcome Timeline Card
                MissionToOutcomeTimelineCard(
                    currentStep = currentStep,
                    actionDescription = uiState.actionText,
                    dueDate = uiState.dueDateText,
                    ownerName = conn?.studentName ?: "Student",
                    isStudentCompleted = outcome?.studentCompleted == true,
                    isAlumniConfirmed = outcome?.alumniConfirmed == true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Compact Connection Participants Ribbon (saves ~70dp)
                if (conn == null && uiState.isLoading) {
                    Spacer(modifier = Modifier.height(10.dp))
                    NeoLoadingIndicator("Loading session workspace...")
                } else if (conn != null) {
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
                                    text = conn.studentName,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = ProofObsidianInk
                                )
                                Text(
                                    text = " ⇄ ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ProofKleinBlue
                                )
                                Text(
                                    text = conn.alumniName,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = ProofObsidianInk
                                )
                            }
                            StatusBadge(status = conn.status)
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Mission: ${conn.requestTitle}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = ProofCharcoalMuted,
                            maxLines = 1
                        )
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
                        NeoCard(
                            backgroundColor = ProofSignalEmerald,
                            contentPadding = 8.dp,
                            shadowOffset = 2.dp
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = ProofObsidianInk, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = uiState.feedbackMessage ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = ProofObsidianInk
                                )
                            }
                        }
                    }
                }

                // Error Banner
                AnimatedVisibility(
                    visible = uiState.errorMessage != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        NeoCard(
                            backgroundColor = ProofSolarTangerine.copy(alpha = 0.2f),
                            borderColor = ProofSolarTangerine,
                            contentPadding = 8.dp,
                            shadowOffset = 2.dp
                        ) {
                            Text(
                                text = uiState.errorMessage ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = ProofObsidianInk
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // COMMITMENT ENGINE: SESSION SCHEDULING CARD
                if (conn != null && conn.status != ConnectionStatus.CANCELLED.name) {
                    NeoCard(
                        backgroundColor = ProofPureWhite,
                        contentPadding = 10.dp,
                        shadowOffset = 2.5.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = ProofKleinBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SESSION COMMITMENT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = ProofObsidianInk
                                )
                            }
                            NeoTag(
                                text = conn.sessionStatus.ifBlank { "SCHEDULED" },
                                backgroundColor = if (conn.sessionStatus == SessionStatus.CONFIRMED.name) ProofSignalEmerald else ProofDigitalLilac
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        val scheduledTimeDisplay = if (conn.sessionAt.isNotBlank()) {
                            "${conn.sessionAt} (${conn.timeZone.ifBlank { "Local Device TZ" }})"
                        } else {
                            "No session time agreed yet. Propose below."
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ProofPaperLight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ProofRecessedMuted),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "SCHEDULED MEETING TIME:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    ),
                                    color = ProofCharcoalMuted
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = scheduledTimeDisplay,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                                    color = ProofObsidianInk
                                )

                                if (conn.cancellationReason.isNotBlank() && conn.sessionStatus == SessionStatus.RESCHEDULED.name) {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "Reschedule context: ${conn.cancellationReason}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = ProofSolarTangerine
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (conn.sessionAt.isBlank()) {
                                NeoButton(
                                    text = "Set Session Time",
                                    onClick = { showScheduleDialog = true },
                                    backgroundColor = ProofDigitalLilac,
                                    contentColor = ProofObsidianInk,
                                    icon = Icons.Default.EditCalendar,
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                NeoButton(
                                    text = "Reschedule",
                                    onClick = { showRescheduleDialog = true },
                                    backgroundColor = ProofDigitalLilac,
                                    contentColor = ProofObsidianInk,
                                    icon = Icons.Default.Schedule,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            NeoOutlinedButton(
                                text = "Cancel Mission",
                                onClick = { showCancelDialog = true },
                                backgroundColor = ProofPureWhite,
                                contentColor = ProofSolarTangerine,
                                icon = Icons.Default.Cancel,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                } else if (conn != null && conn.status == ConnectionStatus.CANCELLED.name) {
                    NeoCard(
                        backgroundColor = ProofSolarTangerine.copy(alpha = 0.12f),
                        borderColor = ProofSolarTangerine,
                        contentPadding = 8.dp,
                        shadowOffset = 2.dp
                    ) {
                        Text(
                            text = "MISSION CONNECTION CANCELLED",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = ProofSolarTangerine
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Reason: ${conn.cancellationReason.ifBlank { "Explicitly cancelled by participant" }}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = ProofObsidianInk
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // AGREED NEXT ACTION COMMITMENT CARD
                NeoCard(
                    backgroundColor = ProofPureWhite,
                    contentPadding = 10.dp,
                    shadowOffset = 2.5.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AGREED NEXT ACTION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = ProofObsidianInk
                        )
                        Text(
                            text = "CONCRETE DELIVERABLE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = ProofCharcoalMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val isActionLocked = outcome?.studentCompleted == true

                    if (isActionLocked) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ProofPaperLight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ProofRecessedMuted),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = uiState.actionText.ifBlank { "Action defined during session" },
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                                    color = ProofObsidianInk
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Owner: ${uiState.ownerText}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                        color = ProofCharcoalMuted
                                    )
                                    Text(
                                        text = "Due: ${uiState.dueDateText}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                        color = ProofCharcoalMuted
                                    )
                                }
                            }
                        }
                    } else {
                        NeoTextField(
                            value = uiState.actionText,
                            onValueChange = viewModel::onActionTextChange,
                            label = "Action Description",
                            placeholder = "e.g. Refactor CartRepository using Flow and add unit test",
                            singleLine = false,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            NeoTextField(
                                value = uiState.dueDateText,
                                onValueChange = viewModel::onDueDateTextChange,
                                label = "Due Date",
                                placeholder = "e.g. Within 7 days",
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            NeoTextField(
                                value = uiState.ownerText,
                                onValueChange = viewModel::onOwnerTextChange,
                                label = "Owner",
                                placeholder = "Student",
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        NeoButton(
                            text = "Save Action Commitment",
                            onClick = viewModel::saveAgreedAction,
                            backgroundColor = ProofVoltCitron,
                            contentColor = ProofObsidianInk,
                            icon = Icons.Default.Save,
                            shadowOffset = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // FOLLOW-THROUGH OUTCOME TRACKER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "OUTCOME FOLLOW-THROUGH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = ProofObsidianInk
                    )
                    Text(
                        text = "INDEPENDENT 2-PARTY VERIFICATION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = ProofCharcoalMuted
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // STEP 1: Student Completion
                val studentDone = outcome?.studentCompleted == true
                NeoCard(
                    backgroundColor = ProofPureWhite,
                    contentPadding = 10.dp,
                    shadowOffset = 2.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "STEP 1: STUDENT COMPLETION",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                                color = ProofObsidianInk
                            )
                            Text(
                                text = "Student self-reported completion of agreed action.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = ProofCharcoalMuted
                            )
                        }
                        if (studentDone) {
                            StatusBadge(status = "COMPLETED")
                        } else {
                            StatusBadge(status = "PENDING")
                        }
                    }

                    if (!studentDone) {
                        Spacer(modifier = Modifier.height(8.dp))
                        if (isStudent) {
                            NeoButton(
                                text = "Mark Action Completed",
                                onClick = viewModel::studentMarkComplete,
                                backgroundColor = ProofDigitalLilac,
                                contentColor = ProofObsidianInk,
                                icon = Icons.Default.CheckCircle,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ProofPaperLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Waiting for ${conn?.studentName ?: "student"} to mark the action completed.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = ProofCharcoalMuted,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ProofSignalEmerald, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Student reported completion. Awaiting alumnus confirmation.",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                color = ProofObsidianInk
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // STEP 2: Alumni Confirmation
                val alumniDone = outcome?.alumniConfirmed == true
                NeoCard(
                    backgroundColor = ProofPureWhite,
                    contentPadding = 10.dp,
                    shadowOffset = 2.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "STEP 2: ALUMNI CONFIRMATION",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                                color = ProofObsidianInk
                            )
                            Text(
                                text = "Independent second-party confirmation by alumnus.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = ProofCharcoalMuted
                            )
                        }
                        if (alumniDone) {
                            StatusBadge(status = "CONFIRMED")
                        } else {
                            StatusBadge(status = "PENDING")
                        }
                    }

                    if (!alumniDone) {
                        Spacer(modifier = Modifier.height(8.dp))
                        if (!studentDone) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ProofPaperLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Unlocks once the student marks the action completed.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = ProofCharcoalMuted,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        } else {
                            if (isAlumni) {
                                NeoButton(
                                    text = "Confirm Interaction Outcome",
                                    onClick = viewModel::alumniConfirmOutcome,
                                    backgroundColor = ProofSignalEmerald,
                                    contentColor = ProofObsidianInk,
                                    icon = Icons.Default.Check,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = ProofDigitalLilac.copy(alpha = 0.2f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Awaiting ${conn?.alumniName ?: "alumnus"} to independently confirm outcome.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 11.sp),
                                        color = ProofObsidianInk,
                                        modifier = Modifier.padding(6.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(6.dp))
                        NeoCard(
                            backgroundColor = ProofSignalEmerald.copy(alpha = 0.15f),
                            borderColor = ProofSignalEmerald,
                            contentPadding = 8.dp,
                            shadowOffset = 2.dp,
                            modifier = Modifier.fillMaxWidth()
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
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = ProofSignalEmerald,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = "FULL LOOP CLOSED & CONFIRMED",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 10.sp,
                                                letterSpacing = 0.5.sp
                                            ),
                                            color = ProofObsidianInk
                                        )
                                        Text(
                                            text = "Verified follow-through recorded permanently.",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                            color = ProofCharcoalMuted
                                        )
                                    }
                                }
                                NeoVerificationSeal(text = "CLOSED")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // HONESTY DISCLAIMER (Condensed 1-line pill)
                NeoCard(
                    backgroundColor = ProofPaperLight,
                    contentPadding = 6.dp,
                    shadowOffset = 1.5.dp
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = ProofObsidianInk,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ProofLoop tracks follow-through on agreed guidance. Confirmation verifies action took place, not guaranteed job placement.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = ProofCharcoalMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Schedule Session Dialog
    if (showScheduleDialog) {
        AlertDialog(
            onDismissRequest = { showScheduleDialog = false },
            title = {
                Text("Schedule Session Time", fontWeight = FontWeight.Bold, color = ProofInk)
            },
            text = {
                Column {
                    Text("Select agreed meeting time with time-zone awareness:", fontSize = 13.sp, color = ProofTextMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = scheduleInputTime,
                        onValueChange = { scheduleInputTime = it },
                        label = { Text("Meeting Time") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.scheduleSession(scheduleInputTime)
                        showScheduleDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ProofSignalMint, contentColor = ProofInk)
                ) {
                    Text("Confirm Schedule", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showScheduleDialog = false }) {
                    Text("Cancel", color = ProofTextMuted)
                }
            }
        )
    }

    // Reschedule Dialog
    if (showRescheduleDialog) {
        AlertDialog(
            onDismissRequest = { showRescheduleDialog = false },
            title = {
                Text("Reschedule Session", fontWeight = FontWeight.Bold, color = ProofInk)
            },
            text = {
                Column {
                    Text("Propose new time and state reason for reschedule:", fontSize = 13.sp, color = ProofTextMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = scheduleInputTime,
                        onValueChange = { scheduleInputTime = it },
                        label = { Text("New Proposed Time") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rescheduleReason,
                        onValueChange = { rescheduleReason = it },
                        label = { Text("Reason for Reschedule") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.rescheduleSession(scheduleInputTime, rescheduleReason)
                        showRescheduleDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ProofElectricLilac, contentColor = ProofInk)
                ) {
                    Text("Reschedule", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRescheduleDialog = false }) {
                    Text("Cancel", color = ProofTextMuted)
                }
            }
        )
    }

    // Cancellation Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = {
                Text("Cancel Mission Connection", fontWeight = FontWeight.Bold, color = ProofCoral)
            },
            text = {
                Column {
                    Text("Provide explicit reason for cancelling. This will free up the participant slot.", fontSize = 13.sp, color = ProofTextMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = cancelReason,
                        onValueChange = { cancelReason = it },
                        label = { Text("Cancellation Reason") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelConnection(cancelReason)
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ProofCoral, contentColor = ProofWhite)
                ) {
                    Text("Confirm Cancellation", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Back", color = ProofTextMuted)
                }
            }
        )
    }
}
