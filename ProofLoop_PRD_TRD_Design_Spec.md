**PROOFLOOP  /  PRODUCT + TECHNICAL SPECIFICATION** 

# **PROOFLOOP** 

### **Connections should lead somewhere.** 

**PRD  •  TRD  •  UI/UX  •  USER FLOWS  •  FIREBASE  •  24-HOUR BUILD PLAN** 

|**PAPER**<br>**INK**|**COBALT**<br>**ACID LIME**|
|---|---|



HackShift — Cmd + Shift + Create | Offline theme: Alumni Network & Professional Connections 

Platform: Native Android | Kotlin + Jetpack Compose | Firebase Authentication + Cloud Firestore 

Document status: Build specification for a functional 24-hour MVP 

#### **Product thesis** 

ProofLoop is a consent-based micro-mentorship app. Students submit specific, time-bounded requests for help; opted-in alumni accept relevant requests; both parties record the agreed next action and whether it was completed. The product optimizes for useful follow-through, not profile counts. 

HACKSHIFT 2026  •  ANDROID MVP  •  PAGE 1 

**PROOFLOOP  /  PRODUCT + TECHNICAL SPECIFICATION** 

## **1. Executive Summary** 

Problem: Networking often stops at discovering a profile or sending a generic message. Students need focused help; alumni need control over time, topic and request volume. 

Solution: ProofLoop structures mentorship into Request → Match → Connect → Act → Confirm. It is a workflow layer for focused, opt-in help, not a replacement for LinkedIn or alumni directories. 

|**Element**|**Decision**|
|---|---|
|Primary users|Students seeking focused help; alumni explicitly opting in to provide<br>it.|
|Differentiator|Outcome-oriented micro-mentorship with bounded commitments<br>and recorded next actions.<br>l|
|MVP success|A complete two-account flow works end-to-end with persisted state<br>and access controls.<br>i|
|AI role|Optional request clarification or outreach drafting; not required for<br>core matching.|
|Platform|Native Android,Kotlin,Jetpack Compose,MVVM and Firebase.|



#### **Product principles** 

- Specific over generic: every request has a clear need and desired outcome. 

- Consent over access: alumni opt in and accept requests before private interaction. 

- Explainable over magical: show why an alumnus is recommended. 

- Evidence over vanity: track agreed actions, not raw connection counts. 

- Buildable over bloated: complete the core loop before adding chat, notifications or AI. 

## **2. Product Requirements Document (PRD)** 

#### **2.1 Problem statement** 

Students need relevant, low-friction professional guidance. Existing channels support discovery and messaging, but students still have to formulate a request, find the right person, coordinate a conversation and translate advice into action. Alumni need control over time, topic and request volume. 

#### **2.2 Target users** 

|**Persona**|**Needs**|**MVP behavior**|
|---|---|---|
|Student|Focused feedback, career direction, portfolio<br>review,interviewpreparation|Creates a request, reviews match reasons,<br>sends request and tracks an action.|
|Alumnus/alumna|Clear scope, limited time, relevant requests<br>and control|Opts in, sets expertise/capacity, accepts or<br>declines,confirms interactions.|
|College coordinator (future)|Aggregate participation visibility|Not in MVP; possible pilot administration<br>later.|



#### **2.3 Jobs to be done** 

- When blocked on a career or learning goal, I want to ask for a specific type of help so I can get useful guidance without writing a vague cold message. 

- When I have limited time to mentor, I want to see scope and duration before accepting so I can contribute without an open-ended commitment. 

- After receiving guidance, I want to record the next action so the conversation results in a practical step. 

#### **2.4 Functional requirements** 

**<mark>Requirement Priority Acceptance criterion</mark>** 

**<mark>ID</mark>** 

HACKSHIFT 2026  •  ANDROID MVP  •  PAGE 2 

**PROOFLOOP  /  PRODUCT + TECHNICAL SPECIFICATION** 

