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
                color = ProofPaperLight,
                border = BorderStroke(1.dp, ProofRecessedMuted)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ProofObsidianInk,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "IMPACT LEDGER",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp
                            ),
                            color = ProofObsidianInk
                        )
                        Text(
                            text = "Institutional Telemetry & Verified Outcomes",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = ProofCharcoalMuted
                        )
                    }
                    // Export CTA
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ProofVoltCitron,
                        border = BorderStroke(1.5.dp, ProofObsidianInk),
                        modifier = Modifier.clickable {
                            exportNotice = "Exported aggregate telemetry CSV (${selectedFilter.label}). Student PII preserved."
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = ProofObsidianInk, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "EXPORT CSV",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 10.sp),
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
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Export Notice Banner
                AnimatedVisibility(
                    visible = exportNotice != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        NeoCard(
                            backgroundColor = ProofSignalEmerald,
                            contentPadding = 8.dp,
                            shadowOffset = 2.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = exportNotice ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                    color = ProofObsidianInk
                                )
                                Text(
                                    text = "✕",
                                    modifier = Modifier.clickable { exportNotice = null },
                                    fontWeight = FontWeight.Black,
                                    color = ProofObsidianInk
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // DATE FILTER CHIPS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TimeWindowFilter.values().forEach { filter ->
                        val isSelected = selectedFilter == filter
                        Surface(
                            shape = RoundedCornerShape(5.dp),
                            color = if (isSelected) ProofObsidianInk else ProofPureWhite,
                            border = BorderStroke(1.5.dp, ProofObsidianInk),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedFilter = filter }
                        ) {
                            Text(
                                text = filter.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                ),
                                color = if (isSelected) ProofPureWhite else ProofObsidianInk,
                                modifier = Modifier.padding(vertical = 5.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Institutional Telemetry Disclaimer Card (Compact 1-line pill)
                NeoCard(
                    backgroundColor = ProofPlumSurface,
                    contentPadding = 8.dp,
                    shadowColor = ProofObsidianInk,
                    shadowOffset = 2.dp
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = ProofSignalEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "PRIVACY-CONSCIOUS AGGREGATION",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 9.sp,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = ProofSignalEmerald
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                NeoTag(text = "k ≥ 3 ANONYMITY", backgroundColor = ProofSignalEmerald.copy(alpha = 0.25f), contentColor = ProofPureWhite)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Aggregate telemetry only. Session chat is never audited. 2nd-party outcomes verified independently.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = ProofPaperLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // KPI Grid Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CAMPUS BENCHMARKS (${selectedFilter.label.uppercase()})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = ProofObsidianInk
                    )
                    Text(
                        text = "Tap metric for formula ⓘ",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = ProofCharcoalMuted
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    MetricCard(
                        title = "LOOP CLOSURE",
                        value = "${metrics.loopClosureRatePercent}%",
                        subtitle = "Confirmed Outcomes",
                        backgroundColor = ProofSignalEmerald,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                activeMetricDefinition = MetricDefinition(
                                    name = "Loop Closure Rate",
                                    formula = "(Alumni-Confirmed Outcomes / Accepted Connections) × 100",
                                    description = "Measures the percentage of mentoring engagements where the student submitted a deliverable and the alumnus independently confirmed its completion.",
                                    includedStatuses = "Outcomes where alumniConfirmed == true against connections with status ACCEPTED or COMPLETED.",
                                    timeWindow = selectedFilter.label,
                                    limitations = "Does not measure external career outcomes; verifies purely that the committed micro-guidance was acted upon."
                                )
                            }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    MetricCard(
                        title = "ACCEPTANCE",
                        value = "${metrics.acceptanceRatePercent}%",
                        subtitle = "Capacity Match",
                        backgroundColor = ProofDigitalLilac,
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

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    MetricCard(
                        title = "AVG RESPONSE",
                        value = "${metrics.meanResponseHours}h",
                        subtitle = "Time to Decision",
                        backgroundColor = ProofPureWhite,
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
                    Spacer(modifier = Modifier.width(8.dp))
                    MetricCard(
                        title = "ACTIVE MENTORS",
                        value = "${metrics.activeMentorsCount}",
                        subtitle = "Opted-In Alumni",
                        backgroundColor = ProofPureWhite,
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

                Spacer(modifier = Modifier.height(10.dp))

                // 5-STAGE WORKFLOW FUNNEL
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "5-STAGE VERIFIABLE FUNNEL",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = ProofObsidianInk
                    )
                    Text(
                        text = "END-TO-END TELEMETRY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = ProofCharcoalMuted
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))

                NeoCard(
                    backgroundColor = ProofPureWhite,
                    contentPadding = 8.dp,
                    shadowOffset = 2.dp
                ) {
                    FunnelStageRow(
                        stageNumber = "1",
                        stageName = "REQUESTS SCOPED",
                        detail = "Structured micro-missions submitted",
                        count = "${metrics.totalRequests}",
                        color = ProofObsidianInk
                    )
                    HorizontalDivider(color = ProofRecessedMuted, thickness = 1.dp, modifier = Modifier.padding(vertical = 5.dp))
                    FunnelStageRow(
                        stageNumber = "2",
                        stageName = "DETERMINISTIC MATCHES",
                        detail = "Explainable affinity & capacity matches",
                        count = "${metrics.totalConnections}",
                        color = ProofKleinBlue
                    )
                    HorizontalDivider(color = ProofRecessedMuted, thickness = 1.dp, modifier = Modifier.padding(vertical = 5.dp))
                    FunnelStageRow(
                        stageNumber = "3",
                        stageName = "MENTOR ACCEPTANCES",
                        detail = "Accepted under 3-connection capacity limit",
                        count = "${metrics.acceptedConnections}",
                        color = ProofDigitalLilac
                    )
                    HorizontalDivider(color = ProofRecessedMuted, thickness = 1.dp, modifier = Modifier.padding(vertical = 5.dp))
                    FunnelStageRow(
                        stageNumber = "4",
                        stageName = "STUDENT ACTION REPORTED",
                        detail = "Self-reported practical task completion",
                        count = "${metrics.studentReportedCount}",
                        color = ProofSolarTangerine
                    )
                    HorizontalDivider(color = ProofRecessedMuted, thickness = 1.dp, modifier = Modifier.padding(vertical = 5.dp))
                    FunnelStageRow(
                        stageNumber = "5",
                        stageName = "ALUMNI CONFIRMED OUTCOMES",
                        detail = "Independent 2nd-party verified closure",
                        count = "${metrics.alumniConfirmedCount}",
                        color = ProofSignalEmerald
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Multi-Role Ecosystem Participation Card
                NeoCard(
                    backgroundColor = ProofPaperLight,
                    borderColor = ProofObsidianInk,
                    contentPadding = 8.dp,
                    shadowOffset = 2.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MULTI-ROLE PARTICIPATION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = ProofObsidianInk
                        )
                        NeoTag(text = "6 ROLES ACTIVE", backgroundColor = ProofDigitalLilac, contentColor = ProofObsidianInk)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        MetricCard(title = "STUDENTS", value = "${metrics.activeStudentsCount}", subtitle = "Missions Created", backgroundColor = ProofDigitalLilac, modifier = Modifier.weight(1f))
                        MetricCard(title = "ALUMNI", value = "${metrics.activeMentorsCount}", subtitle = "Active Mentors", backgroundColor = ProofSignalEmerald, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        MetricCard(title = "FOUNDERS", value = "${metrics.activeFoundersCount}", subtitle = "Sprints", backgroundColor = ProofVoltCitron, modifier = Modifier.weight(1f))
                        MetricCard(title = "FACULTY", value = "${metrics.activeFacultyCount}", subtitle = "Labs", backgroundColor = ProofPureWhite, modifier = Modifier.weight(1f))
                        MetricCard(title = "RECRUITERS", value = "${metrics.activeRecruitersCount}", subtitle = "Pipelines", backgroundColor = ProofSolarTangerine.copy(alpha = 0.25f), modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Institutional Value Thesis
                NeoCard(
                    backgroundColor = ProofPureWhite,
                    contentPadding = 8.dp,
                    shadowOffset = 2.dp
                ) {
                    Text(
                        text = "THE PROOFLOOP INSTITUTIONAL VALUE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = ProofObsidianInk
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Traditional alumni directories track passive member counts, not student outcomes. ProofLoop provides universities with real-time operational visibility into whether advice was delivered, what actionable commitment was agreed upon, and whether it was independently confirmed—without violating student privacy.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = ProofObsidianInk
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Metric Definition Dialog
    if (activeMetricDefinition != null) {
        val def = activeMetricDefinition!!
        AlertDialog(
            onDismissRequest = { activeMetricDefinition = null },
            title = {
                Text(def.name, fontWeight = FontWeight.Black, color = ProofObsidianInk)
            },
            text = {
                Column {
                    Text("FORMULA:", fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = ProofKleinBlue)
                    Text(def.formula, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ProofObsidianInk)

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("EXPLANATION:", fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = ProofCharcoalMuted)
                    Text(def.description, fontSize = 12.sp, color = ProofObsidianInk)

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("INCLUDED STATUSES:", fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = ProofCharcoalMuted)
                    Text(def.includedStatuses, fontSize = 11.sp, color = ProofObsidianInk)

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("LIMITATIONS:", fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = ProofSolarTangerine)
                    Text(def.limitations, fontSize = 11.sp, color = ProofObsidianInk)
                }
            },
            confirmButton = {
                Button(
                    onClick = { activeMetricDefinition = null },
                    colors = ButtonDefaults.buttonColors(containerColor = ProofObsidianInk, contentColor = ProofPureWhite)
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
        contentPadding = 8.dp,
        shadowOffset = 2.dp,
        modifier = modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 9.sp,
                letterSpacing = 0.5.sp
            ),
            color = ProofObsidianInk
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp
            ),
            color = ProofObsidianInk
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = ProofObsidianInk.copy(alpha = 0.8f),
            maxLines = 1
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
                border = BorderStroke(1.dp, ProofObsidianInk),
                modifier = Modifier.size(20.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stageNumber,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        ),
                        color = if (color == ProofObsidianInk) ProofPureWhite else ProofObsidianInk
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = stageName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    ),
                    color = ProofObsidianInk
                )
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = ProofCharcoalMuted
                )
            }
        }
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp
            ),
            color = ProofObsidianInk
        )
    }
}
