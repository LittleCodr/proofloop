package com.proofloop.domain.matching

import com.proofloop.data.model.HelpRequest
import com.proofloop.data.model.PublicProfile
import com.proofloop.data.model.UserAccount
import kotlin.math.roundToInt

/**
 * Result of evaluating an alumnus against a help request.
 */
data class MatchResult(
    val alumni: PublicProfile,
    val totalScore: Int, // 0 - 100
    val skillOverlapScore: Double,
    val categoryScore: Double,
    val availabilityScore: Double,
    val goalScore: Double,
    val interestScore: Double,
    val matchedSkills: List<String>,
    val reasons: List<String>
)

object MatchingEngine {

    /**
     * Ranks eligible alumni using the specification formula:
     * score = 0.50 * skillOverlap + 0.20 * categoryMatch + 0.15 * availabilityMatch + 0.10 * goalAlignment + 0.05 * interestAlignment
     */
    fun findMatches(
        request: HelpRequest,
        student: UserAccount?,
        candidates: List<PublicProfile>,
        activeRequestsCountMap: Map<String, Int> = emptyMap()
    ): List<MatchResult> {
        val eligibleCandidates = candidates.filter { alumni ->
            // Filter 1: Must be opted-in
            if (!alumni.acceptingRequests) return@filter false
            
            // Filter 2: Must not exceed capacity
            val currentActive = activeRequestsCountMap[alumni.uid] ?: 0
            if (currentActive >= alumni.maxActiveRequests) return@filter false

            true
        }

        return eligibleCandidates.map { alumni ->
            evaluateAlumnus(request, student, alumni)
        }.sortedByDescending { it.totalScore }
    }

    fun evaluateAlumnus(
        request: HelpRequest,
        student: UserAccount?,
        alumni: PublicProfile
    ): MatchResult {
        val reasons = mutableListOf<String>()

        // 1. Skill Overlap (Weight: 0.50)
        val requestedSkillsNormalized = request.requiredSkills
            .map { it.trim().lowercase() }
            .filter { it.isNotEmpty() }
        
        val alumniExpertiseNormalized = alumni.expertise
            .map { it.trim().lowercase() }
            .toSet()

        val matchedSkills = mutableListOf<String>()
        val skillOverlapRatio = if (requestedSkillsNormalized.isEmpty()) {
            0.0
        } else {
            for (req in requestedSkillsNormalized) {
                val matched = alumni.expertise.firstOrNull { it.trim().equals(req, ignoreCase = true) }
                    ?: alumni.expertise.firstOrNull { it.lowercase().contains(req) || req.contains(it.lowercase()) }
                if (matched != null) {
                    matchedSkills.add(matched)
                }
            }
            (matchedSkills.size.toDouble() / requestedSkillsNormalized.size.toDouble()).coerceIn(0.0, 1.0)
        }
        val skillScore = skillOverlapRatio * 100.0
        if (matchedSkills.isNotEmpty()) {
            reasons.add("${matchedSkills.size} of ${requestedSkillsNormalized.size} requested skills match (${matchedSkills.joinToString(", ")})")
        } else {
            reasons.add("No direct requested skills overlap")
        }

        // 2. Category Match (Weight: 0.20)
        val reqCategory = request.category.trim()
        val offersCategory = alumni.helpCategories.any { it.trim().equals(reqCategory, ignoreCase = true) }
        val categoryScore = if (offersCategory) 100.0 else 0.0
        if (offersCategory) {
            reasons.add("Offers $reqCategory")
        } else {
            reasons.add("Does not explicitly list $reqCategory")
        }

        // 3. Availability Match (Weight: 0.15)
        val prefAvailability = request.preferredAvailability.trim().lowercase()
        val availabilityMatch = if (prefAvailability.isEmpty() || alumni.availableSlots.isEmpty()) {
            false
        } else {
            alumni.availableSlots.any { slot ->
                val s = slot.lowercase()
                s.contains(prefAvailability) || prefAvailability.contains(s) ||
                    (prefAvailability.contains("weekend") && s.contains("weekend")) ||
                    (prefAvailability.contains("evening") && s.contains("evening")) ||
                    (prefAvailability.contains("morning") && s.contains("morning"))
            }
        }
        val availabilityScore = if (availabilityMatch) 100.0 else 0.0
        if (availabilityMatch) {
            reasons.add("Availability matches your preference (${request.preferredAvailability})")
        }

        // 4. Goal Alignment (Weight: 0.10)
        val studentGoal = (student?.careerGoals.orEmpty() + " " + request.desiredOutcome).trim().lowercase()
        val alumniContext = (alumni.jobTitle + " " + alumni.company + " " + alumni.expertise.joinToString(" ")).lowercase()
        val goalMatch = if (studentGoal.isBlank()) {
            false
        } else {
            val keywords = studentGoal.split(" ", ",", "-", "/").filter { it.length > 3 }
            keywords.any { alumniContext.contains(it) }
        }
        val goalScore = if (goalMatch) 100.0 else 0.0
        if (goalMatch) {
            reasons.add("Experience aligns with your goal at ${alumni.jobTitle} (${alumni.company})")
        }

        // 5. Interest Alignment (Weight: 0.05)
        val studentInterests = student?.interests?.map { it.trim().lowercase() } ?: emptyList()
        val sharedInterests = if (studentInterests.isEmpty()) {
            emptyList()
        } else {
            studentInterests.filter { interest ->
                alumni.expertise.any { it.lowercase().contains(interest) } ||
                alumni.helpCategories.any { it.lowercase().contains(interest) }
            }
        }
        val interestScore = if (sharedInterests.isNotEmpty()) 100.0 else 0.0
        if (sharedInterests.isNotEmpty()) {
            reasons.add("Shared interest: ${sharedInterests.joinToString(", ")}")
        }

        // Weighted total
        val total = (0.50 * skillScore) +
                    (0.20 * categoryScore) +
                    (0.15 * availabilityScore) +
                    (0.10 * goalScore) +
                    (0.05 * interestScore)

        return MatchResult(
            alumni = alumni,
            totalScore = total.roundToInt().coerceIn(0, 100),
            skillOverlapScore = skillScore,
            categoryScore = categoryScore,
            availabilityScore = availabilityScore,
            goalScore = goalScore,
            interestScore = interestScore,
            matchedSkills = matchedSkills,
            reasons = reasons
        )
    }
}
