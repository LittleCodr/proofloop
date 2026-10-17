package com.proofloop.data.repository

import com.proofloop.data.model.HelpCategories
import com.proofloop.data.model.PublicProfile
import com.proofloop.data.model.UserAccount
import com.proofloop.data.model.UserRole

object DemoData {

    // Predefined demo accounts for 1-tap hackathon evaluation
    val DEMO_STUDENT = UserAccount(
        uid = "demo_student_aarav",
        name = "Aarav Sharma",
        email = "aarav.student@hackshift.edu",
        role = UserRole.STUDENT.name,
        college = "IIIT Delhi",
        skills = listOf("Kotlin", "Jetpack Compose", "Android Basics", "Git"),
        interests = listOf("Mobile Development", "System Design", "Fintech"),
        careerGoals = "Securing an Android Engineering internship and improving Compose architecture",
        createdAt = System.currentTimeMillis()
    )

    val DEMO_ALUMNI_1 = PublicProfile(
        uid = "demo_alumni_priya",
        displayName = "Priya Sharma",
        role = UserRole.ALUMNI.name,
        college = "IIIT Delhi",
        graduationYear = "2020",
        jobTitle = "Senior Android Architect",
        company = "Google",
        expertise = listOf("Kotlin", "Jetpack Compose", "System Design", "Android Architecture", "Coroutines"),
        helpCategories = listOf(
            HelpCategories.PORTFOLIO_REVIEW,
            HelpCategories.CODE_REVIEW,
            HelpCategories.MOCK_INTERVIEW
        ),
        availableSlots = listOf("Weekends 2-4 PM", "Saturday 11 AM - 1 PM"),
        acceptingRequests = true,
        maxActiveRequests = 3,
        isDemonstrationProfile = true
    )

    val DEMO_ALUMNI_2 = PublicProfile(
        uid = "demo_alumni_arjun",
        displayName = "Arjun Mehta",
        role = UserRole.ALUMNI.name,
        college = "BITS Pilani",
        graduationYear = "2019",
        jobTitle = "Staff Mobile Engineer",
        company = "Uber",
        expertise = listOf("Jetpack Compose", "App Performance", "Coroutines", "Offline Sync"),
        helpCategories = listOf(
            HelpCategories.CODE_REVIEW,
            HelpCategories.CAREER_DIRECTION
        ),
        availableSlots = listOf("Weekday Evenings (7-9 PM)", "Sunday 3-5 PM"),
        acceptingRequests = true,
        maxActiveRequests = 2,
        isDemonstrationProfile = true
    )

    val DEMO_ALUMNI_3 = PublicProfile(
        uid = "demo_alumni_sneha",
        displayName = "Sneha Patel",
        role = UserRole.ALUMNI.name,
        college = "IIIT Hyderabad",
        graduationYear = "2018",
        jobTitle = "Engineering Manager",
        company = "Stripe",
        expertise = listOf("Android", "API Design", "System Design", "Career Mentorship"),
        helpCategories = listOf(
            HelpCategories.CAREER_DIRECTION,
            HelpCategories.MOCK_INTERVIEW,
            HelpCategories.RESUME_FEEDBACK
        ),
        availableSlots = listOf("Saturday Morning (10-12 AM)"),
        acceptingRequests = true,
        maxActiveRequests = 3,
        isDemonstrationProfile = true
    )

    val DEMO_ALUMNI_4 = PublicProfile(
        uid = "demo_alumni_vikram",
        displayName = "Vikram Das",
        role = UserRole.ALUMNI.name,
        college = "DTU",
        graduationYear = "2021",
        jobTitle = "Tech Lead",
        company = "Flipkart",
        expertise = listOf("Kotlin", "Jetpack Compose", "Firebase", "MVVM"),
        helpCategories = listOf(
            HelpCategories.PORTFOLIO_REVIEW,
            HelpCategories.CODE_REVIEW
        ),
        availableSlots = listOf("Sunday 4-6 PM"),
        acceptingRequests = true,
        maxActiveRequests = 3,
        isDemonstrationProfile = true
    )

    val DEMO_FOUNDER_1 = PublicProfile(
        uid = "demo_founder_kunal",
        displayName = "Kunal Varma",
        role = UserRole.FOUNDER.name,
        college = "DTU",
        graduationYear = "2021",
        jobTitle = "Founder & CEO",
        company = "NeoSprint (YC W24)",
        headline = "Founder & CEO @ NeoSprint (YC W24)",
        organization = "NeoSprint Inc.",
        expertise = listOf("Kotlin", "Jetpack Compose", "Startup MVP", "System Design", "Go-To-Market"),
        helpCategories = listOf(
            HelpCategories.FOUNDER_COFOUNDER,
            HelpCategories.ENTREPRENEURSHIP,
            HelpCategories.SYSTEM_DESIGN
        ),
        availableSlots = listOf("Weekday Mornings (9-11 AM)", "Flexible Async"),
        acceptingRequests = true,
        maxActiveRequests = 3,
        isVerifiedProfile = true,
        isDemonstrationProfile = true
    )

    val DEMO_FACULTY_1 = PublicProfile(
        uid = "demo_faculty_radhika",
        displayName = "Dr. Radhika Sen",
        role = UserRole.FACULTY.name,
        college = "IIIT Delhi",
        jobTitle = "Associate Professor & Lab Director",
        company = "IIIT Delhi - Mobile AI Lab",
        department = "Computer Science & Engineering",
        headline = "Associate Professor & Director @ Mobile AI Lab",
        expertise = listOf("Distributed Systems", "Mobile AI", "Research Methodology", "Kotlin", "Systems Architecture"),
        helpCategories = listOf(
            HelpCategories.RESEARCH_COLLABORATION,
            HelpCategories.CODE_REVIEW,
            HelpCategories.CAREER_DIRECTION
        ),
        availableSlots = listOf("Tuesday & Thursday 3-5 PM"),
        acceptingRequests = true,
        maxActiveRequests = 3,
        isVerifiedProfile = true,
        isDemonstrationProfile = true
    )

    val DEMO_RECRUITER_1 = PublicProfile(
        uid = "demo_recruiter_maya",
        displayName = "Maya Thorne",
        role = UserRole.RECRUITER.name,
        college = "Delhi University",
        graduationYear = "2018",
        jobTitle = "Lead Technical Talent Partner",
        company = "Razorpay",
        headline = "Lead Tech Recruiter (Mobile & Platform) @ Razorpay",
        expertise = listOf("Technical Hiring", "Portfolio Assessment", "Interview Prep", "Compensation Benchmarks"),
        helpCategories = listOf(
            HelpCategories.RECRUITMENT_INTERNSHIP,
            HelpCategories.RESUME_FEEDBACK,
            HelpCategories.MOCK_INTERVIEW
        ),
        availableSlots = listOf("Wednesday 2-4 PM", "Friday 3-5 PM"),
        acceptingRequests = true,
        maxActiveRequests = 4,
        isVerifiedProfile = true,
        isDemonstrationProfile = true
    )

    val ALL_DEMO_ALUMNI = listOf(
        DEMO_ALUMNI_1,
        DEMO_ALUMNI_2,
        DEMO_ALUMNI_3,
        DEMO_ALUMNI_4
    )

    val ALL_DEMO_PROFILES = listOf(
        DEMO_ALUMNI_1,
        DEMO_ALUMNI_2,
        DEMO_ALUMNI_3,
        DEMO_ALUMNI_4,
        DEMO_FOUNDER_1,
        DEMO_FACULTY_1,
        DEMO_RECRUITER_1
    )
}