|FR-01|Email/password authentication|P0|User signs in and returns to role-<br>aware home.|
|---|---|---|---|
|FR-02|Role-aware profile|P0|Profile persists; client cannot<br>freelychange role.|
|FR-03|Alumni opt-in discovery|P0|Only opted-in alumni are<br>discoverable; private contacts<br>remainprivate.|
|FR-04|Structured help request|P0|Title, description, category,<br>skills, desired outcome,<br>duration,statuspersist.|
|FR-05|Explainable matching|P0|Eligible alumni are ranked with<br>reasons.|
|FR-06|Connection lifecycle|P0|Student sends request; alumnus<br>accepts/declines; both see<br>status.|
|FR-07|Outcome record|P0|Student completion and alumni<br>confirmation are separate.|
|FR-08|UI states|P0|Loading, empty and error states<br>do not crash.|
|FR-09|Push notifications|P1|Only if core flow is stable; in-app<br>status is enough for MVP.|
|FR-10|AI request rewriting|P2|Optional; core app works if AI is<br>unavailable.|



#### **2.5 Non-functional requirements** 

- Security: authorization enforced by Firestore rules, not just UI checks. 

- Reliability: show progress/errors; never silently discard writes. 

- Performance: bounded reads, no unbounded collection scans. 

- Accessibility: readable contrast, scalable text, descriptive controls, ~48dp touch targets. 

- Privacy: collect only needed data; do not expose email/phone by default. 

- Demo resilience: seeded, clearly labelled demo profiles and tested two-account flow. 

#### **2.6 Out of scope** 

Real-time chat, video calls, payments, public feeds, complex reputation scores, job placement guarantees, institutional SSO, automatic alumni-directory imports and AI predictions are excluded from the 24-hour MVP. 

## **3. UX, Information Architecture & User Flows** 

#### **3.1 Navigation** 

AUTH → Sign in / Create account → Role-aware onboarding STUDENT → Home → Create Request → Match Results → Request Status → Outcome Tracker ALUMNI → Profile & Expertise → Availability / Capacity → Incoming Requests → Connection Workspace CONNECTION → Accepted → Agree on next action → Student completion → Alumni confirmation 

#### **3.2 Student flow** 

1. Sign in and complete a short profile. 

2. Tap Request Help. 

3. Enter problem, category, skills, desired outcome, duration and availability. 

4. Review ranked alumni and why each person matches. 

5. Send a request and track pending/accepted/declined status. 

6. After the session, record the agreed next action. 

7. Mark action complete; alumnus may separately confirm the interaction. 

HACKSHIFT 2026  •  ANDROID MVP  •  PAGE 3 

**PROOFLOOP  /  PRODUCT + TECHNICAL SPECIFICATION** 

#### **3.3 Alumni flow** 

8. Create profile and explicitly opt into discovery. 

9. Choose expertise, help categories, availability and maximum active requests. 

10. Review incoming request scope, expected outcome and duration. 

11. Accept or decline; acceptance opens the connection workspace. 

12. Confirm the interaction after the student reports action completion. 

#### **3.4 Screen specifications** 

|**Screen**|**Key components**|**CTA**|**States**|
|---|---|---|---|
|Auth|Logo, email, password, role<br>onboarding|Continue|Validation, auth error, loading|
|Student Home|Active request, pending<br>response,next action|Request help|New/active/error|
|Create Request|Problem, category, skills,<br>outcome,duration,availability|Find matches|Invalid form, submitting|
|Matches|Alumni cards, expertise, match<br>reasons,availability|Send request|No matches, loading, error|
|Alumni Inbox|Request scope, duration,<br>accept/decline|Respond|Empty inbox, stale request|
|Connection|Status, agreed action, session<br>details|Save action|Pending, accepted, declined<br>i|
|Outcome|Action, student completion,<br>alumni confirmation|Mark complete|Awaiting confirmation,<br>completed|



#### **3.5 User-flow diagram** 

Student creates request ↓ validate required fields Load opted-in eligible alumni ↓ Score candidates + generate reasons ↓ Student sends request ↓ Alumnus accepts / declines ↓ accepted Connection workspace → agree on next action ↓ Student marks action complete ↓ Alumnus confirms interaction ↓ Outcome record completed 

## **4. Neo-Brutalist UI Design System** 

#### **4.1 Visual direction** 

Editorial neo-brutalism: warm paper backgrounds, black outlines, hard offset shadows, strong typography, asymmetric compositions and purposeful accents. Avoid generic gradients, glassmorphism, excessive rounded cards, random decoration and childish sticker overload. 

|**Token**|**Value**|**Usage**|
|---|---|---|
|Paper|#F5F1E8|Main background|
|Ink|#111111|Text,borders,dark surfaces|



HACKSHIFT 2026  •  ANDROID MVP  •  PAGE 4 

