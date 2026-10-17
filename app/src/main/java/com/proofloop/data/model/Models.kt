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

    val ALL = listOf(
        PORTFOLIO_REVIEW,
        CODE_REVIEW,
        CAREER_DIRECTION,
        MOCK_INTERVIEW,
        SYSTEM_DESIGN,
        RESUME_FEEDBACK
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
    val meanResponseHours: Double = 1.8
)

