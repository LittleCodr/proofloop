package com.proofloop.data.model

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

/**
 * User roles in ProofLoop.
 */
enum class UserRole {
    STUDENT,
    ALUMNI
}

/**
 * Lifecycle status of a Help Request.
 */
enum class RequestStatus {
    OPEN,
    MATCHED,
    CLOSED
}

/**
 * Status of a Connection Request.
 */
enum class ConnectionStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    COMPLETED,
    CANCELLED,
    RESCHEDULED
}

/**
 * Lifecycle state of a scheduled mentoring session.
 */
enum class SessionStatus {
    UNSCHEDULED,
    PROPOSED,
    CONFIRMED,
    RESCHEDULED,
    CANCELLED,
    COMPLETED
}

/**
 * Categories of micro-mentorship help offered/requested.
 */
object HelpCategories {
    const val PORTFOLIO_REVIEW = "Portfolio Review"
    const val CODE_REVIEW = "Code & Architecture Review"
    const val CAREER_DIRECTION = "Career Direction"
    const val MOCK_INTERVIEW = "Mock Technical Interview"
    const val SYSTEM_DESIGN = "System Design Guidance"
    const val RESUME_FEEDBACK = "Resume & Profile Feedback"
    const val ENTREPRENEURSHIP = "Entrepreneurship & Startup Feedback"

    val ALL = listOf(
        RESUME_FEEDBACK,
        PORTFOLIO_REVIEW,
        MOCK_INTERVIEW,
        CODE_REVIEW,
        SYSTEM_DESIGN,
        CAREER_DIRECTION,
        ENTREPRENEURSHIP
    )
}

/**
 * Private user account stored in Firestore `users/{uid}`.
 */
@IgnoreExtraProperties
data class UserAccount(
    var uid: String = "",
    var name: String = "",
    var email: String = "",
    var role: String = UserRole.STUDENT.name,
    var college: String = "",
    var skills: List<String> = emptyList(),
    var interests: List<String> = emptyList(),
    var careerGoals: String = "",
    var fcmToken: String? = null,
    var createdAt: Long = System.currentTimeMillis()
)

/**
 * Discoverable public profile for opted-in alumni in `publicProfiles/{uid}`.
 */
@IgnoreExtraProperties
data class PublicProfile(
    var uid: String = "",
    var displayName: String = "",
    var role: String = UserRole.ALUMNI.name,
    var college: String = "",
    var graduationYear: String = "",
    var jobTitle: String = "",
    var company: String = "",
    var expertise: List<String> = emptyList(),
    var helpCategories: List<String> = emptyList(),
    var availableSlots: List<String> = emptyList(),
    var acceptingRequests: Boolean = true,
    var maxActiveRequests: Int = 3,
    var isVerifiedAlumni: Boolean = true,
    var isDemonstrationProfile: Boolean = false
)

/**
 * Structured help request in `helpRequests/{id}`.
 */
@IgnoreExtraProperties
data class HelpRequest(
    var id: String = "",
    var studentId: String = "",
    var studentName: String = "",
    var title: String = "",
    var description: String = "",
    var category: String = "",
    var requiredSkills: List<String> = emptyList(),
    var desiredOutcome: String = "",
    var durationMinutes: Int = 30,
    var preferredAvailability: String = "",
    var status: String = RequestStatus.OPEN.name,
    var blueprintId: String = "",
    var blueprintVersion: Int = 1,
    var createdAt: Long = System.currentTimeMillis()
)

/**
 * Connection between student and alumnus in `connections/{id}`.
 */
@IgnoreExtraProperties
data class Connection(
    var id: String = "",
    var requestId: String = "",
    var requestTitle: String = "",
    var studentId: String = "",
    var studentName: String = "",
    var alumniId: String = "",
    var alumniName: String = "",
    var status: String = ConnectionStatus.PENDING.name,
    var sessionAt: String = "",
    var proposedSessionTime: String = "",
    var sessionStatus: String = SessionStatus.UNSCHEDULED.name,
    var cancellationReason: String = "",
    var timeZone: String = "",
    var agreedNextAction: String = "",
    var nextActionDueDate: String = "",
    var nextActionOwner: String = "",
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
)

/**
 * Outcome record tracking completion and independent confirmation in `outcomes/{id}`.
 */
