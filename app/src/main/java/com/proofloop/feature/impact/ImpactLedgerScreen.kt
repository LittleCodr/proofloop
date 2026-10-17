package com.proofloop.feature.impact

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proofloop.ProofLoopApp
import com.proofloop.core.ui.components.NeoCard
import com.proofloop.core.ui.components.NeoTag
import com.proofloop.core.ui.components.StatusBadge
import com.proofloop.core.ui.theme.*
import com.proofloop.data.model.InstitutionalMetrics
import com.proofloop.data.model.TimeWindowFilter

@Composable
fun ImpactLedgerScreen(
    onNavigateBack: () -> Unit
) {
    val repository = ProofLoopApp.instance.repository
    var selectedFilter by remember { mutableStateOf(TimeWindowFilter.ALL_TIME) }
    val metrics by repository.getInstitutionalMetricsFiltered(selectedFilter).collectAsState(
        initial = InstitutionalMetrics(filterWindow = selectedFilter)
    )
    val scrollState = rememberScrollState()

    var activeMetricDefinition by remember { mutableStateOf<MetricDefinition?>(null) }
    var exportNotice by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ProofPaper
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ProofPaper,
                border = BorderStroke(1.dp, ProofBorder)
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "IMPACT LEDGER",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp
                            ),
                            color = ProofInk
                        )
                        Text(
                            text = "Institutional Mentorship Telemetry",
                            style = MaterialTheme.typography.bodySmall,
                            color = ProofTextMuted
                        )
                    }
                    // Export CTA
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ProofElectricLilac,
                        border = BorderStroke(1.5.dp, ProofBorder),
                        modifier = Modifier.clickable {
                            exportNotice = "Exported aggregate telemetry CSV (${selectedFilter.label}). Student PII preserved."
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = ProofInk, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "EXPORT",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                color = ProofInk
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Export Notice Banner
                AnimatedVisibility(
                    visible = exportNotice != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        NeoCard(backgroundColor = ProofSignalMint, shadowOffset = 2.dp) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = exportNotice ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = ProofInk
                                )
                                Text(
                                    text = "✕",
                                    modifier = Modifier.clickable { exportNotice = null },
                                    fontWeight = FontWeight.Black,
                                    color = ProofInk
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // DATE FILTER CHIPS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimeWindowFilter.values().forEach { filter ->
                        val isSelected = selectedFilter == filter
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) ProofInk else ProofWhite,
                            border = BorderStroke(1.5.dp, ProofBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedFilter = filter }
                        ) {
                            Text(
                                text = filter.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = if (isSelected) ProofWhite else ProofInk,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Institutional Telemetry Disclaimer Card
                NeoCard(
                    backgroundColor = ProofPlumSurface,
                    shadowColor = ProofInk,
                    shadowOffset = 3.dp
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = ProofSignalMint,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "PRIVACY-CONSCIOUS AGGREGATION",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    ),
                                    color = ProofSignalMint
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                NeoTag(text = "k ≥ 3 ANONYMITY", backgroundColor = ProofSignalMint.copy(alpha = 0.3f), contentColor = ProofWhite)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Campus analytics measure workflow completion and response latency across opted-in participants. Private session correspondence is never stored or audited. Student self-reporting is audited separately from 2nd-party alumni confirmation.",
                                style = MaterialTheme.typography.bodySmall,
                                color = ProofMutedLavender
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // KPI Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CAMPUS BENCHMARKS (${selectedFilter.label.uppercase()})",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = ProofInk
                    )
                    Text(
                        text = "Tap metric for formula ⓘ",
                        style = MaterialTheme.typography.labelSmall,
                        color = ProofTextMuted
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    MetricCard(
                        title = "LOOP CLOSURE",
                        value = "${metrics.loopClosureRatePercent}%",
                        subtitle = "Confirmed Outcomes",
                        backgroundColor = ProofSignalMint,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                activeMetricDefinition = MetricDefinition(
                                    name = "Loop Closure Rate",
                                    formula = "(Alumni-Confirmed Outcomes / Accepted Connections) × 100",
                                    description = "Measures the percentage of mentoring engagements where the student submitted a deliverable and the alumnus independently confirmed its completion.",
                                    includedStatuses = "Outcomes where alumniConfirmed == true against connections with status ACCEPTED or COMPLETED.",
                                    timeWindow = selectedFilter.label,
                                    limitations = "Does not measure external career outcomes (e.g. landing a job); verifies purely that the committed micro-guidance was acted upon."
                                )
                            }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    MetricCard(
                        title = "ACCEPTANCE",
                        value = "${metrics.acceptanceRatePercent}%",
                        subtitle = "Capacity-Aware Match",
                        backgroundColor = ProofElectricLilac,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                activeMetricDefinition = MetricDefinition(
                                    name = "Acceptance Rate",
                                    formula = "(Accepted Connections / Total Connection Requests) × 100",
                                    description = "Evaluates the proportion of student requests accepted by alumni, strictly enforcing the 3-connection capacity ceiling.",
                                    includedStatuses = "Connections with status ACCEPTED, COMPLETED, or RESCHEDULED vs total requests.",
                                    timeWindow = selectedFilter.label,
                                    limitations = "Alumni at max capacity cannot accept new requests until active missions are closed."
                                )
                            }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    MetricCard(
                        title = "AVG RESPONSE",
                        value = "${metrics.meanResponseHours}h",
                        subtitle = "Time to Decision",
                        backgroundColor = ProofWhite,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                activeMetricDefinition = MetricDefinition(
                                    name = "Mean Response Latency",
                                    formula = "Mean(connection.updatedAt - connection.createdAt) in hours",
                                    description = "Average elapsed time from student submission to alumnus decision (Accept or Decline).",
                                    includedStatuses = "All resolved connection requests.",
                                    timeWindow = selectedFilter.label,
                                    limitations = "Pending requests awaiting decision are monitored separately under Loop Health."
                                )
                            }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    MetricCard(
                        title = "ACTIVE MENTORS",
                        value = "${metrics.activeMentorsCount}",
                        subtitle = "Opted-In Alumni",
                        backgroundColor = ProofWhite,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                activeMetricDefinition = MetricDefinition(
                                    name = "Active Mentor Cohort",
                                    formula = "Count of publicProfiles with acceptingRequests == true and isVerifiedAlumni == true",
                                    description = "Number of verified alumni actively discoverable for new student micro-missions.",
                                    includedStatuses = "Verified alumni profiles with active capacity.",
                                    timeWindow = "Live snapshot",
                                    limitations = "Paused mentors are excluded from matching."
                                )
                            }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 5-STAGE WORKFLOW FUNNEL
                Text(
                    text = "5-STAGE VERIFIABLE FUNNEL",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = ProofInk
                )
                Text(
                    text = "Telemetry tracks every stage from initial micro-request to second-party verified closure.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ProofTextMuted
                )
                Spacer(modifier = Modifier.height(10.dp))

                NeoCard(backgroundColor = ProofWhite, shadowOffset = 3.dp) {
                    FunnelStageRow(
                        stageNumber = "1",
                        stageName = "REQUESTS SCOPED",
                        detail = "Structured micro-missions submitted",
                        count = "${metrics.totalRequests}",
                        color = ProofInk
                    )
                    HorizontalDivider(color = ProofBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))
                    FunnelStageRow(
                        stageNumber = "2",
                        stageName = "DETERMINISTIC MATCHES",
                        detail = "Explainable affinity & capacity matches",
                        count = "${metrics.totalConnections}",
                        color = ProofInk
                    )
                    HorizontalDivider(color = ProofBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))
                    FunnelStageRow(
                        stageNumber = "3",
                        stageName = "MENTOR ACCEPTANCES",
                        detail = "Accepted under 3-connection capacity limit",
                        count = "${metrics.acceptedConnections}",
                        color = ProofElectricLilac
                    )
                    HorizontalDivider(color = ProofBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))
                    FunnelStageRow(
                        stageNumber = "4",
                        stageName = "STUDENT ACTION REPORTED",
                        detail = "Self-reported practical task completion",
                        count = "${metrics.studentReportedCount}",
                        color = ProofCoral
                    )
                    HorizontalDivider(color = ProofBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))
                    FunnelStageRow(
                        stageNumber = "5",
                        stageName = "ALUMNI CONFIRMED OUTCOMES",
                        detail = "Independent second-party closed loop verification",
                        count = "${metrics.alumniConfirmedCount}",
                        color = ProofSignalMint
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Institutional Value Thesis
                NeoCard(backgroundColor = ProofWhite, shadowOffset = 3.dp) {
                    Text(
                        text = "THE PROOFLOOP INSTITUTIONAL VALUE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = ProofInk
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Traditional alumni directories track passive member counts, not student outcomes. ProofLoop provides universities with real-time operational visibility into whether advice was delivered, what actionable commitment was agreed upon, and whether it was independently confirmed—without violating student privacy.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ProofInk
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Metric Definition Dialog
    if (activeMetricDefinition != null) {
        val def = activeMetricDefinition!!
        AlertDialog(
            onDismissRequest = { activeMetricDefinition = null },
            title = {
                Text(def.name, fontWeight = FontWeight.Black, color = ProofInk)
            },
            text = {
                Column {
                    Text("FORMULA:", fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = ProofCobalt)
                    Text(def.formula, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ProofInk)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("EXPLANATION:", fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = ProofTextMuted)
                    Text(def.description, fontSize = 13.sp, color = ProofInk)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("INCLUDED STATUSES:", fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = ProofTextMuted)
                    Text(def.includedStatuses, fontSize = 12.sp, color = ProofInk)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("LIMITATIONS:", fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = ProofCoral)
                    Text(def.limitations, fontSize = 12.sp, color = ProofInk)
                }
            },
            confirmButton = {
                Button(
                    onClick = { activeMetricDefinition = null },
                    colors = ButtonDefaults.buttonColors(containerColor = ProofInk, contentColor = ProofWhite)
                ) {
                    Text("Close")
                }
            }
        )
    }
}

data class MetricDefinition(
    val name: String,
    val formula: String,
    val description: String,
    val includedStatuses: String,
    val timeWindow: String,
    val limitations: String
)

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    NeoCard(
        backgroundColor = backgroundColor,
        shadowOffset = 2.dp,
        modifier = modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            ),
            color = ProofInk
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            ),
            color = ProofInk
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = ProofInk.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun FunnelStageRow(
    stageNumber: String,
    stageName: String,
    detail: String,
    count: String,
    color: Color
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
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = color,
                border = BorderStroke(1.dp, ProofBorder),
                modifier = Modifier.size(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stageNumber,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        ),
                        color = if (color == ProofInk) ProofWhite else ProofInk
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = stageName,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = ProofInk
                )
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = ProofTextMuted
                )
            }
        }
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            ),
            color = ProofInk
        )
    }
}