**PROOFLOOP  /  PRODUCT + TECHNICAL SPECIFICATION** 

|Cobalt|#3155FF|Primaryactions,selected states|
|---|---|---|
|Acid lime|#D7FF3F|Progress and highlighted actions|
|Coral|#FF6B4A|Warnings; pair with explicit labels/icons|
|Lavender|#E9DDFB|Optional secondaryhighlight|



#### **4.2 Typography** 

|**Role**|**Typeface**|**Rules**|
|---|---|---|
|Display|Space Grotesk / sans-serif fallback|Bold,large hierarchy;use sparingly.|
|Body|Inter / system sans-serif|Readable and consistent.|
|Metadata|JetBrains Mono / monospace fallback|IDs,timestamps and status labels.|



#### **4.3 Component rules** 

- Use 1.5–2dp dark outlines on key cards and controls. 

- Use consistent 8–12dp corner radii; avoid rounding everything into pills. 

- Use hard offset shadows on hero cards and key CTAs, not every element. 

- Use an 8dp spacing grid and comfortable mobile margins. 

- Primary CTA: cobalt fill, ink border, bold text and pressed state. 

- Success uses lime plus icon/label; warnings use coral plus text/icon; never rely on color alone. 

- Each card should communicate one key idea, one status and one action. 

- Light theme is the product default; preserve contrast in system bars. 

#### **4.4 UI copy** 

|**Avoid**|**Prefer**|
|---|---|
|Find a mentor|Request focused help|
|AI-powered match|Whythisperson matches|
|Connection successful|Request sent|
|Success score: 98%<br>i|4 of 5 requested skills match<br>i|
|Verified outcome|Student completed;alumnus confirmed|



#### **4.5 Visual assets and wireframes** 

Use vector shapes, typography and purposeful status symbols rather than stock photography. The recurring hero graphic is five bold outlined blocks connected by arrows: REQUEST → MATCH → CONNECT → ACT → CONFIRM. Build MVP visuals in Compose; don't spend core build time on decorative image generation. 

## **5. Technical Requirements Document (TRD)** 

#### **5.1 Technology stack** 

|**Layer**|**Technology**|**Purpose**|
|---|---|---|
|Language|Kotlin|Native Android implementation|
|UI|Jetpack Compose + Material 3|Declarative UI and reusable components|
|Architecture|MVVM + repositories|Separate UI state from data access|
|Navigation|Navigation Compose|Role-aware routing|
|Async|Coroutines + Flow/StateFlow|Async work and observable state|
|Auth|Firebase Authentication|Email/password<br>i|
|Database<br>i|Cloud Firestore|Profiles,requests,connections,outcomes|
|Notifications|Firebase Cloud Messaging (P1)|Push alerts if time allows|
|Testing|JUnit, Compose UI tests, Firebase Emulator<br>Suite|Logic, UI and rules validation|



HACKSHIFT 2026  •  ANDROID MVP  •  PAGE 5 

**PROOFLOOP  /  PRODUCT + TECHNICAL SPECIFICATION** 

#### **5.2 Architecture** 

- Compose Screen → ViewModel (StateFlow<UiState>) → Repository interface → Firebase implementation → Firebase Auth / Cloud Firestore 

- MatchingUseCase: 1. eligibility filtering 2. deterministic weighted score 3. human-readable match explanations 

#### **5.3 Suggested package structure** 

com.proofloop/ MainActivity.kt navigation/ core/ui/theme/ core/ui/components/ feature/auth/ feature/student/ feature/alumni/ feature/requests/ feature/connections/ feature/outcomes/ data/model/ data/repository/ domain/matching/ 

#### **5.4 Firestore data model** 

|**Collection**|**Document ID**|**Key fields**|
|---|---|---|
|users|Firebase UID|uid, name, email, role, college, skills,<br>interests,careerGoals,createdAt|
|publicProfiles|Firebase UID|uid, displayName, role, college,<br>graduationYear, jobTitle, expertise,<br>helpCategories, availableSlots,<br>acceptingRequests,maxActiveRequests|
|helpRequests|Auto ID|studentId, title, description, category,<br>requiredSkills, desiredOutcome,<br>durationMinutes, preferredAvailability,<br>status,createdAt|
|connections|Auto ID|requestId, studentId, alumniId, status,<br>sessionAt, agreedNextAction, createdAt,<br>updatedAt|
|outcomes|Auto ID|connectionId, studentId, alumniId,<br>actionDescription, studentCompleted,<br>alumniConfirmed,createdAt,completedAt|



