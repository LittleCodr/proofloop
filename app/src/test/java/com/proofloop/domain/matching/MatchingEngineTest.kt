package com.proofloop.domain.matching

import com.proofloop.data.model.HelpCategories
import com.proofloop.data.model.HelpRequest
import com.proofloop.data.model.PublicProfile
import com.proofloop.data.model.UserAccount
import com.proofloop.data.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MatchingEngineTest {

    private val sampleStudent = UserAccount(
        uid = "student_1",
        name = "Aarav Sharma",
        email = "aarav@test.edu",
        role = UserRole.STUDENT.name,
        careerGoals = "Android Engineer internship focusing on Kotlin and Jetpack Compose",
        interests = listOf("Mobile Development", "System Design")
    )

    private val sampleRequest = HelpRequest(
        id = "req_1",
        studentId = "student_1",
        title = "Compose Architecture Review",
        description = "Need feedback on clean architecture and state hoisting",
        category = HelpCategories.PORTFOLIO_REVIEW,
        requiredSkills = listOf("Kotlin", "Jetpack Compose", "System Design", "Coroutines"),
        desiredOutcome = "Concrete list of architecture refactoring steps",
        durationMinutes = 30,
        preferredAvailability = "Weekends 2-4 PM"
    )

    private val highMatchAlumni = PublicProfile(
        uid = "alumni_priya",
        displayName = "Priya Sharma",
        role = UserRole.ALUMNI.name,
        jobTitle = "Senior Android Architect",
        company = "Google",
        college = "IIIT Delhi",
        expertise = listOf("Kotlin", "Jetpack Compose", "System Design", "Coroutines", "Android"),
        helpCategories = listOf(HelpCategories.PORTFOLIO_REVIEW, HelpCategories.CODE_REVIEW),
        availableSlots = listOf("Weekends 2-4 PM"),
        acceptingRequests = true,
        maxActiveRequests = 3
    )

    private val partialMatchAlumni = PublicProfile(
        uid = "alumni_bob",
        displayName = "Bob Singh",
        role = UserRole.ALUMNI.name,
        jobTitle = "Backend Engineer",
        company = "Acme",
        college = "DTU",
        expertise = listOf("Kotlin", "Java"),
        helpCategories = listOf(HelpCategories.CAREER_DIRECTION),
        availableSlots = listOf("Weekday Mornings"),
        acceptingRequests = true,
        maxActiveRequests = 3
    )

    private val optedOutAlumni = highMatchAlumni.copy(
        uid = "alumni_opted_out",
        acceptingRequests = false
    )

    @Test
    fun `matching formula ranks perfect match with high score and truthful reasons`() {
        val result = MatchingEngine.evaluateAlumnus(sampleRequest, sampleStudent, highMatchAlumni)

        // 4 of 4 skills match: 100% * 0.50 = 50.0
        assertEquals(100.0, result.skillOverlapScore, 0.01)

        // Category matches: 100% * 0.20 = 20.0
        assertEquals(100.0, result.categoryScore, 0.01)

        // Availability matches: 100% * 0.15 = 15.0
        assertEquals(100.0, result.availabilityScore, 0.01)

        // Total score should be >= 85
        assertTrue(result.totalScore >= 85)

        // Truthful reasons verification
        assertTrue(result.reasons.any { it.contains("4 of 4 requested skills match") })
        assertTrue(result.reasons.any { it.contains("Offers Portfolio Review") })
        assertTrue(result.reasons.any { it.contains("Availability matches your preference") })
    }

    @Test
    fun `matching formula calculates partial overlap accurately without fabricating score`() {
        val result = MatchingEngine.evaluateAlumnus(sampleRequest, sampleStudent, partialMatchAlumni)

        // 1 of 4 skills match (Kotlin): 25.0%
        assertEquals(25.0, result.skillOverlapScore, 0.01)

        // Category does not match: 0.0
        assertEquals(0.0, result.categoryScore, 0.01)

        // Availability does not match: 0.0
        assertEquals(0.0, result.availabilityScore, 0.01)

        // Total score reflects only genuine match components
        assertTrue(result.totalScore < 50)
        assertTrue(result.reasons.any { it.contains("1 of 4 requested skills match") })
    }

    @Test
    fun `opted-out alumni are excluded from match candidates`() {
        val matches = MatchingEngine.findMatches(
            request = sampleRequest,
            student = sampleStudent,
            candidates = listOf(highMatchAlumni, optedOutAlumni)
        )

        assertEquals(1, matches.size)
        assertEquals("alumni_priya", matches.first().alumni.uid)
    }

    @Test
    fun `alumni at maximum active capacity are excluded`() {
        val activeCountMap = mapOf(
            "alumni_priya" to 3 // Priya's maxActiveRequests is 3 -> at capacity
        )

        val matches = MatchingEngine.findMatches(
            request = sampleRequest,
            student = sampleStudent,
            candidates = listOf(highMatchAlumni, partialMatchAlumni),
            activeRequestsCountMap = activeCountMap
        )

        assertEquals(1, matches.size)
        assertEquals("alumni_bob", matches.first().alumni.uid)
    }
}
