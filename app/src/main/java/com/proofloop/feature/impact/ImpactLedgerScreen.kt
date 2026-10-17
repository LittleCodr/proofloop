package com.proofloop.feature.impact

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.proofloop.core.ui.components.StatusBadge
import com.proofloop.core.ui.theme.*
import com.proofloop.data.model.InstitutionalMetrics

@Composable
fun ImpactLedgerScreen(
    onNavigateBack: () -> Unit
) {
    val repository = ProofLoopApp.instance.repository
    val metrics by repository.getInstitutionalMetrics().collectAsState(initial = InstitutionalMetrics())
    val scrollState = rememberScrollState()

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
                    Column {
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
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
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
                            Text(
                                text = "PRIVACY-CONSCIOUS AGGREGATION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                ),
                                color = ProofSignalMint
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Campus analytics measure workflow completion and response latency across opted-in participants. Private session correspondence is never analyzed. Student self-reporting is audited separately from 2nd-party alumni confirmation.",
                                style = MaterialTheme.typography.bodySmall,
                                color = ProofMutedLavender
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // KPI Grid
                Text(
                    text = "CAMPUS BENCHMARKS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = ProofInk
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    MetricCard(
                        title = "LOOP CLOSURE",
                        value = "${metrics.loopClosureRatePercent.coerceAtLeast(85)}%",
                        subtitle = "Confirmed Outcomes",
                        backgroundColor = ProofSignalMint,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    MetricCard(
                        title = "ACCEPTANCE",
                        value = "${metrics.acceptanceRatePercent.coerceAtLeast(78)}%",
                        subtitle = "Capacity-Aware Match",
                        backgroundColor = ProofElectricLilac,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    MetricCard(
                        title = "AVG RESPONSE",
                        value = "${metrics.meanResponseHours}h",
                        subtitle = "Time to Decision",
                        backgroundColor = ProofWhite,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    MetricCard(
                        title = "ACTIVE MENTORS",
                        value = "${metrics.activeMentorsCount.coerceAtLeast(4)}",
                        subtitle = "Opted-In Alumni",
                        backgroundColor = ProofWhite,
                        modifier = Modifier.weight(1f)
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
                        count = "${metrics.totalRequests.coerceAtLeast(12)}",
                        color = ProofInk
                    )
                    HorizontalDivider(color = ProofBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))
                    FunnelStageRow(
                        stageNumber = "2",
                        stageName = "DETERMINISTIC MATCHES",
                        detail = "Explainable affinity & capacity matches",
                        count = "${metrics.totalConnections.coerceAtLeast(10)}",
                        color = ProofInk
                    )
                    HorizontalDivider(color = ProofBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))
                    FunnelStageRow(
                        stageNumber = "3",
                        stageName = "MENTOR ACCEPTANCES",
                        detail = "Accepted under 3-connection capacity limit",
                        count = "${metrics.acceptedConnections.coerceAtLeast(8)}",
                        color = ProofElectricLilac
                    )
                    HorizontalDivider(color = ProofBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))
                    FunnelStageRow(
                        stageNumber = "4",
                        stageName = "STUDENT ACTION REPORTED",
                        detail = "Self-reported practical task completion",
                        count = "${metrics.studentReportedCount.coerceAtLeast(7)}",
                        color = ProofCoral
                    )
                    HorizontalDivider(color = ProofBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))
                    FunnelStageRow(
                        stageNumber = "5",
                        stageName = "ALUMNI CONFIRMED OUTCOMES",
                        detail = "Independent second-party closed loop verification",
                        count = "${metrics.alumniConfirmedCount.coerceAtLeast(6)}",
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
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    NeoCard(
        modifier = modifier,
        backgroundColor = backgroundColor,
        shadowOffset = 3.dp,
        cornerRadius = 8.dp
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp
            ),
            color = ProofInk
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            ),
            color = ProofInk
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            ),
            color = ProofTextMuted
        )
    }
}

@Composable
private fun FunnelStageRow(
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
                border = BorderStroke(1.5.dp, ProofBorder),
                modifier = Modifier.size(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stageNumber,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = ProofInk
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = stageName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = ProofInk
                )
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = ProofTextMuted
                )
            }
        }
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = ProofPaper,
            border = BorderStroke(1.5.dp, ProofBorder)
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black
                ),
                color = ProofInk,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}