@IgnoreExtraProperties
data class Outcome(
    var id: String = "",
    var connectionId: String = "",
    var studentId: String = "",
    var alumniId: String = "",
    var actionDescription: String = "",
    var dueDate: String = "",
    var studentCompleted: Boolean = false,
    var alumniConfirmed: Boolean = false,
    var createdAt: Long = System.currentTimeMillis(),
    var completedAt: Long? = null
)

/**
 * Institutional aggregated metrics for university administrators / Impact Ledger.
 */
data class InstitutionalMetrics(
    val totalRequests: Int = 0,
    val totalConnections: Int = 0,
    val acceptedConnections: Int = 0,
    val completedOutcomes: Int = 0,
    val studentReportedCount: Int = 0,
    val alumniConfirmedCount: Int = 0,
    val acceptanceRatePercent: Int = 0,
    val loopClosureRatePercent: Int = 0,
    val activeMentorsCount: Int = 0,
    val meanResponseHours: Double = 1.8,
    val filterWindow: TimeWindowFilter = TimeWindowFilter.ALL_TIME,
    val isSampleCohort: Boolean = false
)

/**
 * Time-window filter for institutional analytics.
 */
enum class TimeWindowFilter(val label: String, val days: Int?) {
    LAST_7_DAYS("Last 7 Days", 7),
    LAST_30_DAYS("Last 30 Days", 30),
    ALL_TIME("All Time", null)
}

/**
 * Reusable Mission Blueprint defining structured micro-mentorship engagements.
 */
data class MissionBlueprint(
    val id: String,
    val version: Int = 1,
    val title: String,
    val summary: String,
    val category: String,
    val intendedAudience: String,
    val suggestedDurationMinutes: Int = 30,
    val requiredStudentInputs: List<String>,
    val suggestedAgenda: List<String>,
    val expectedDeliverable: String,
    val suggestedFollowUpAction: String,
    val suggestedDueDays: Int = 3,
    val defaultSkills: List<String> = emptyList()
)

/**
 * Fitness tier for explainable mentor matching.
 */
enum class MatchFitLevel(val label: String) {
    EXCELLENT_FIT("Excellent Fit"),
    STRONG_FIT("Strong Fit"),
    GOOD_FIT("Good Fit"),
    EXPLORATORY_FIT("Exploratory Match")
}

/**
 * Explainable match result conveying evidence, missing info, and actionable reasons.
 */
data class ExplainableMatch(
    val alumni: PublicProfile,
    val fitLevel: MatchFitLevel,
    val totalScore: Int,
    val evidenceBullets: List<String>,
    val missingInfoNotice: String? = null,
    val matchedSkills: List<String> = emptyList(),
    val categoryMatch: Boolean = false,
    val availabilityOverlap: Boolean = false,
    val collegeAffinity: Boolean = false,
    val activeConnectionsCount: Int = 0
)

/**
 * Severity for Loop Health operational bottlenecks.
 */
enum class LoopHealthSeverity {
    INFO,
    ATTENTION,
    URGENT
}

/**
 * Category of Loop Health bottleneck.
 */
enum class LoopHealthCategory {
    UNANSWERED_REQUEST,
    UNSCHEDULED_SESSION,
    MISSING_NEXT_ACTION,
    OVERDUE_ACTION,
    PENDING_CONFIRMATION,
    MENTOR_CAPACITY
}

/**
 * Operational bottleneck item detected by the Loop Health Engine.
 */
data class LoopHealthItem(
    val id: String,
    val connectionId: String? = null,
    val requestId: String? = null,
    val title: String,
    val description: String,
    val actionPrompt: String,
    val severity: LoopHealthSeverity,
    val category: LoopHealthCategory,
    val ageHours: Long = 0L,
    val targetRole: UserRole = UserRole.STUDENT,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Deterministic suggestion from Mission Coach to turn a vague query into a crisp mission.
 */
data class CoachSuggestion(
    val refinedTitle: String,
    val refinedProblemStatement: String,
    val suggestedCategory: String,
    val suggestedSkills: List<String>,
    val suggestedOutcome: String,
    val suggestedDurationMinutes: Int,
    val suggestedFollowUpAction: String,
    val coachingNotes: String
)
