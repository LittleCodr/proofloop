package com.proofloop.domain.health

import com.proofloop.data.model.Connection
import com.proofloop.data.model.ConnectionStatus
import com.proofloop.data.model.HelpRequest
import com.proofloop.data.model.LoopHealthCategory
import com.proofloop.data.model.LoopHealthItem
import com.proofloop.data.model.LoopHealthSeverity
import com.proofloop.data.model.Outcome
import com.proofloop.data.model.PublicProfile
import com.proofloop.data.model.UserRole
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Operational intelligence layer that identifies where mentorship interactions
 * need attention, overdue follow-ups, or capacity adjustments.
 *
 * All evaluations are deterministically derived from persisted timestamps and status invariants.
 * Uses empathetic, constructive language without shaming or automated destructive transitions.
 */
object LoopHealthEngine {

    const val RESPONSE_WINDOW_HOURS = 24L
    const val URGENT_RESPONSE_HOURS = 48L

    fun evaluateHealth(
        requests: List<HelpRequest>,
        connections: List<Connection>,
        outcomes: List<Outcome>,
        userRole: UserRole,
        currentUserId: String,
        alumniProfile: PublicProfile? = null
    ): List<LoopHealthItem> {
        val items = mutableListOf<LoopHealthItem>()
        val now = System.currentTimeMillis()

        // 1. Unanswered Requests (>24 hours)
        connections.filter { it.status == ConnectionStatus.PENDING.name }.forEach { conn ->
            val ageHours = (now - conn.createdAt).coerceAtLeast(0L) / (1000 * 60 * 60)
            if (ageHours >= RESPONSE_WINDOW_HOURS) {
                if (conn.studentId == currentUserId) {
                    items.add(
                        LoopHealthItem(
                            id = "unanswered_creator_${conn.id}",
                            connectionId = conn.id,
                            requestId = conn.requestId,
                            title = "Request Awaiting Response (${ageHours}h)",
                            description = "Your mission '${conn.requestTitle}' has been pending with ${conn.alumniName} for over $ageHours hours.",
                            actionPrompt = "You can send a gentle nudge or explore alternative partners if your timeline is urgent.",
                            severity = if (ageHours >= URGENT_RESPONSE_HOURS) LoopHealthSeverity.URGENT else LoopHealthSeverity.ATTENTION,
                            category = LoopHealthCategory.UNANSWERED_REQUEST,
                            ageHours = ageHours,
                            targetRole = userRole,
                            createdAt = conn.createdAt
                        )
                    )
                } else if (conn.alumniId == currentUserId) {
                    items.add(
                        LoopHealthItem(
                            id = "unanswered_partner_${conn.id}",
                            connectionId = conn.id,
                            requestId = conn.requestId,
                            title = "Pending Request Needs Response (${ageHours}h)",
                            description = "${conn.studentName} is awaiting your decision on '${conn.requestTitle}'.",
                            actionPrompt = "Accept to start the session or decline to free up the request slot.",
                            severity = if (ageHours >= URGENT_RESPONSE_HOURS) LoopHealthSeverity.URGENT else LoopHealthSeverity.ATTENTION,
                            category = LoopHealthCategory.UNANSWERED_REQUEST,
                            ageHours = ageHours,
                            targetRole = userRole,
                            createdAt = conn.createdAt
                        )
                    )
                }
            }
        }

        // 2. Accepted Connection Without Scheduled Session (>12 hours)
        connections.filter { it.status == ConnectionStatus.ACCEPTED.name && it.sessionAt.isBlank() }.forEach { conn ->
            val ageHours = (now - conn.updatedAt).coerceAtLeast(0L) / (1000 * 60 * 60)
            items.add(
                LoopHealthItem(
                    id = "unscheduled_${conn.id}",
                    connectionId = conn.id,
                    title = "Session Time Needed",
                    description = "Connection is active for '${conn.requestTitle}', but no meeting time is scheduled yet.",
                    actionPrompt = "Open the workspace to propose or confirm a 30-45m calendar slot.",
                    severity = if (ageHours > 24) LoopHealthSeverity.ATTENTION else LoopHealthSeverity.INFO,
                    category = LoopHealthCategory.UNSCHEDULED_SESSION,
                    ageHours = ageHours,
                    targetRole = userRole,
                    createdAt = conn.updatedAt
                )
            )
        }

        // 3. Active Session Without Agreed Next Action
        connections.filter { it.status == ConnectionStatus.ACCEPTED.name && it.sessionAt.isNotBlank() && it.agreedNextAction.isBlank() }.forEach { conn ->
            items.add(
                LoopHealthItem(
                    id = "missing_action_${conn.id}",
                    connectionId = conn.id,
                    title = "Post-Session Next Action Missing",
                    description = "A successful loop requires one measurable next action following the session.",
                    actionPrompt = "Agree on a concrete deliverable and target due date in the workspace.",
                    severity = LoopHealthSeverity.INFO,
                    category = LoopHealthCategory.MISSING_NEXT_ACTION,
                    targetRole = userRole,
                    createdAt = conn.updatedAt
                )
            )
        }

        // 4. Overdue Next Actions
        outcomes.filter { !it.studentCompleted && it.dueDate.isNotBlank() }.forEach { outcome ->
            val isOverdue = isDatePast(outcome.dueDate)
            if (isOverdue) {
                if (outcome.studentId == currentUserId) {
                    items.add(
                        LoopHealthItem(
                            id = "overdue_creator_${outcome.id}",
                            connectionId = outcome.connectionId,
                            title = "Deliverable Due Date Passed",
                            description = "Your committed next action '${outcome.actionDescription}' was due on ${outcome.dueDate}.",
                            actionPrompt = "Submit your progress when ready or message your partner for a date extension.",
                            severity = LoopHealthSeverity.URGENT,
                            category = LoopHealthCategory.OVERDUE_ACTION,
                            targetRole = userRole,
                            createdAt = outcome.createdAt
                        )
                    )
                } else if (outcome.alumniId == currentUserId) {
                    items.add(
                        LoopHealthItem(
                            id = "overdue_partner_${outcome.id}",
                            connectionId = outcome.connectionId,
                            title = "Follow-Up Pending",
                            description = "The agreed deliverable from ${outcome.actionDescription} was due on ${outcome.dueDate}.",
                            actionPrompt = "Send an encouraging check-in via workspace to unblock your partner.",
                            severity = LoopHealthSeverity.ATTENTION,
                            category = LoopHealthCategory.OVERDUE_ACTION,
                            targetRole = userRole,
                            createdAt = outcome.createdAt
                        )
                    )
                }
            }
        }

        // 5. Completion Awaiting Confirmation
        outcomes.filter { it.studentCompleted && !it.alumniConfirmed }.forEach { outcome ->
            if (outcome.alumniId == currentUserId) {
                items.add(
                    LoopHealthItem(
                        id = "pending_confirm_partner_${outcome.id}",
                        connectionId = outcome.connectionId,
                        title = "Deliverable Awaiting Confirmation",
                        description = "Partner has marked '${outcome.actionDescription}' as completed and submitted deliverable.",
                        actionPrompt = "Review the deliverable and confirm completion to close the loop.",
                        severity = LoopHealthSeverity.URGENT,
                        category = LoopHealthCategory.PENDING_CONFIRMATION,
                        targetRole = userRole,
                        createdAt = outcome.createdAt
                    )
                )
            } else if (outcome.studentId == currentUserId) {
                items.add(
                    LoopHealthItem(
                        id = "pending_confirm_creator_${outcome.id}",
                        connectionId = outcome.connectionId,
                        title = "Deliverable Submitted",
                        description = "Your deliverable '${outcome.actionDescription}' has been logged and is awaiting confirmation.",
                        actionPrompt = "No action required. Your partner has been notified.",
                        severity = LoopHealthSeverity.INFO,
                        category = LoopHealthCategory.PENDING_CONFIRMATION,
                        targetRole = userRole,
                        createdAt = outcome.createdAt
                    )
                )
            }
        }

        // 6. Partner Capacity Status
        if (alumniProfile != null) {
            val activeConnectionsCount = connections.count {
                it.alumniId == currentUserId &&
                (it.status == ConnectionStatus.ACCEPTED.name || it.status == ConnectionStatus.PENDING.name)
            }
            if (activeConnectionsCount >= alumniProfile.maxActiveRequests) {
                items.add(
                    LoopHealthItem(
                        id = "partner_capacity_${currentUserId}",
                        title = "At Maximum Capacity ($activeConnectionsCount/${alumniProfile.maxActiveRequests})",
                        description = "You have reached your active connection limit. New inbound requests will be temporarily paused.",
                        actionPrompt = "Complete or resolve an active mission to open a new slot for upcoming collaborators.",
                        severity = LoopHealthSeverity.INFO,
                        category = LoopHealthCategory.MENTOR_CAPACITY,
                        targetRole = userRole,
                        createdAt = now
                    )
                )
            }
        }


        return items.sortedByDescending { it.severity.ordinal }
    }

    private fun isDatePast(dateStr: String): Boolean {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = format.parse(dateStr)
            date != null && date.time < System.currentTimeMillis() - (24 * 60 * 60 * 1000L)
        } catch (_: Exception) {
            false
        }
    }
}
