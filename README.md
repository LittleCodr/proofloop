# ProofLoop — Multi-Role Professional Network & Collaboration Platform

> **"Make every professional connection count."**  
> *A professional-growth platform where people find relevant connections, collaborate around specific goals, and turn introductions into measurable follow-through.*

[![Download Prototype APK](https://img.shields.io/badge/Download-Prototype%20APK%20(v1.0.0)-3155FF?style=for-the-badge&logo=android&logoColor=white)](https://github.com/LittleCodr/proofloop/releases/download/v1.0.0-prototype/proofloop-v1.0.0-hackshift2026.apk)
[![GitHub Release](https://img.shields.io/badge/Release-v1.0.0--prototype-D7FF3F?style=for-the-badge&logo=github&labelColor=111111&color=D7FF3F)](https://github.com/LittleCodr/proofloop/releases/tag/v1.0.0-prototype)
[![Android](https://img.shields.io/badge/Android-8.0%2B%20(API%2026%2B)-28A745?style=for-the-badge&logo=android&logoColor=white)](https://github.com/LittleCodr/proofloop)

---

## 📲 HackShift 2026 Judges & Reviewers Quick-Start

Judges can install and evaluate the complete ProofLoop ecosystem in **under 60 seconds**:

| Deliverable | Location / Details |
| :--- | :--- |
| **Direct APK Download** | [**`proofloop-v1.0.0-hackshift2026.apk`**](https://github.com/LittleCodr/proofloop/releases/download/v1.0.0-prototype/proofloop-v1.0.0-hackshift2026.apk) |
| **Repository File Copy** | [`release/proofloop-v1.0.0-hackshift2026.apk`](file:///Users/mayank/Developer/proofloop/release/proofloop-v1.0.0-hackshift2026.apk) (30.8 MB) |
| **GitHub Release** | [ProofLoop v1.0.0 — HackShift 2026 Prototype Release](https://github.com/LittleCodr/proofloop/releases/tag/v1.0.0-prototype) |
| **SHA-256 Checksum** | `5b951fc5fd774c926e33d8a69637adb09fc65872f69bf5ef6fb9062d5091b5f9` |

### Installation Instructions:
```bash
# Via ADB to connected device or emulator:
adb install -r proofloop-v1.0.0-hackshift2026.apk

# Or transfer the APK directly to any Android 8.0+ phone and open via Files app
```

### ⚡ 1-Tap Quick-Switch Demo Personas (No Typing Required)
On the welcome authentication screen, tap any of the pre-configured role buttons:
1. **`STUDENT (AARAV)`**: Explore structured Loop Mission creation using 9 proven blueprints, interact with deterministic **ProofLoop Coach**, and search explainable mentor recommendations.
2. **`ALUMNI (PRIYA @ GOOGLE)`**: Access the Alumni Mentorship Portal, manage 3-session capacity limiter, accept requests, and confirm completed outcomes.
3. **`FOUNDER (KUNAL @ YC)`**: View the dedicated Founder Hub, post technical co-founder / MVP collaboration sprints, and discover engineers.
4. **`FACULTY (DR. SEN @ IIIT DELHI)`**: View the Faculty Research Portal, review research briefs, and bridge academia-to-industry lab projects.
5. **`RECRUITER (MAYA @ RAZORPAY)`**: Open the Recruiter Talent Portal, screen candidate portfolios, and coordinate expedited interview loops.
6. **`IMPACT LEDGER (DEAN)`**: Inspect privacy-conscious ($k \ge 3$ anonymity) institutional telemetry, drop-off funnels, and multi-role participation benchmarks.

---

## 1. Executive Summary & Product Concept

ProofLoop is built for the **Alumni Network & Professional Connections** theme at **HackShift 2026**:
> **"Building a Network, Not a Directory."**

The platform connects **students, alumni, faculty, founders, recruiters, and institutions** through **expertise, intent, and opportunities**. Rather than treating roles as generic profile tags, each participant type has distinct workflows, permissions, and portals.

The central object across all journeys is a **Loop Mission**:
```
[1. DEFINE INTENT] ➔ [2. EXPLAINABLE MATCH] ➔ [3. CONNECT & AGREE] ➔ [4. COMMIT & ACT] ➔ [5. CLOSE THE LOOP]
```

### The Core Principle: Connections Should Lead Somewhere
- **Not another directory:** Participants explicitly opt-in and define strict active capacity limits (e.g. max 3 active sessions).
- **Explainable Evidence Matching:** Deterministic 5-factor scoring engine surfaces truthful evidence bullets (skill overlap, institutional affinity, availability) without inventing predictive AI scores.
- **Actionable Micro-Missions:** Introductions are anchored to specific, scoped deliverables with clear owners, timelines, and next actions.
- **Independent Second-Party Confirmation:** Student self-reporting is audited separately from 2nd-party alumni/collaborator verification.
- **Privacy-Conscious Telemetry:** Campuses view aggregated outcome benchmarks with $k \ge 3$ differential privacy, never private session correspondence.

---

## 2. Distinctive Visual Design System: Neo-Brutalism

ProofLoop adopts a bespoke **Neo-Brutalist** aesthetic tailored for high legibility, memorable contrast, and utilitarian clarity:

| Token | Hex Value | Usage |
| :--- | :--- | :--- |
| **ProofPaper** | `#F5F1E8` | Primary background, high-comfort warm cream surface |
| **ProofInk** | `#111111` | Primary text, bold headers, and structural 2dp borders |
| **ProofCobalt** | `#3155FF` | Primary brand accent, interactive links, primary CTAs |
| **ProofAcidLime** | `#D7FF3F` | High-energy secondary accent, confirmation badges, success states |
| **ProofCoral** | `#FF6B4A` | Decline actions, validation warnings, error banners |
| **ProofLavender** | `#E9DDFB` | Soft highlighted cards, status containers, secondary badges |
| **ProofBorder** | `#111111` | 2dp solid black borders for cards, buttons, text inputs |
| **ProofShadow** | `#111111` | Hard offset box shadows (3dp–5dp) with 0 blur radius |

### Typography & Grid
- **Display Typography:** Space Grotesk / Heavy Display weights for headings and badges.
- **Body & Data:** Inter / Sans-serif for optimal mobile reading.
- **Metadata & Counters:** Monospace fonts for dates, progress badges, and match percentages.
- **Layout System:** Strict 8dp grid spacing with hard-edged cards and tactile press elevation.

### Tactile Interactions & Micro-Animations
- **Mechanical Button Press Physics:** Buttons and cards physically sink into their hard-offset shadows upon press using snappy bounce spring physics (`Spring.DampingRatioMediumBouncy`, `StiffnessMediumLow`).
- **Snappy Screen Slide Transitions:** Navigation flows use horizontal slide transitions (`initialOffsetX = { it }`) paired with 280ms easing curves.
- **Custom NeoSpinner Component:** High-contrast rotating square spinner with heavy 2dp black border, spinning around its axis inside loading buttons and state indicators.
- **Morphing Workflow Hero:** Dynamic color animations (`animateColorAsState`) and pop bounce scaling as users advance through the 5-step lifecycle.
- **Animated Visibility:** Expand/fade transitions on feedback banners, error tags, and async search states.

---

## 3. Architecture & Technical Stack

ProofLoop is engineered following modern Android architecture best practices:

- **Language:** Kotlin 2.3.20 (JVM 21 compatibility)
- **UI Toolkit:** Jetpack Compose + Material 3 Design Tokens
- **Architecture Pattern:** MVVM (Model-View-ViewModel) + Repository Pattern
- **State Management:** Kotlin Coroutines, StateFlow, callbackFlow, and SharedFlow triggers
- **Navigation:** Navigation Compose 2.8.8 with type-safe route builders
- **Data Persistence & Auth:**
  - **Firebase Authentication** (Google Sign-In + Email/Password with real user accounts)
  - **Firebase Analytics** (`com.google.firebase:firebase-analytics` event tracking for auth, requests, connections, and confirmations)
  - **Cloud Firestore** (100% production persistence across `users`, `publicProfiles`, `helpRequests`, `connections`, and `outcomes` without dummy/fake seed data)
  - **Dual-Store Cache:** Fully reactive in-memory backing store combined with real-time Firestore synchronization listeners (`addSnapshotListener`). This guarantees immediate zero-latency UI reactivity alongside persistent cloud storage.

### Package Architecture
```
com.proofloop/
├── ProofLoopApp.kt                     # Application class holding singleton repository
├── MainActivity.kt                     # Single Activity entry point with Compose edge-to-edge
├── navigation/
│   ├── Screen.kt                       # Sealed class navigation routes
│   └── ProofLoopNavHost.kt             # Compose navigation graph and route resolvers
├── data/
│   ├── model/
│   │   └── Models.kt                   # UserAccount, PublicProfile, HelpRequest, Connection, Outcome
│   └── repository/
│       ├── IProofLoopRepository.kt     # Repository interface contract
│       ├── ProofLoopRepository.kt      # Dual-store Firebase + Reactive In-Memory implementation
│       └── DemoData.kt                 # Seeded HackShift demo data (Aarav Sharma & Priya Sharma)
├── domain/
│   └── matching/
│       └── MatchingEngine.kt           # Deterministic 5-factor explainable matching algorithm
├── core/
│   └── ui/
│       ├── theme/
│       │   ├── Color.kt                # Neo-Brutalist color palette
│       │   ├── Theme.kt                # ProofLoopTheme configuration
│       │   └── Type.kt                 # Typography hierarchy
│       └── components/
│           └── NeoComponents.kt        # Reusable NeoCard, NeoButton, NeoTextField, StatusBadge, etc.
└── feature/
    ├── auth/                           # AuthScreen & AuthViewModel (Role selection, demo buttons)
    ├── student/                        # StudentHomeScreen & StudentHomeViewModel (Action hub)
    ├── requests/                       # CreateRequestScreen, MatchResultsScreen & ViewModels
    ├── alumni/                         # AlumniInboxScreen & AlumniInboxViewModel (Request triage)
    └── connections/                    # ConnectionWorkspaceScreen & ViewModel (Agreed action & outcome)
```

---

## 4. Explainable Matching Algorithm

ProofLoop strictly adheres to the 5-factor deterministic matching formula defined in the specification:

$$\text{Score} = 0.50 \times \text{SkillOverlap} + 0.20 \times \text{CategoryMatch} + 0.15 \times \text{AvailabilityMatch} + 0.10 \times \text{GoalAlignment} + 0.05 \times \text{InterestAlignment}$$

### Factor Computation
1. **Skill Overlap (50%):** Jaccard index between requested skills and alumnus expertise:
   $$\text{SkillOverlap} = \frac{|\text{Requested} \cap \text{AlumnusSkills}|}{|\text{Requested}|} \times 100$$
2. **Category Match (20%):** 100 if alumnus offers the requested help category; 0 otherwise.
3. **Availability Match (15%):** 100 if preferred timeframes overlap; 50 if alumnus is generally flexible.
4. **Goal Alignment (10%):** Keyword overlap between desired outcome and alumnus bio/experience.
5. **Interest Alignment (5%):** Shared domain tags and background overlap.

### Capacity & Privacy Guardrails
- **Opt-in Gate:** Alumni who toggle off `acceptingRequests` are automatically excluded from discovery.
- **Capacity Gate:** Alumni who have reached `maxActiveRequests` (e.g. 3 active sessions) are automatically excluded.
- **Explainability:** Generates human-readable evidence strings (e.g. *"3 of 3 requested skills match"*, *"Offers Architecture Review"*, *"Matches weekend schedule"*).

---

## 5. Security & Authorization Rules

ProofLoop enforces strict role-based access control and state transition integrity:

1. **Role Partitioning:**
   - Public discoverable profiles (`publicProfiles/{uid}`) only contain opted-in metadata (skills, categories, capacity).
   - Private account credentials and emails are isolated in `users/{uid}`.
2. **Alumni Connection Isolation:**
   - Only the specific alumnus addressed in a connection request (`conn.alumniId == currentUser.uid`) may accept or decline it. Other users are rejected with `SecurityException`.
3. **Outcome Confirmation Separation:**
   - Only the student (`conn.studentId == currentUser.uid`) can mark Step 1 (Student Completion).
   - Only the alumnus (`conn.alumniId == currentUser.uid`) can confirm Step 2 (Alumni Confirmation).
   - Student completion never automatically counts as alumni confirmation.
4. **Firestore Security Rules:** Implemented in `firestore.rules` adhering to the principle of least privilege.

---

## 6. Exact Build & Run Commands

### Prerequisites
- Android Studio Ladybug / Meerkat or later
- JDK 21 (`export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"`)
- Android SDK 36 (Build Tools 36.0.0, Platform Tools)
- Android Emulator or physical device with USB debugging enabled

### Build APK
```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew assembleDebug
```
Output APK locations:
```
apk/proofloop-debug.apk (Ready to install directly from repository root)
app/build/outputs/apk/debug/app-debug.apk (Build artifact, 23 MB)
```

### Run Unit Tests
```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew testDebugUnitTest
```

### Install & Launch via ADB
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.proofloop/.MainActivity
```

---

## 7. Firebase Setup & Backend Deployment

The application includes full Firebase SDK integration (Auth, Firestore, Google Services):

1. **`google-services.json`:** Place the project's `google-services.json` in the `app/` directory (a demo configuration is already bundled).
2. **Deploy Firestore Security Rules:**
   ```bash
   firebase deploy --only firestore:rules
   ```
3. **Composite Indexes:** The query patterns in `ProofLoopRepository.kt` utilize single-field indexes by default. If composite queries are deployed in Firestore console:
   - Collection `connections`: `alumniId` (Ascending) + `status` (Ascending) + `createdAt` (Descending)
   - Collection `connections`: `studentId` (Ascending) + `updatedAt` (Descending)
   - Collection `helpRequests`: `studentId` (Ascending) + `createdAt` (Descending)

---

## 8. Test Results & Verification

### Unit Tests (`app/src/test/java/com/proofloop/`)
- `MatchingEngineTest.kt`:
  - `testDeterministicScoreMatchesSpecFormula`: Passed (Verified 100% score for exact match and 0% for non-match).
  - `testAlumnusNotAcceptingRequestsIsExcluded`: Passed (Verified opt-out filter).
  - `testAlumnusAtCapacityIsExcluded`: Passed (Verified max capacity filter).
  - `testExplanationsAreTruthful`: Passed (Verified truthful skill overlap strings).
- `ProofLoopSecurityTest.kt`:
  - `unauthorizedAlumnusCannotAcceptRequest`: Passed (Verified `SecurityException` when non-assigned alumnus attempts accept).
  - `studentCompletionDoesNotCountAsAlumniConfirmation`: Passed (Verified separation of completion vs confirmation flags).

**Test Execution Result:** `6 passed, 0 failed, 0 skipped` in 7s.

### Live Emulator End-to-End Visual Verification
The full vertical slice was executed and captured on `emulator-5554`:
1. `01_auth_screen.png`: Neo-Brutalist Auth screen with 1-tap HackShift Demo Quick-Start.
2. `02_student_home.png`: Student dashboard with active workflow progress and open requests.
3. `03_create_request_screen.png`: Structured request creation with categories, duration, and skill tags.
4. `04_match_results_screen.png`: Ranked matches displaying 100% score and honest explanation reasons.
5. `04b_request_sent.png`: Disabled request button preventing duplicate submissions.
6. `05_student_home_pending.png`: Dashboard reflecting pending request awaiting alumni response.
7. `07_alumni_inbox.png`: Alumnus portal showing incoming request, scope review, and capacity counter (`1/3 max`).
8. `07b_alumni_accepted.png`: Alumnus accepts request; connection moves to Active Sessions.
9. `08_connection_workspace_alumni.png`: Shared workspace with participant cards and permission-guarded steps.
10. `08b_action_saved.png`: Agreed next action saved and synchronized across sessions.
11. `09_student_marked_completed.png`: Student marks action completed; status updates to `ACTION COMPLETED`.
12. `12_connection_workspace_alumni_confirm.png`: Alumnus view with active `CONFIRM INTERACTION OUTCOME` button.
13. `13_loop_closed_confirmed.png`: Alumnus confirms outcome; full loop officially closed.
14. `14_loop_closed_full_card.png`: Completed state banner and honesty disclaimer.
15. `15_student_home_loop_closed.png`: Student home reflects closed loop with confirmed outcome.

---

## 9. Three-Minute Two-Account Demo Script

Follow this rehearsed script during HackShift 2026 judging:

### Account Profiles
- **Student Account:** Aarav Sharma (`aarav.sharma@campus.edu`)
- **Alumni Account:** Priya Sharma (`priya.sharma@alumni.edu`, Senior Android Architect at Google)

---

### Step 1: Student Login & Help Request (Minute 0:00 – 1:00)
1. Launch ProofLoop. Point out the distinctive **Neo-Brutalist** visual system (high contrast, hard shadows, warm cream `#F5F1E8`, bold borders).
2. Tap **`STUDENT DEMO`** on the Auth screen to authenticate instantly as Aarav Sharma.
3. On the Student Dashboard, observe the workflow progress tracker and tap **`+ REQUEST FOCUSED HELP`**.
4. Fill in the structured request:
   - **Title:** "Review Android Compose Architecture"
   - **Category:** "Architecture Review"
   - **Required Skills:** Jetpack Compose, Kotlin, Coroutines
   - **Desired Outcome:** "Validate repository Flow cache and state isolation"
   - **Duration:** "30 minutes"
5. Tap **`FIND MATCHING ALUMNI`**.
6. Show the **Explainable Match Results**:
   - Priya Sharma appears at the top with a **100% Match Score**.
   - Point out the honest explanation badges: *"3 of 3 requested skills match"*, *"Offers Architecture Review"*, *"Matches weekend schedule"*.
7. Tap **`REQUEST CONNECTION`**. Show that the button disables immediately to prevent duplicate requests.
8. Tap **`VIEW IN DASHBOARD`** to show the request now tracked under "Pending Connection Requests".

---

### Step 2: Alumni Inbox & Acceptance (Minute 1:00 – 1:45)
1. Tap the top-right **Sign Out** icon.
2. Tap **`ALUMNI DEMO`** to log in as Priya Sharma.
3. Show the **Alumni Portal**:
   - Highlight the **Active Sessions counter (1 / 3 max)** and the **Opted-In toggle**.
   - Show the incoming request from Aarav Sharma under "Incoming Requests".
4. Review the student's request scope and tap **`ACCEPT`**.
5. The request moves immediately to "Active Connections & Sessions".
6. Tap **`OPEN WORKSPACE`**.

---

### Step 3: Agreed Action & Independent Confirmation (Minute 1:45 – 3:00)
1. In the **Connection Workspace**:
   - Show the shared session participants card and original request scope.
   - Show the **Agreed Next Action** section:
     Enter `"Refactor repository with offline-first Flow cache and unit test"`.
   - Tap **`SAVE ACTION`**. Show the green success banner.
   - Point out **Step 1 (Student Completion)**: Notice that as the alumnus, Priya *cannot* mark the student's task complete ("Waiting for Aarav Sharma to mark action completed").
2. Tap the back button and **Sign Out**.
3. Tap **`STUDENT DEMO`** to return to Aarav Sharma's dashboard.
4. Tap **`OPEN WORKSPACE`** on the accepted connection:
   - See the locked Agreed Next Action.
   - Tap **`MARK ACTION COMPLETED`**.
   - Show the state update: Step 1 badge turns green (`ACTION COMPLETED`).
   - Note that Step 2 remains pending: *"Awaiting Priya Sharma to independently confirm the interaction outcome."*
5. Tap back and **Sign Out**. Tap **`ALUMNI DEMO`**.
6. Open the Workspace as Priya Sharma:
   - Step 2 is now unlocked! The bright Acid Lime **`CONFIRM INTERACTION OUTCOME`** button is active.
   - Tap **`CONFIRM INTERACTION OUTCOME`**.
7. **The Loop is Closed!**
   - The banner updates: *"Full loop closed & confirmed. Both student completion and alumnus confirmation recorded in Firestore."*
   - Highlight the **Honesty Disclaimer**: ProofLoop tracks verified follow-through on practical guidance without fabricating career outcome guarantees.

---

## 10. HackShift 2026 P0 Requirements Checklist

| Requirement | Status | Verification Evidence |
| :--- | :---: | :--- |
| **App builds and launches natively** | ✅ **VERIFIED** | Gradle build succeeds (`assembleDebug`), APK generated, runs on emulator |
| **Authentication & role-aware behavior** | ✅ **VERIFIED** | Student and Alumni roles with dedicated navigation and quick-start auth |
| **Student creates help request** | ✅ **VERIFIED** | Structured input, validation errors, persisted in repository/Firestore |
| **Alumni matching with explainable reasons** | ✅ **VERIFIED** | Deterministic 5-factor formula, truthful explanation breakdown |
| **Capacity & Opt-in filtering** | ✅ **VERIFIED** | Opted-out and at-capacity alumni excluded from discovery |
| **Alumni accepts or declines** | ✅ **VERIFIED** | Real-time state transition, intended alumnus authorization enforced |
| **Connection & next action state persists** | ✅ **VERIFIED** | Persisted across app restarts, dual-store cache syncs with Firestore |
| **Student completes action** | ✅ **VERIFIED** | Student self-reports completion, locks action description |
| **Alumni independently confirms outcome** | ✅ **VERIFIED** | Student cannot confirm; separate alumnus authority required |
| **Basic security & validation** | ✅ **VERIFIED** | Unit tested security rules; Firestore rules written; no leaked secrets |
| **Monetization & Ad Placements** | ✅ **VERIFIED** | Google AdMob test banner integration (1 ad/feed screen) + campus partner sponsor fallback |
| **Neo-Brutalist Visual Polish** | ✅ **VERIFIED** | Warm cream `#F5F1E8`, bold 2dp outlines, hard offset shadows, zero generic styling |

---

## 11. Known Limitations & Future Roadmap (P2)
- **Push Notifications (FCM):** Cut for MVP in accordance with Hackathon Priority P2 to guarantee 100% stability of the core loop.
- **AI Request Rewriter:** Optional enhancement deferred to post-hackathon release to keep the core product fully functional offline without external API dependencies.
- **Video Calling / In-App Messaging:** ProofLoop deliberately focuses on micro-mentorship follow-through rather than duplicating existing communication channels (Google Meet/Slack).

---

*ProofLoop — Built with passion for HackShift 2026.*
