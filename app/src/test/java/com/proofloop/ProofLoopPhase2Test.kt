package com.proofloop

import com.proofloop.data.model.Connection
import com.proofloop.data.model.ConnectionStatus
import com.proofloop.data.model.HelpCategories
import com.proofloop.data.model.HelpRequest
import com.proofloop.data.model.LoopHealthCategory
import com.proofloop.data.model.LoopHealthSeverity
import com.proofloop.data.model.MatchFitLevel
import com.proofloop.data.model.Outcome
import com.proofloop.data.model.PublicProfile
import com.proofloop.data.model.SessionStatus
import com.proofloop.data.model.TimeWindowFilter
import com.proofloop.data.model.UserAccount
import com.proofloop.data.model.UserRole
import com.proofloop.domain.blueprint.MissionBlueprintRegistry
import com.proofloop.domain.coach.MissionCoach
import com.proofloop.domain.health.LoopHealthEngine
import com.proofloop.domain.matching.MatchingEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProofLoopPhase2Test {

    // ==========================================
    // 1. PROOFLOOP INTELLIGENCE & EXPLAINABILITY
    // ==========================================

    @Test
    fun `explainable matching provides truthful evidence bullets and accurate fit levels`() {
        val student = UserAccount(
            uid = "student_test_1",
            name = "Test Student",
            college = "Stanford University",
            careerGoals = "Android Developer internship specializing in Kotlin",
            interests = listOf("Mobile Development")
        )

        val request = HelpRequest(
            id = "req_compose",
            studentId = student.uid,
            title = "Jetpack Compose Review",
            category = HelpCategories.CODE_REVIEW,
            requiredSkills = listOf("Kotlin", "Jetpack Compose"),
            desiredOutcome = "Architecture refactoring notes",
            preferredAvailability = "Weekends"
        )

        val candidate = PublicProfile(
            uid = "alumni_1",
            displayName = "Dr. Maya Lin",
            role = UserRole.ALUMNI.name,
            college = "Stanford University",
            jobTitle = "Staff Software Engineer",
            company = "Google",
            expertise = listOf("Kotlin", "Jetpack Compose", "Coroutines"),
            helpCategories = listOf(HelpCategories.CODE_REVIEW),
            availableSlots = listOf("Weekends 2-4 PM"),
            acceptingRequests = true,
            maxActiveRequests = 3,
            isVerifiedAlumni = true
        )

        val matches = MatchingEngine.findExplainableMatches(
            request = request,
            student = student,
            candidates = listOf(candidate)
        )

        assertEquals(1, matches.size)
        val match = matches.first()

        assertEquals(MatchFitLevel.EXCELLENT_FIT, match.fitLevel)
        assertTrue(match.totalScore >= 75)
        assertTrue(match.evidenceBullets.any { it.contains("Matches 2 of 2 requested skills") })
        assertTrue(match.evidenceBullets.any { it.contains("Specializes in Code & Architecture Review") })
        assertTrue(match.evidenceBullets.any { it.contains("Alumni affinity: Both affiliated with Stanford University") })
        assertTrue(match.collegeAffinity)
        assertTrue(match.categoryMatch)
        assertTrue(match.availabilityOverlap)
    }

    @Test
    fun `explainable matching flags missing profile information honestly`() {
        val studentWithoutGoals = UserAccount(
            uid = "student_2",
            name = "Alex",
            careerGoals = "" // missing
        )

        val request = HelpRequest(
            id = "req_2",
            studentId = studentWithoutGoals.uid,
            title = "General Career Advice",
            category = HelpCategories.CAREER_DIRECTION,
            requiredSkills = emptyList() // no skills tagged
        )

        val mentorWithoutSlots = PublicProfile(
            uid = "mentor_2",
            displayName = "Marcus Vance",
            availableSlots = emptyList(), // missing availability
            acceptingRequests = true,
            maxActiveRequests = 3,
            isVerifiedAlumni = true
        )

        val matches = MatchingEngine.findExplainableMatches(
            request = request,
            student = studentWithoutGoals,
            candidates = listOf(mentorWithoutSlots)
        )

        assertEquals(1, matches.size)
        val match = matches.first()
        assertNotNull(match.missingInfoNotice)
        assertTrue(match.missingInfoNotice!!.contains("Mentor has not listed specific weekly hours"))
    }

    @Test
    fun `explainable matching filters unverified or over-capacity mentors`() {
        val request = HelpRequest(id = "req_3", category = HelpCategories.MOCK_INTERVIEW)

        val unverifiedMentor = PublicProfile(
            uid = "unverified_1",
            isVerifiedAlumni = false,
            acceptingRequests = true
        )

        val overCapacityMentor = PublicProfile(
            uid = "busy_1",
            isVerifiedAlumni = true,
            acceptingRequests = true,
            maxActiveRequests = 3
        )

        val activeMap = mapOf("busy_1" to 3) // 3 active requests = full capacity

        val matches = MatchingEngine.findExplainableMatches(
            request = request,
            student = null,
            candidates = listOf(unverifiedMentor, overCapacityMentor),
            activeRequestsCountMap = activeMap
        )

        assertTrue("Unverified and over-capacity mentors must be excluded from recommendations", matches.isEmpty())
    }

    // ==========================================
    // 2. MISSION COACH DETERMINISTIC ADVISOR
    // ==========================================

    @Test
    fun `mission coach transforms vague resume request into crisp action plan`() {
        val suggestion = MissionCoach.generateCoachingSuggestion(
            rawTitle = "help with resume",
            rawDescription = "i want someone to look at my cv"
        )

        assertEquals("Resume Audit: Quantified Impact & Role Alignment", suggestion.refinedTitle)
        assertEquals(HelpCategories.RESUME_FEEDBACK, suggestion.suggestedCategory)
        assertTrue(suggestion.suggestedSkills.contains("Resume Writing"))
        assertTrue(suggestion.suggestedFollowUpAction.contains("Rewrite top 3 project bullets"))
        assertEquals(30, suggestion.suggestedDurationMinutes)
    }

    @Test
    fun `mission coach formats interview prep with rubric and algorithm practice`() {
        val suggestion = MissionCoach.generateCoachingSuggestion(
            rawTitle = "mock interview please",
            rawDescription = "practicing leetcode and dsa for intern test"
        )

        assertEquals(HelpCategories.MOCK_INTERVIEW, suggestion.suggestedCategory)
        assertTrue(suggestion.suggestedSkills.contains("Algorithms"))
        assertEquals(45, suggestion.suggestedDurationMinutes)
        assertTrue(suggestion.suggestedFollowUpAction.contains("optimized O(N) solution"))
    }

    // ==========================================
    // 3. MISSION BLUEPRINT REGISTRY
    // ==========================================

    @Test
    fun `blueprint registry contains all six official reusable templates`() {
        val blueprints = MissionBlueprintRegistry.getAllBlueprints()
        assertEquals(6, blueprints.size)

        val ids = blueprints.map { it.id }
        assertTrue(ids.contains("resume_review_v1"))
        assertTrue(ids.contains("portfolio_review_v1"))
        assertTrue(ids.contains("mock_interview_v1"))
        assertTrue(ids.contains("architecture_review_v1"))
        assertTrue(ids.contains("career_direction_v1"))
        assertTrue(ids.contains("startup_feedback_v1"))

        // Verify prefilled request generation
        val resumeBp = MissionBlueprintRegistry.getBlueprintById("resume_review_v1")!!
        val prefilled = MissionBlueprintRegistry.createPrefilledRequest(resumeBp, "stu_1", "Student Aarav")

        assertEquals("stu_1", prefilled.studentId)
        assertEquals(resumeBp.title, prefilled.title)
        assertEquals(resumeBp.category, prefilled.category)
        assertEquals(resumeBp.id, prefilled.blueprintId)
        assertEquals(resumeBp.version, prefilled.blueprintVersion)
        assertTrue(prefilled.description.contains("Session Focus:"))
    }

    // ==========================================
    // 4. LOOP HEALTH OPERATIONAL INTELLIGENCE
    // ==========================================

    @Test
    fun `loop health detects unanswered requests beyond 24 hours`() {
        val stalePendingConnection = Connection(
            id = "conn_stale",
            studentId = "stu_1",
            alumniId = "alum_1",
            studentName = "Aarav",
            alumniName = "Priya",
            requestTitle = "System Design Review",
            status = ConnectionStatus.PENDING.name,
            createdAt = System.currentTimeMillis() - (26 * 60 * 60 * 1000L) // 26 hours ago
        )

        val studentItems = LoopHealthEngine.evaluateHealth(
            requests = emptyList(),
            connections = listOf(stalePendingConnection),
            outcomes = emptyList(),
            userRole = UserRole.STUDENT,
            currentUserId = "stu_1"
        )

        assertEquals(1, studentItems.size)
        val item = studentItems.first()
        assertEquals(LoopHealthCategory.UNANSWERED_REQUEST, item.category)
        assertEquals(LoopHealthSeverity.ATTENTION, item.severity)
        assertTrue(item.title.contains("Request Awaiting Response (26h)"))

        // For Alumni perspective:
        val alumniItems = LoopHealthEngine.evaluateHealth(
            requests = emptyList(),
            connections = listOf(stalePendingConnection),
            outcomes = emptyList(),
            userRole = UserRole.ALUMNI,
            currentUserId = "alum_1"
        )
        assertEquals(1, alumniItems.size)
        assertTrue(alumniItems.first().actionPrompt.contains("Accept to start the session or decline"))
    }

    @Test
    fun `loop health flags accepted connection without scheduled session`() {
        val unscheduledConnection = Connection(
            id = "conn_accepted_no_session",
            studentId = "stu_1",
            alumniId = "alum_1",
            requestTitle = "Code Review",
            status = ConnectionStatus.ACCEPTED.name,
            sessionAt = "", // no session
            updatedAt = System.currentTimeMillis() - (15 * 60 * 60 * 1000L)
        )

        val items = LoopHealthEngine.evaluateHealth(
            requests = emptyList(),
            connections = listOf(unscheduledConnection),
            outcomes = emptyList(),
            userRole = UserRole.STUDENT,
            currentUserId = "stu_1"
        )

        assertTrue(items.any { it.category == LoopHealthCategory.UNSCHEDULED_SESSION })
    }

    @Test
    fun `loop health alerts alumnus when student completion awaits confirmation`() {
        val pendingConfirmOutcome = Outcome(
            id = "out_1",
            connectionId = "conn_1",
            studentId = "stu_1",
            alumniId = "alum_1",
            actionDescription = "Implement Unit Tests",
            studentCompleted = true,
            alumniConfirmed = false
        )

        val alumniItems = LoopHealthEngine.evaluateHealth(
            requests = emptyList(),
            connections = emptyList(),
            outcomes = listOf(pendingConfirmOutcome),
            userRole = UserRole.ALUMNI,
            currentUserId = "alum_1"
        )

        assertEquals(1, alumniItems.size)
        val alert = alumniItems.first()
        assertEquals(LoopHealthCategory.PENDING_CONFIRMATION, alert.category)
        assertEquals(LoopHealthSeverity.URGENT, alert.severity)
        assertTrue(alert.title.contains("Deliverable Awaiting Confirmation"))
    }

    // ==========================================
    // 5. COMMITMENT ENGINE TRANSITIONS
    // ==========================================

    @Test
    fun `connection model supports timezone-aware scheduling and explicit cancellation`() {
        val conn = Connection(
            id = "conn_test",
            studentId = "stu_1",
            alumniId = "alum_1",
            status = ConnectionStatus.ACCEPTED.name
        )

        val scheduled = conn.copy(
            sessionAt = "Friday 4:00 PM",
            sessionStatus = SessionStatus.CONFIRMED.name,
            timeZone = "America/Los_Angeles"
        )

        assertEquals("Friday 4:00 PM", scheduled.sessionAt)
        assertEquals(SessionStatus.CONFIRMED.name, scheduled.sessionStatus)
        assertEquals("America/Los_Angeles", scheduled.timeZone)

        val cancelled = scheduled.copy(
            status = ConnectionStatus.CANCELLED.name,
            sessionStatus = SessionStatus.CANCELLED.name,
            cancellationReason = "Student accepted offer; feedback no longer needed"
        )

        assertEquals(ConnectionStatus.CANCELLED.name, cancelled.status)
        assertEquals("Student accepted offer; feedback no longer needed", cancelled.cancellationReason)
    }
}