#### **5.5 Data conventions and queries** 

- Use Firestore Timestamp/serverTimestamp(), not timestamp strings, in production. 

- Normalize skill/category tokens for matching and use enums/sealed classes in Kotlin. 

- Query a bounded set of opted-in alumni, then rank locally for the MVP. 

- Query requests by studentId, connections by studentId/alumniId, and outcomes by connectionId. 

- Add composite indexes only when required by actual queries. 

HACKSHIFT 2026  •  ANDROID MVP  •  PAGE 6 

##### **PROOFLOOP  /  PRODUCT + TECHNICAL SPECIFICATION** 

#### **5.6 Matching algorithm** 

Initial deterministic score: 50% requested-skill overlap, 20% help-category match, 15% availability match, 10% careergoal alignment and 5% shared-interest alignment. These are initial hypotheses, not validated parameters. 

score = 0.50 * skillOverlap + 0.20 * categoryMatch + 0.15 * availabilityMatch + 0.10 * goalAlignment + 0.05 * interestAlignment 

skillOverlap = matched requested skills / requested skills * 100 categoryMatch = 100 if offered, else 0 availabilityMatch = 100 if a preferred slot overlaps, else 0 

Exclude opted-out alumni and those at capacity before scoring. Return a score plus truthful reasons. If goal/interest data is missing, do not pretend it was measured; renormalize active weights or disclose missing signals. 

#### **5.7 Security requirements** 

- Role assignment must be trusted and immutable to the client. 

- Only opted-in alumni profiles may be discoverable; private email/phone stay private. 

- Requests are readable only by the owner and authorized matched/selected alumni. 

- Only the assigned alumnus may accept/decline; student cannot impersonate the alumnus. 

- Student completion and alumni confirmation require separate permissions. 

- Use field allowlists, immutable identity fields and valid status transitions. 

- Test allowed and denied operations with the Firebase Emulator Suite. 

- Never package service-account credentials or ship permissive test rules. 

#### **5.8 Error handling** 

|**Failure**|**Expected behavior**|
|---|---|
|No network|Show retry; preserve form content wherepractical.|
|Auth failure|Show actionable error.|
|No matches|Explain and allow broader criteria.|
|Request alreadyhandled|Refresh and show current state.|
|Permission denied|Show safe error;lognoprivate data.|
|AI/API unavailable|Coreproduct continues working.|



## **6. Firestore Rules: Implementation Notes** 

Security rules must be implemented and tested alongside the schema. A conceptual policy is not enough. Firestore rules are not query filters, and client-only writes are insufficient for some cross-document invariants. 

- Prevent users from assigning themselves the alumni role; use trusted onboarding/backend role assignment. 

- Validate request existence, participant IDs and eligibility before creating a connection. 

- Restrict acceptance/decline to the addressed alumnus and the pending state. 

- Restrict action completion to the student and confirmation to the alumnus. 

- Validate links between requestId, connectionId, studentId and alumniId. 

- For production-sensitive transitions, use trusted Cloud Functions or another authenticated backend. 

- Before using real users, write complete rules with field allowlists and transition checks, then test both permitted and forbidden access in the emulator. Do not deploy placeholder rules as production-ready security. 

HACKSHIFT 2026  •  ANDROID MVP  •  PAGE 7 

**PROOFLOOP  /  PRODUCT + TECHNICAL SPECIFICATION** 

## **7. 24-Hour Implementation Plan** 

|**Time**|**Build**|**Exit criteria**<br>i|
|---|---|---|
|00–02h|Project,Firebase,theme,navigation<br>i|Applaunches;Firebase configworks.<br>i|
|02–05h|Auth + role-awareprofiles|Both roles sign in andpersistprofiles.|
|05–09h|Request creation/list|Student creates and views requests.|
|09–12h|Alumni discovery+ matching|Ranked results and reasons display.|
|12–15h|Connection lifecycle|Alumnus accepts/declines; student sees<br>update.<br>i|
|15–18h|Outcome tracking|Completion and confirmation persist<br>separately.<br>l|
|18–21h|Security, integration, edge cases|Two-account flow works; unauthorized<br>writes rejected.|
|21–24h|Polish,testing,rehearsal|Stable demoplus backupscreenshots.|



#### **Cut line if behind schedule** 

