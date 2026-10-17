package com.proofloop.domain.blueprint

import com.proofloop.data.model.HelpCategories
import com.proofloop.data.model.HelpRequest
import com.proofloop.data.model.MissionBlueprint
import com.proofloop.data.model.RequestStatus

/**
 * Registry of reusable Mission Blueprints for structured micro-mentorship engagements.
 * Blueprints transform vague student requests into high-signal, time-bounded commitments.
 */
object MissionBlueprintRegistry {

    private val BLUEPRINTS = listOf(
        MissionBlueprint(
            id = "resume_review_v1",
            version = 1,
            title = "Resume & Professional Profile Review",
            summary = "High-impact line-by-line feedback on your engineering or design resume and LinkedIn summary.",
            category = HelpCategories.RESUME_FEEDBACK,
            intendedAudience = "Students targeting internships, new-grad roles, or pivoting tech domains.",
            suggestedDurationMinutes = 30,
            requiredStudentInputs = listOf(
                "Link to 1-page PDF Resume (Google Drive / GitHub)",
                "Top 2 target job roles or company profiles",
                "Specific section you feel least confident about"
            ),
            suggestedAgenda = listOf(
                "00-05m: Alignment on target role criteria and hiring expectations",
                "05-20m: Live walkthrough of project bullets, metric framing (XYZ formula)",
                "20-27m: Formatting, ATS parsing vulnerabilities, and layout clarity",
                "27-30m: Definition of top 3 revised bullets for follow-up"
            ),
            expectedDeliverable = "Annotated bullet point improvements using metric-driven impact verbs.",
            suggestedFollowUpAction = "Rewrite top 3 project bullets using mentor feedback and submit for verification.",
            suggestedDueDays = 3,
            defaultSkills = listOf("Resume Writing", "Career Strategy", "ATS Optimization")
        ),

        MissionBlueprint(
            id = "portfolio_review_v1",
            version = 1,
            title = "Design & Engineering Portfolio Teardown",
            summary = "Critical evaluation of project case studies, visual hierarchy, and technical depth.",
            category = HelpCategories.PORTFOLIO_REVIEW,
            intendedAudience = "Product designers, full-stack developers, and creative technologists with live work.",
            suggestedDurationMinutes = 45,
            requiredStudentInputs = listOf(
                "Live portfolio URL or hosted GitHub Pages link",
                "Target industry tier (Seed startup, Scale-up, or Big Tech)",
                "The core project case study you want to spotlight"
            ),
            suggestedAgenda = listOf(
                "00-10m: First-impression test (navigation speed, 30-second hook)",
                "10-30m: Deep-dive into problem framing, architecture diagrams, and tradeoffs",
                "30-40m: Mobile responsiveness, typography polish, and live demo audit",
                "40-45m: Setting priority list of 2 high-impact fixes"
            ),
            expectedDeliverable = "Structured critique list covering story clarity, proof of ownership, and polish.",
            suggestedFollowUpAction = "Deploy updated hero section and case study tradeoffs on live portfolio.",
            suggestedDueDays = 4,
            defaultSkills = listOf("Portfolio Review", "UI/UX", "Case Studies", "Frontend Architecture")
        ),

        MissionBlueprint(
            id = "mock_interview_v1",
            version = 1,
            title = "Mock Technical & Problem Solving Interview",
            summary = "Simulated 45-minute live technical screen covering algorithms, problem breakdown, and communication.",
            category = HelpCategories.MOCK_INTERVIEW,
            intendedAudience = "Students preparing for upcoming technical screening or onsite interviews.",
            suggestedDurationMinutes = 45,
            requiredStudentInputs = listOf(
                "Target company interview format (LeetCode medium, system design, practical pair-programming)",
                "Preferred programming language (Kotlin, Java, Python, TypeScript)",
                "Comfort level with algorithmic time/space complexity analysis"
            ),
            suggestedAgenda = listOf(
                "00-05m: Interview context setup and problem prompt presentation",
                "05-30m: Candidate live coding, edge case exploration, and complexity analysis",
                "30-40m: Candidate reflection and mentor score breakdown",
                "40-45m: Calibrated rubric feedback: Communication, Technical Accuracy, Speed"
            ),
            expectedDeliverable = "Calibrated scoring rubric and customized practice problem recommendation.",
            suggestedFollowUpAction = "Implement the optimized O(N) solution with unit tests on GitHub.",
            suggestedDueDays = 2,
            defaultSkills = listOf("Algorithms", "Data Structures", "Technical Communication", "Kotlin")
        ),

        MissionBlueprint(
            id = "architecture_review_v1",
            version = 1,
            title = "Software Architecture & Code Teardown",
            summary = "In-depth review of component boundaries, state flow, database models, or backend schemas.",
            category = HelpCategories.CODE_REVIEW,
            intendedAudience = "Engineers building capstones, production side projects, or open-source libraries.",
            suggestedDurationMinutes = 45,
            requiredStudentInputs = listOf(
                "GitHub repository or Pull Request link",
                "Architecture overview or sequence diagram",
                "Current scalability, latency, or concurrency bottleneck"
            ),
            suggestedAgenda = listOf(
                "00-10m: Codebase structure, domain layers, and dependency injection hygiene",
                "10-30m: Concurrency hazards, boundary isolation, and error handling",
                "30-40m: Refactoring proposals for state management and testability",
                "40-45m: Agreeing on the immediate refactoring target"
            ),
            expectedDeliverable = "Architectural review notes highlighting decoupled layers and test boundaries.",
            suggestedFollowUpAction = "Open a refactoring Pull Request addressing the identified concurrency or state boundary issue.",
            suggestedDueDays = 5,
            defaultSkills = listOf("Software Architecture", "Clean Code", "State Management", "System Design")
        ),

        MissionBlueprint(
            id = "career_direction_v1",
            version = 1,
            title = "Strategic Career Direction & Role Navigation",
            summary = "Clear, pragmatic discussion to identify market specialization, navigate offers, or break into tech.",
            category = HelpCategories.CAREER_DIRECTION,
            intendedAudience = "Students deciding between specialties (e.g. Mobile, Distributed Systems, ML) or negotiating offers.",
            suggestedDurationMinutes = 30,
            requiredStudentInputs = listOf(
                "Current academic/work background and upcoming milestone (e.g. graduation date)",
                "2-3 career paths currently under consideration",
                "The primary dilemma or uncertainty blocking your next decision"
            ),
            suggestedAgenda = listOf(
                "00-05m: Honest assessment of market demand and candidate strengths",
                "05-20m: Day-in-the-life realities of contrasting roles and growth trajectories",
                "20-27m: Building a 6-month skill proof strategy",
                "27-30m: Committing to one concrete decision experiment"
            ),
            expectedDeliverable = "Clear 6-month trajectory milestone roadmap with key proof artifacts.",
            suggestedFollowUpAction = "Complete the agreed exploration milestone (e.g. reach out to 2 domain experts or build target prototype).",
            suggestedDueDays = 7,
            defaultSkills = listOf("Career Strategy", "Industry Insights", "Mentorship")
        ),

        MissionBlueprint(
            id = "startup_feedback_v1",
            version = 1,
            title = "Entrepreneurship & MVP Product Validation",
            summary = "Direct founder-to-founder feedback on user problem validation, MVP scoping, and initial distribution.",
            category = HelpCategories.ENTREPRENEURSHIP,
            intendedAudience = "Student founders and hackers launching new products or entering startup competitions.",
            suggestedDurationMinutes = 45,
            requiredStudentInputs = listOf(
                "1-paragraph elevator pitch explaining problem, customer, and wedge",
                "Link to demo, landing page, or prototype",
                "Evidence of user validation or earliest retention signals"
            ),
            suggestedAgenda = listOf(
                "00-10m: Ruthless critique of problem statement: Nice-to-have vs. Hair-on-fire pain",
                "10-25m: MVP scoping: What feature can be cut to launch 2 weeks sooner?",
                "25-35m: First 50 user acquisition channels and distribution mechanics",
                "35-45m: Committing to customer discovery interview goal"
            ),
            expectedDeliverable = "Actionable MVP de-scoping matrix and 7-day customer acquisition test plan.",
            suggestedFollowUpAction = "Execute 5 customer discovery interviews and document verbatim quotes in validation doc.",
            suggestedDueDays = 7,
            defaultSkills = listOf("Product Strategy", "Go-To-Market", "MVP Scoping", "User Discovery")
        )
    )

