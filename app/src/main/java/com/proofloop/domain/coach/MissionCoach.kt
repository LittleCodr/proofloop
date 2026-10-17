package com.proofloop.domain.coach

import com.proofloop.data.model.CoachSuggestion
import com.proofloop.data.model.HelpCategories

/**
 * Deterministic local Mission Coach.
 * Analyzes rough or ambiguous student input and formats it into a high-signal,
 * time-bounded mission specification with measurable deliverables.
 *
 * All suggestions are proposed for student review; nothing is silently submitted.
 */
object MissionCoach {

    fun generateCoachingSuggestion(
        rawTitle: String,
        rawDescription: String,
        selectedCategory: String = ""
    ): CoachSuggestion {
        val combinedText = "$rawTitle $rawDescription $selectedCategory".lowercase()

        return when {
            // 1. Resume / CV / LinkedIn
            combinedText.contains("resume") || combinedText.contains("cv") || combinedText.contains("linkedin") ||
            combinedText.contains("ats") || combinedText.contains("bullet") -> {
                CoachSuggestion(
                    refinedTitle = "Resume Audit: Quantified Impact & Role Alignment",
                    refinedProblemStatement = "Current resume lacks measurable outcome framing (XYZ formula) and ATS readability for competitive engineering roles.",
                    suggestedCategory = HelpCategories.RESUME_FEEDBACK,
                    suggestedSkills = listOf("Resume Writing", "Career Strategy", "Impact Metrics"),
                    suggestedOutcome = "Line-by-line review of top 2 projects with rewritten, metric-driven bullet points.",
                    suggestedDurationMinutes = 30,
                    suggestedFollowUpAction = "Rewrite top 3 project bullets using mentor feedback and submit for verification.",
                    coachingNotes = "Tip: High-impact mentors review resumes faster when you link a 1-page PDF and highlight your top 2 target job descriptions."
                )
            }

            // 2. Mock Interview / Coding / LeetCode
            combinedText.contains("mock") || combinedText.contains("interview") || combinedText.contains("algo") ||
            combinedText.contains("leetcode") || combinedText.contains("coding") || combinedText.contains("dsa") -> {
                CoachSuggestion(
                    refinedTitle = "Mock Technical Screen: Algorithmic Breakdown & Edge Cases",
                    refinedProblemStatement = "Seeking live practice articulating time/space tradeoffs under timed interview constraints for tech screening.",
                    suggestedCategory = HelpCategories.MOCK_INTERVIEW,
                    suggestedSkills = listOf("Algorithms", "Data Structures", "Technical Communication"),
                    suggestedOutcome = "40-minute simulation with calibrated scoring rubric covering speed, accuracy, and communication.",
                    suggestedDurationMinutes = 45,
                    suggestedFollowUpAction = "Implement the optimized O(N) solution with comprehensive edge case unit tests on GitHub.",
                    coachingNotes = "Tip: Specify your preferred programming language and whether you want medium LeetCode, system design, or practical pairing."
                )
            }

            // 3. Architecture / Code Review / System Design
            combinedText.contains("architecture") || combinedText.contains("design") || combinedText.contains("system") ||
            combinedText.contains("backend") || combinedText.contains("database") || combinedText.contains("api") ||
            combinedText.contains("code review") || combinedText.contains("refactor") -> {
                CoachSuggestion(
                    refinedTitle = "Architecture Teardown: State Flow & Layer Decoupling",
                    refinedProblemStatement = "Evaluating component boundaries, concurrency hazards, and database schema efficiency in my current application.",
                    suggestedCategory = HelpCategories.CODE_REVIEW,
                    suggestedSkills = listOf("Software Architecture", "Clean Code", "State Management"),
                    suggestedOutcome = "Targeted review of component decoupling, repository pattern boundaries, and error recovery strategies.",
                    suggestedDurationMinutes = 45,
                    suggestedFollowUpAction = "Open a refactoring pull request addressing the identified concurrency or state boundary issue.",
                    coachingNotes = "Tip: Linking a specific GitHub PR or architecture diagram helps mentors give concrete, production-grade feedback."
                )
            }

            // 4. Portfolio / UI / UX / Frontend
            combinedText.contains("portfolio") || combinedText.contains("ui") || combinedText.contains("ux") ||
            combinedText.contains("design") || combinedText.contains("frontend") || combinedText.contains("website") -> {
                CoachSuggestion(
                    refinedTitle = "Portfolio Teardown: Storytelling & Technical Depth",
                    refinedProblemStatement = "Need critical assessment on project presentation, case study storytelling, and UI polish to stand out to hiring managers.",
                    suggestedCategory = HelpCategories.PORTFOLIO_REVIEW,
                    suggestedSkills = listOf("Portfolio Review", "UI/UX", "Case Studies", "Frontend Architecture"),
                    suggestedOutcome = "Actionable critique list of first impressions, visual hierarchy, and technical proof points.",
                    suggestedDurationMinutes = 45,
                    suggestedFollowUpAction = "Deploy updated hero section and case study tradeoffs on live portfolio URL.",
                    coachingNotes = "Tip: Make sure your live demo link works on mobile and your source code is publicly accessible before the session."
                )
            }

            // 5. Startup / Product / Entrepreneurship
            combinedText.contains("startup") || combinedText.contains("product") || combinedText.contains("mvp") ||
            combinedText.contains("founder") || combinedText.contains("business") || combinedText.contains("growth") -> {
                CoachSuggestion(
                    refinedTitle = "MVP Validation: Problem Wedge & Early Distribution",
                    refinedProblemStatement = "Refining value proposition, cutting non-essential MVP features, and defining first 20 customer acquisition channels.",
                    suggestedCategory = HelpCategories.ENTREPRENEURSHIP,
                    suggestedSkills = listOf("Product Strategy", "Go-To-Market", "MVP Scoping", "User Discovery"),
                    suggestedOutcome = "De-scoped MVP matrix and structured customer discovery interview script.",
                    suggestedDurationMinutes = 45,
                    suggestedFollowUpAction = "Conduct 5 structured customer discovery interviews and synthesize key objection themes.",
                    coachingNotes = "Tip: Be prepared to state who your hair-on-fire customer is and why current alternatives fail them."
                )
            }

            // 6. Career Strategy / General Direction (Default Fallback)
            else -> {
                val derivedTitle = if (rawTitle.isNotBlank()) rawTitle.trim() else "Targeted Career Strategy & Role Trajectory"
                CoachSuggestion(
                    refinedTitle = derivedTitle,
                    refinedProblemStatement = if (rawDescription.isNotBlank()) rawDescription.trim() else "Seeking experienced industry guidance to define a high-leverage 6-month skill development plan and navigate role specializations.",
                    suggestedCategory = if (selectedCategory.isNotBlank()) selectedCategory else HelpCategories.CAREER_DIRECTION,
                    suggestedSkills = listOf("Career Strategy", "Industry Insights", "Mentorship"),
                    suggestedOutcome = "Clarified career trajectory roadmap with 2 concrete milestone targets.",
                    suggestedDurationMinutes = 30,
                    suggestedFollowUpAction = "Complete the agreed exploratory milestone and document learning notes.",
                    coachingNotes = "Tip: The more specific your immediate dilemma (e.g. choice between two fields or preparing for first promotion), the more actionable the alumni advice."
                )
            }
        }
    }
}