|**Keep**|**Defer**<br>i    i|
|---|---|
|Auth, request, matching, accept/decline, outcome record,|Push notifications, AI rewriting, advanced profile editing, chat, video,|
|authorization<br>l|analytics dashboard|
|Realpersistence and two-account flow|Decorative animations and elaborate onboarding|



#### **Definition of Done** 

- Student can register, create a request and see saved state after reopening. 

- Opted-in alumni are ranked with truthful explanations. 

- Alumnus accepts/declines and student sees updated status. 

- Accepted connection stores an agreed next action. 

- Student completion and alumnus confirmation persist separately. 

- Rules pass positive and negative tests. 

- No secrets or unrestricted rules are shipped. 

- Demo is rehearsed with two accounts and backup screenshots. 

## **8. Pilot Metrics & Validation** 

Do not claim adoption or impact before a pilot. Recruit a small opt-in alumni cohort and test whether requests are specific, manageable and useful. 

|**Metric**|**Definition**|**Purpose**|
|---|---|---|
|Request acceptance rate|Accepted requests / valid requests sent|Tests relevance and willingness.|
|Time to first response|Median time to accept/decline|Tests responsiveness.|
|i<br>Completed interaction rate|Completed interactions / accepted requests|Tests follow-through.|
|Next-action completion rate|Student-marked completed actions /<br>recorded actions|Measures self-reported progress.|
|Mutually confirmed rate|Outcomes confirmed by both / outcomes<br>submitted|Second-party signal, not proof of career<br>success.|



13. Recruit 10–20 alumni who opt in and state what help they offer. 

14. Invite a small student cohort to submit real requests. 

15. Observe match comprehension, request acceptance and follow-through. 

16. Interview both sides and improve request templates/weights based on observed behavior. 

HACKSHIFT 2026  •  ANDROID MVP  •  PAGE 8 

**PROOFLOOP  /  PRODUCT + TECHNICAL SPECIFICATION** 

## **9. Risks & Mitigations** 

|**Risk**|**Impact**|**Mitigation**<br>i|
|---|---|---|
|Cold start|No meaningful matches|Use labelled demo profiles; recruit an opt-in<br>pilot.|
|Generic requests|Poor match quality|Require desired outcome, category, skills<br>and duration.|
|Role abuse / insecure writes|Privacy failure|Trusted role assignment, strict rules,<br>emulator tests, backend for sensitive<br>transitions.|
|False outcome claims|Misleading metrics|Separate self-report and alumni<br>confirmation.<br>i|
|Scope creep|Incomplete MVP|Defer chat, notifications and AI<br>enhancements.<br>i|
|Firebase integration failure|Demo blocked|Configure early; prepare seed data and<br>screenshots.|



## **10. Release & Demo Checklist** 

- Create Firebase project and register Android package. 

- Add google-services.json locally; never commit service-account credentials. 

- Enable Email/Password authentication. 

- Deploy restrictive Firestore rules. 

- Test role assignment and role immutability. 

- Create one student and two opted-in alumni demo accounts. 

- Test request → match → send → accept → action → completion/confirmation. 

- Test denied access from unrelated users. 

- Check text scaling, contrast, loading and small-screen layouts. 

- Rehearse on the actual device/emulator and keep backup screenshots. 

## **Appendix A. Product Copy** 

|**Context**|**Copy**|
|---|---|
|Tagline|Connections should lead somewhere.<br>i|
|Emptyrequests|No open requests. Start with one specific thing you need helpwith.|
|No matches|No strong matches yet. Try fewer required skills or another help<br>category.|
|Request sent|Request sent. You'll see an update when the alumnus responds.|
|Accepted|You're connected. Agree on onepractical next action.<br>i|
|Outcome complete|Action marked complete. The other participant can confirm the<br>interaction.|
|Privacynote|Your contact details stay private unlessyou choose to share them.|



## **Appendix B. Final implementation decisions** 

- Firebase is the MVP backend; avoid adding a separate Node server unless necessary. 

- Use clearly labelled seeded profiles if no real alumni have opted in. 

- AI must not be a dependency for auth, matching, requests or outcomes. 

- Use a trusted backend for production-sensitive transitions and capacity reservation. 

- The MVP records user-reported actions and confirmations; it does not verify employment, skill mastery or career outcomes. 

HACKSHIFT 2026  •  ANDROID MVP  •  PAGE 9 