    fun getAllBlueprints(): List<MissionBlueprint> = BLUEPRINTS

    fun getBlueprintById(id: String): MissionBlueprint? {
        return BLUEPRINTS.find { it.id.equals(id, ignoreCase = true) }
    }

    /**
     * Converts a MissionBlueprint into a pre-filled, editable HelpRequest.
     * The student is always free to adjust every field before submission.
     */
    fun createPrefilledRequest(
        blueprint: MissionBlueprint,
        studentId: String = "",
        studentName: String = ""
    ): HelpRequest {
        val descriptionBuilder = StringBuilder()
        descriptionBuilder.append(blueprint.summary).append("\n\n")
        descriptionBuilder.append("Session Focus:\n")
        blueprint.suggestedAgenda.forEach { agendaItem ->
            descriptionBuilder.append("• ").append(agendaItem).append("\n")
        }
        descriptionBuilder.append("\nPrepared Inputs for Mentor:\n")
        blueprint.requiredStudentInputs.forEach { inputItem ->
            descriptionBuilder.append("[ ] ").append(inputItem).append("\n")
        }

        return HelpRequest(
            id = "",
            studentId = studentId,
            studentName = studentName,
            title = blueprint.title,
            description = descriptionBuilder.toString().trim(),
            category = blueprint.category,
            requiredSkills = blueprint.defaultSkills,
            desiredOutcome = blueprint.expectedDeliverable,
            durationMinutes = blueprint.suggestedDurationMinutes,
            preferredAvailability = "Flexible Weekday Evenings or Weekends",
            status = RequestStatus.OPEN.name,
            blueprintId = blueprint.id,
            blueprintVersion = blueprint.version,
            createdAt = System.currentTimeMillis()
        )
    }
}
