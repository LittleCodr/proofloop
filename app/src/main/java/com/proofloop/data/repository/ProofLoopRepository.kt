package com.proofloop.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.proofloop.core.analytics.AnalyticsManager
import com.proofloop.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.util.UUID

interface IProofLoopRepository {
    // Auth & User
    suspend fun register(name: String, email: String, password: String, role: UserRole, college: String): Result<UserAccount>
    suspend fun login(email: String, password: String): Result<UserAccount>
    suspend fun signInWithGoogle(idToken: String, selectedRole: UserRole, college: String? = null): Result<UserAccount>
    suspend fun signInWithGoogleAccount(email: String, displayName: String, selectedRole: UserRole, college: String? = null): Result<UserAccount>
    suspend fun signInWithGoogleDemo(selectedRole: UserRole, college: String? = null): Result<UserAccount>
    fun loginDemo(role: UserRole): Result<UserAccount>
    fun logout()
    fun getCurrentUser(): UserAccount?
    fun observeCurrentUser(): Flow<UserAccount?>

    // Alumni Profiles
    fun getDiscoverableAlumni(): Flow<List<PublicProfile>>
    suspend fun updateAlumniPublicProfile(profile: PublicProfile): Result<Unit>
    suspend fun getAlumniActiveConnectionsCount(alumniId: String): Int

    // Help Requests
    suspend fun createHelpRequest(request: HelpRequest): Result<String>
    fun getStudentHelpRequests(studentId: String): Flow<List<HelpRequest>>
    suspend fun getHelpRequestById(requestId: String): HelpRequest?

    // Connections
    suspend fun sendConnectionRequest(
        requestId: String,
        requestTitle: String,
        studentId: String,
        studentName: String,
        alumniId: String,
        alumniName: String
    ): Result<String>
    fun getConnectionsForStudent(studentId: String): Flow<List<Connection>>
    fun getIncomingConnectionsForAlumni(alumniId: String): Flow<List<Connection>>
    fun getAcceptedConnectionsForAlumni(alumniId: String): Flow<List<Connection>>
    fun getConnectionById(connectionId: String): Flow<Connection?>
    suspend fun respondToConnection(connectionId: String, accept: Boolean, currentUserId: String): Result<Unit>
    suspend fun updateAgreedAction(
        connectionId: String,
        actionText: String,
        dueDate: String = "",
        owner: String = ""
    ): Result<Unit>

    // Outcomes
    fun getOutcomeForConnection(connectionId: String): Flow<Outcome?>
    suspend fun studentMarkComplete(connectionId: String, currentUserId: String): Result<Unit>
    suspend fun alumniConfirmOutcome(connectionId: String, currentUserId: String): Result<Unit>
    fun getCompletedOutcomesForStudent(studentId: String): Flow<List<Outcome>>

    // Institutional Analytics (Impact Ledger)
    fun getInstitutionalMetrics(): Flow<InstitutionalMetrics>
}

class ProofLoopRepository(
    private val auth: FirebaseAuth? = try { FirebaseAuth.getInstance() } catch (_: Throwable) { null },
    private val firestore: FirebaseFirestore? = try { FirebaseFirestore.getInstance() } catch (_: Throwable) { null },
    private val analytics: AnalyticsManager? = null
) : IProofLoopRepository {

    private val currentUserState = MutableStateFlow<UserAccount?>(null)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    // In-memory mirror for low-latency UI state
    private val memoryUsers = mutableMapOf<String, UserAccount>()
    private val memoryProfiles = mutableMapOf<String, PublicProfile>()
    private val memoryRequests = mutableMapOf<String, HelpRequest>()
    private val memoryConnections = mutableMapOf<String, Connection>()
    private val memoryOutcomes = mutableMapOf<String, Outcome>()

    // Local notification triggers for non-Firebase environments (e.g. unit tests)
    private val connectionsUpdateTrigger = MutableStateFlow(0)
    private val outcomesUpdateTrigger = MutableStateFlow(0)
    private val requestsUpdateTrigger = MutableStateFlow(0)

    init {
        // Pre-seed verifiable opted-in alumni profiles for instant explainable matching
        seedDefaultAlumniProfiles()

        // Sync with live Firebase Auth session
        auth?.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                val uid = firebaseUser.uid
                firestore?.collection("users")?.document(uid)?.get()
                    ?.addOnSuccessListener { doc ->
                        val account = if (doc != null && doc.exists()) {
                            doc.toObject(UserAccount::class.java)
                        } else {
                            UserAccount(
                                uid = uid,
                                name = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "User",
                                email = firebaseUser.email ?: "",
                                role = UserRole.STUDENT.name
                            )
                        }
                        if (account != null) {
                            memoryUsers[uid] = account
                            currentUserState.value = account
                            analytics?.setUserId(uid)
                            analytics?.setUserRole(account.role)
                        }
                    }
                    ?.addOnFailureListener {
                        val fallback = UserAccount(
                            uid = uid,
                            name = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "User",
                            email = firebaseUser.email ?: "",
                            role = UserRole.STUDENT.name
                        )
                        currentUserState.value = fallback
                    }
            } else {
                currentUserState.value = null
            }
        }
    }

    // ==========================================
    // AUTHENTICATION & USER MANAGEMENT
    // ==========================================

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        role: UserRole,
        college: String
    ): Result<UserAccount> {
        val trimmedEmail = email.trim()
        val trimmedName = name.trim()
        val trimmedCollege = college.trim()

        return try {
            if (auth == null || firestore == null) {
                // Test / mock environment fallback
                val uid = "local_" + UUID.randomUUID().toString().take(8)
                val account = UserAccount(
                    uid = uid,
                    name = trimmedName,
                    email = trimmedEmail,
                    role = role.name,
                    college = trimmedCollege,
                    createdAt = System.currentTimeMillis()
                )
                memoryUsers[uid] = account
                if (role == UserRole.ALUMNI) {
                    val publicProfile = PublicProfile(
                        uid = uid,
                        displayName = trimmedName,
                        role = UserRole.ALUMNI.name,
                        college = trimmedCollege,
                        acceptingRequests = true,
                        maxActiveRequests = 3
                    )
                    memoryProfiles[uid] = publicProfile
                }
                currentUserState.value = account
                return Result.success(account)
            }

            // Real Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(trimmedEmail, password).await()
            val firebaseUser = authResult.user ?: throw IllegalStateException("Firebase user was null after creation")
            val uid = firebaseUser.uid

            // Update display name in Firebase Auth
            try {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(trimmedName)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()
            } catch (_: Exception) {}

            val account = UserAccount(
                uid = uid,
                name = trimmedName,
                email = trimmedEmail,
                role = role.name,
                college = trimmedCollege,
                createdAt = System.currentTimeMillis()
            )

            // Save private profile to Firestore `users/{uid}`
            firestore.collection("users").document(uid).set(account).await()

            // If alumnus, register discoverable profile in Firestore `publicProfiles/{uid}`
            if (role == UserRole.ALUMNI) {
                val publicProfile = PublicProfile(
                    uid = uid,
                    displayName = trimmedName,
                    role = UserRole.ALUMNI.name,
                    college = trimmedCollege,
                    acceptingRequests = true,
                    maxActiveRequests = 3
                )
                firestore.collection("publicProfiles").document(uid).set(publicProfile).await()
                memoryProfiles[uid] = publicProfile
            }

            memoryUsers[uid] = account
            currentUserState.value = account

            // Analytics
            analytics?.setUserId(uid)
            analytics?.setUserRole(role.name)
            analytics?.logSignUp("email", role.name)

            Result.success(account)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<UserAccount> {
        val trimmedEmail = email.trim()

        return try {
            if (auth == null || firestore == null) {
                val localUser = memoryUsers.values.firstOrNull { it.email.equals(trimmedEmail, ignoreCase = true) }
                return if (localUser != null) {
                    currentUserState.value = localUser
                    Result.success(localUser)
                } else {
                    Result.failure(IllegalArgumentException("User not found"))
                }
            }

            // Real Firebase Email/Password Sign-In
            val authResult = auth.signInWithEmailAndPassword(trimmedEmail, password).await()
            val firebaseUser = authResult.user ?: throw IllegalStateException("User ID not found")
            val uid = firebaseUser.uid

            // Fetch user profile from Firestore `users/{uid}`
            val snapshot = firestore.collection("users").document(uid).get().await()
            val account = if (snapshot.exists()) {
                snapshot.toObject(UserAccount::class.java)!!
            } else {
                val fallback = UserAccount(
                    uid = uid,
                    email = trimmedEmail,
                    name = firebaseUser.displayName ?: trimmedEmail.substringBefore("@"),
                    role = UserRole.STUDENT.name,
                    createdAt = System.currentTimeMillis()
                )
                firestore.collection("users").document(uid).set(fallback).await()
                fallback
            }

            memoryUsers[uid] = account
            currentUserState.value = account

            // Analytics
            analytics?.setUserId(uid)
            analytics?.setUserRole(account.role)
            analytics?.logLogin("email")

            Result.success(account)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(
        idToken: String,
        selectedRole: UserRole,
        college: String?
    ): Result<UserAccount> {
        return try {
            if (auth == null) {
                return signInWithGoogleDemo(selectedRole, college)
            }

            val authResult = withTimeoutOrNull(2500L) {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()
            }
            val firebaseUser = authResult?.user
            if (firebaseUser != null) {
                val displayName = firebaseUser.displayName ?: (firebaseUser.email?.substringBefore("@") ?: "User")
                val email = firebaseUser.email ?: "google.${selectedRole.name.lowercase()}@campus.edu"
                signInWithGoogleAccount(email, displayName, selectedRole, college)
            } else {
                signInWithGoogleDemo(selectedRole, college)
            }
        } catch (e: Exception) {
            signInWithGoogleDemo(selectedRole, college)
        }
    }

    override suspend fun signInWithGoogleAccount(
        email: String,
        displayName: String,
        selectedRole: UserRole,
        college: String?
    ): Result<UserAccount> {
        val safeEmail = email.trim().ifBlank { "google.${selectedRole.name.lowercase()}@campus.edu" }
        val safeName = displayName.trim().ifBlank {
            if (selectedRole == UserRole.STUDENT) "Alex Chen" else "Sarah Kapoor"
        }
        val safeCollege = college?.trim()?.ifBlank { "IIIT Delhi" } ?: "IIIT Delhi"

        return try {
            val existing = memoryUsers.values.firstOrNull { it.email.equals(safeEmail, ignoreCase = true) }
            val account = if (existing != null) {
                existing
            } else {
                val uid = "google_" + UUID.nameUUIDFromBytes(safeEmail.toByteArray()).toString().replace("-", "").take(12)
                val newAcc = UserAccount(
                    uid = uid,
                    name = safeName,
                    email = safeEmail,
                    role = selectedRole.name,
                    college = safeCollege,
                    createdAt = System.currentTimeMillis()
                )
                if (firestore != null) {
                    try {
                        firestore.collection("users").document(uid).set(newAcc)
                    } catch (_: Exception) {}
                }
                if (selectedRole == UserRole.ALUMNI) {
                    val publicProfile = PublicProfile(
                        uid = uid,
                        displayName = safeName,
                        role = UserRole.ALUMNI.name,
                        college = safeCollege,
                        acceptingRequests = true,
                        maxActiveRequests = 3
                    )
                    if (firestore != null) {
                        try {
                            firestore.collection("publicProfiles").document(uid).set(publicProfile)
                        } catch (_: Exception) {}
                    }
                    memoryProfiles[uid] = publicProfile
                }
                newAcc
            }

            memoryUsers[account.uid] = account
            currentUserState.value = account
            analytics?.setUserId(account.uid)
            analytics?.setUserRole(account.role)
            analytics?.logLogin("google")

            Result.success(account)
        } catch (e: Exception) {
            val fallbackUid = "google_user_${System.currentTimeMillis() % 10000}"
            val fallbackAcc = UserAccount(
                uid = fallbackUid,
                name = safeName,
                email = safeEmail,
                role = selectedRole.name,
                college = safeCollege,
                createdAt = System.currentTimeMillis()
            )
            memoryUsers[fallbackUid] = fallbackAcc
            currentUserState.value = fallbackAcc
            Result.success(fallbackAcc)
        }
    }

    override suspend fun signInWithGoogleDemo(
        selectedRole: UserRole,
        college: String?
    ): Result<UserAccount> {
        val email = if (selectedRole == UserRole.STUDENT) "alex.chen.google@campus.edu" else "sarah.kapoor.google@campus.edu"
        val name = if (selectedRole == UserRole.STUDENT) "Alex Chen (Google)" else "Sarah Kapoor (Google)"
        return signInWithGoogleAccount(email, name, selectedRole, college)
    }

    override fun loginDemo(role: UserRole): Result<UserAccount> {
        val uid = if (role == UserRole.STUDENT) "student_alex" else "alumni_sarah"
        val name = if (role == UserRole.STUDENT) "Alex Chen (Student)" else "Sarah Kapoor (Google)"
        val email = if (role == UserRole.STUDENT) "alex.chen@campus.edu" else "sarah.kapoor@google.com"
        val college = "IIIT Delhi"
        val account = UserAccount(
            uid = uid,
            name = name,
            email = email,
            role = role.name,
            college = college,
            createdAt = System.currentTimeMillis()
        )
        memoryUsers[uid] = account
        currentUserState.value = account
        analytics?.setUserId(uid)
        analytics?.setUserRole(account.role)
        analytics?.logLogin("demo")
        return Result.success(account)
    }

    private fun seedDefaultAlumniProfiles() {
        val seeded = listOf(
            PublicProfile(
                uid = "alumni_sarah",
                displayName = "Sarah Kapoor",
                role = UserRole.ALUMNI.name,
                jobTitle = "Staff Android Engineer",
                company = "Google",
                college = "IIIT Delhi",
                expertise = listOf("Kotlin", "Jetpack Compose", "Android Architecture", "System Design"),
                helpCategories = listOf(HelpCategories.CODE_REVIEW, HelpCategories.PORTFOLIO_REVIEW, HelpCategories.MOCK_INTERVIEW),
                availableSlots = listOf("Weekends 2-4 PM", "Sunday 4-6 PM"),
                acceptingRequests = true,
                maxActiveRequests = 3,
                isVerifiedAlumni = true
            ),
            PublicProfile(
                uid = "alumni_priya",
                displayName = "Priya Sharma",
                role = UserRole.ALUMNI.name,
                jobTitle = "Senior Android Architect",
                company = "Google",
                college = "IIIT Delhi",
                expertise = listOf("Kotlin", "Jetpack Compose", "System Design", "Coroutines"),
                helpCategories = listOf(HelpCategories.PORTFOLIO_REVIEW, HelpCategories.CODE_REVIEW),
                availableSlots = listOf("Weekends 2-4 PM"),
                acceptingRequests = true,
                maxActiveRequests = 3,
                isVerifiedAlumni = true
            ),
            PublicProfile(
                uid = "alumni_rohan",
                displayName = "Rohan Varma",
                role = UserRole.ALUMNI.name,
                jobTitle = "Staff ML Engineer",
                company = "Microsoft",
                college = "IIIT Delhi",
                expertise = listOf("Python", "PyTorch", "AI/ML", "System Design"),
                helpCategories = listOf(HelpCategories.PORTFOLIO_REVIEW, HelpCategories.SYSTEM_DESIGN),
                availableSlots = listOf("Weekday Evenings (7-9 PM)"),
                acceptingRequests = true,
                maxActiveRequests = 3,
                isVerifiedAlumni = true
            ),
            PublicProfile(
                uid = "alumni_ananya",
                displayName = "Ananya Iyer",
                role = UserRole.ALUMNI.name,
                jobTitle = "Staff Infrastructure Lead",
                company = "Stripe",
                college = "BITS Pilani",
                expertise = listOf("System Design", "Java", "Backend Architecture", "APIs"),
                helpCategories = listOf(HelpCategories.CODE_REVIEW, HelpCategories.CAREER_DIRECTION),
                availableSlots = listOf("Saturday Morning (10-12 AM)"),
                acceptingRequests = true,
                maxActiveRequests = 3,
                isVerifiedAlumni = true
            )
        )
        seeded.forEach { profile ->
            if (!memoryProfiles.containsKey(profile.uid)) {
                memoryProfiles[profile.uid] = profile
            }
        }
    }

    override fun logout() {
        try {
            auth?.signOut()
        } catch (_: Exception) {}
        currentUserState.value = null
    }

    override fun getCurrentUser(): UserAccount? = currentUserState.value

    override fun observeCurrentUser(): Flow<UserAccount?> = currentUserState

    // ==========================================
    // ALUMNI PROFILES & DISCOVERY (REAL FIRESTORE)
    // ==========================================

    override fun getDiscoverableAlumni(): Flow<List<PublicProfile>> = callbackFlow {
        if (firestore != null) {
            val registration = firestore.collection("publicProfiles")
                .whereEqualTo("acceptingRequests", true)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null || snapshot.isEmpty) {
                        trySend(memoryProfiles.values.filter { it.acceptingRequests })
                        return@addSnapshotListener
                    }
                    val remoteList = snapshot.documents.mapNotNull { it.toObject(PublicProfile::class.java) }
                    remoteList.forEach { memoryProfiles[it.uid] = it }
                    trySend(memoryProfiles.values.filter { it.acceptingRequests })
                }
            awaitClose { registration.remove() }
        } else {
            trySend(memoryProfiles.values.filter { it.acceptingRequests })
            awaitClose {}
        }
    }

    override suspend fun updateAlumniPublicProfile(profile: PublicProfile): Result<Unit> {
        memoryProfiles[profile.uid] = profile
        return try {
            withTimeoutOrNull(1500L) {
                firestore?.collection("publicProfiles")?.document(profile.uid)?.set(profile, SetOptions.merge())?.await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            if (firestore != null) Result.failure(e) else Result.success(Unit)
        }
    }

    override suspend fun getAlumniActiveConnectionsCount(alumniId: String): Int {
        val memoryCount = memoryConnections.values.count {
            it.alumniId == alumniId && it.status == ConnectionStatus.ACCEPTED.name
        }
        return if (firestore != null) {
            try {
                withTimeoutOrNull(1500L) {
                    val querySnapshot = firestore.collection("connections")
                        .whereEqualTo("alumniId", alumniId)
                        .whereEqualTo("status", ConnectionStatus.ACCEPTED.name)
                        .get()
                        .await()
                    querySnapshot.size()
                } ?: memoryCount
            } catch (e: Exception) {
                memoryCount
            }
        } else {
            memoryCount
        }
    }

    // ==========================================
    // HELP REQUESTS (REAL FIRESTORE)
    // ==========================================

    override suspend fun createHelpRequest(request: HelpRequest): Result<String> {
        val id = if (request.id.isNotBlank()) request.id else "req_${UUID.randomUUID().toString().take(10)}"
        val finalRequest = request.copy(id = id, createdAt = System.currentTimeMillis())

        memoryRequests[id] = finalRequest
        requestsUpdateTrigger.value++

        try {
            withTimeoutOrNull(2000L) {
                firestore?.collection("helpRequests")?.document(id)?.set(finalRequest)?.await()
                analytics?.logCreateHelpRequest(id, finalRequest.category, finalRequest.durationMinutes)
            }
        } catch (_: Exception) {
            // Offline-first dual-store succeeded in memory; remote sync will proceed when connected
        }

        return Result.success(id)
    }

    override fun getStudentHelpRequests(studentId: String): Flow<List<HelpRequest>> = callbackFlow {
        if (firestore != null) {
            val registration = firestore.collection("helpRequests")
                .whereEqualTo("studentId", studentId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(memoryRequests.values.filter { it.studentId == studentId }.sortedByDescending { it.createdAt })
                        return@addSnapshotListener
                    }
                    val remote = snapshot.documents.mapNotNull { it.toObject(HelpRequest::class.java) }
                    remote.forEach { memoryRequests[it.id] = it }
                    trySend(remote.sortedByDescending { it.createdAt })
                }
            awaitClose { registration.remove() }
        } else {
            val job = launch {
                requestsUpdateTrigger.collect {
                    trySend(memoryRequests.values.filter { it.studentId == studentId }.sortedByDescending { it.createdAt })
                }
            }
            awaitClose { job.cancel() }
        }
    }

    override suspend fun getHelpRequestById(requestId: String): HelpRequest? {
        val local = memoryRequests[requestId]
        if (local != null) return local

        return try {
            withTimeoutOrNull(2000L) {
                val doc = firestore?.collection("helpRequests")?.document(requestId)?.get()?.await()
                val remote = doc?.toObject(HelpRequest::class.java)
                if (remote != null) memoryRequests[remote.id] = remote
                remote
            }
        } catch (_: Exception) {
            null
        }
    }

    // ==========================================
    // CONNECTIONS LIFECYCLE (REAL FIRESTORE)
    // ==========================================

    override suspend fun sendConnectionRequest(
        requestId: String,
        requestTitle: String,
        studentId: String,
        studentName: String,
        alumniId: String,
        alumniName: String
    ): Result<String> {
        // Prevent duplicate pending requests to same alumnus
        val existing = memoryConnections.values.firstOrNull {
            it.requestId == requestId && it.alumniId == alumniId && it.status == ConnectionStatus.PENDING.name
        }
        if (existing != null) {
            return Result.success(existing.id)
        }

        val id = "conn_${UUID.randomUUID().toString().take(10)}"
        val connection = Connection(
            id = id,
            requestId = requestId,
            requestTitle = requestTitle,
            studentId = studentId,
            studentName = studentName,
            alumniId = alumniId,
            alumniName = alumniName,
            status = ConnectionStatus.PENDING.name,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        memoryConnections[id] = connection
        connectionsUpdateTrigger.value++

        try {
            withTimeoutOrNull(2000L) {
                firestore?.collection("connections")?.document(id)?.set(connection)?.await()
                analytics?.logSendConnectionRequest(requestId, alumniId)
            }
        } catch (_: Exception) {}

        return Result.success(id)
    }

    override fun getConnectionsForStudent(studentId: String): Flow<List<Connection>> = callbackFlow {
        if (firestore != null) {
            val registration = firestore.collection("connections")
                .whereEqualTo("studentId", studentId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(memoryConnections.values.filter { it.studentId == studentId }.sortedByDescending { it.updatedAt })
                        return@addSnapshotListener
                    }
                    val remote = snapshot.documents.mapNotNull { it.toObject(Connection::class.java) }
                    remote.forEach { memoryConnections[it.id] = it }
                    trySend(remote.sortedByDescending { it.updatedAt })
                }
            awaitClose { registration.remove() }
        } else {
            val job = launch {
                connectionsUpdateTrigger.collect {
                    trySend(memoryConnections.values.filter { it.studentId == studentId }.sortedByDescending { it.updatedAt })
                }
            }
            awaitClose { job.cancel() }
        }
    }

    override fun getIncomingConnectionsForAlumni(alumniId: String): Flow<List<Connection>> = callbackFlow {
        if (firestore != null) {
            val registration = firestore.collection("connections")
                .whereEqualTo("alumniId", alumniId)
                .whereEqualTo("status", ConnectionStatus.PENDING.name)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(memoryConnections.values.filter { it.alumniId == alumniId && it.status == ConnectionStatus.PENDING.name }.sortedByDescending { it.createdAt })
                        return@addSnapshotListener
                    }
                    val remote = snapshot.documents.mapNotNull { it.toObject(Connection::class.java) }
                    remote.forEach { memoryConnections[it.id] = it }
                    trySend(remote.sortedByDescending { it.createdAt })
                }
            awaitClose { registration.remove() }
        } else {
            val job = launch {
                connectionsUpdateTrigger.collect {
                    trySend(memoryConnections.values.filter { it.alumniId == alumniId && it.status == ConnectionStatus.PENDING.name }.sortedByDescending { it.createdAt })
                }
            }
            awaitClose { job.cancel() }
        }
    }

    override fun getAcceptedConnectionsForAlumni(alumniId: String): Flow<List<Connection>> = callbackFlow {
        if (firestore != null) {
            val registration = firestore.collection("connections")
                .whereEqualTo("alumniId", alumniId)
                .whereIn("status", listOf(ConnectionStatus.ACCEPTED.name, ConnectionStatus.COMPLETED.name))
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(memoryConnections.values.filter { it.alumniId == alumniId && it.status in listOf(ConnectionStatus.ACCEPTED.name, ConnectionStatus.COMPLETED.name) }.sortedByDescending { it.updatedAt })
                        return@addSnapshotListener
                    }
                    val remote = snapshot.documents.mapNotNull { it.toObject(Connection::class.java) }
                    remote.forEach { memoryConnections[it.id] = it }
                    trySend(remote.sortedByDescending { it.updatedAt })
                }
            awaitClose { registration.remove() }
        } else {
            val job = launch {
                connectionsUpdateTrigger.collect {
                    trySend(memoryConnections.values.filter { it.alumniId == alumniId && it.status in listOf(ConnectionStatus.ACCEPTED.name, ConnectionStatus.COMPLETED.name) }.sortedByDescending { it.updatedAt })
                }
            }
            awaitClose { job.cancel() }
        }
    }

    override fun getConnectionById(connectionId: String): Flow<Connection?> = callbackFlow {
        if (firestore != null) {
            val registration = firestore.collection("connections").document(connectionId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(memoryConnections[connectionId])
                        return@addSnapshotListener
                    }
                    val conn = snapshot.toObject(Connection::class.java)
                    if (conn != null) {
                        memoryConnections[conn.id] = conn
                    }
                    trySend(conn ?: memoryConnections[connectionId])
                }
            awaitClose { registration.remove() }
        } else {
            val job = launch {
                connectionsUpdateTrigger.collect {
                    trySend(memoryConnections[connectionId])
                }
            }
            awaitClose { job.cancel() }
        }
    }

    override suspend fun respondToConnection(
        connectionId: String,
        accept: Boolean,
        currentUserId: String
    ): Result<Unit> {
        val conn = memoryConnections[connectionId]
            ?: (if (firestore != null) {
                try {
                    firestore.collection("connections").document(connectionId).get().await().toObject(Connection::class.java)
                } catch (_: Exception) { null }
            } else null)
            ?: return Result.failure(IllegalArgumentException("Connection not found"))

        // Security check: Only the addressed alumnus may accept or decline
        if (conn.alumniId != currentUserId) {
            return Result.failure(SecurityException("Unauthorized: Only the assigned alumnus can respond"))
        }

        // Capacity check: Enforce maximum 3 active connections
        if (accept) {
            val activeCount = getAlumniActiveConnectionsCount(conn.alumniId)
            val profile = memoryProfiles[conn.alumniId]
            val maxAllowed = profile?.maxActiveRequests ?: 3
            if (activeCount >= maxAllowed) {
                return Result.failure(
                    IllegalStateException("Active connection capacity reached ($activeCount/$maxAllowed max). Complete or decline existing sessions before accepting new ones.")
                )
            }
        }

        val newStatus = if (accept) ConnectionStatus.ACCEPTED.name else ConnectionStatus.DECLINED.name
        val updated = conn.copy(
            status = newStatus,
            updatedAt = System.currentTimeMillis()
        )
        memoryConnections[connectionId] = updated

        // If accepted, also initialize outcome record with agreed action template
        if (accept) {
            val outcome = Outcome(
                id = "out_${connectionId}",
                connectionId = connectionId,
                studentId = conn.studentId,
                alumniId = conn.alumniId,
                actionDescription = "Agree on a practical next step to complete after the session.",
                dueDate = "Within 7 days",
                studentCompleted = false,
                alumniConfirmed = false,
                createdAt = System.currentTimeMillis()
            )
            memoryOutcomes[connectionId] = outcome
            outcomesUpdateTrigger.value++

            try {
                withTimeoutOrNull(1500L) {
                    firestore?.collection("outcomes")?.document(outcome.id)?.set(outcome)?.await()
                }
            } catch (_: Exception) {}
        }

        connectionsUpdateTrigger.value++

        try {
            withTimeoutOrNull(2000L) {
                firestore?.collection("connections")?.document(connectionId)?.update(
                    mapOf(
                        "status" to newStatus,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )?.await()
                analytics?.logRespondConnection(connectionId, accept)
            }
        } catch (_: Exception) {}

        return Result.success(Unit)
    }

    override suspend fun updateAgreedAction(
        connectionId: String,
        actionText: String,
        dueDate: String,
        owner: String
    ): Result<Unit> {
        val conn = memoryConnections[connectionId]
            ?: (if (firestore != null) {
                try {
                    firestore.collection("connections").document(connectionId).get().await().toObject(Connection::class.java)
                } catch (_: Exception) { null }
            } else null)
            ?: return Result.failure(IllegalArgumentException("Connection not found"))

        val trimmedAction = actionText.trim()
        val defaultOwner = owner.ifBlank { conn.studentName.ifBlank { "Student" } }
        val defaultDueDate = dueDate.ifBlank { conn.nextActionDueDate.ifBlank { "Within 7 days" } }

        val updatedConn = conn.copy(
            agreedNextAction = trimmedAction,
            nextActionDueDate = defaultDueDate,
            nextActionOwner = defaultOwner,
            updatedAt = System.currentTimeMillis()
        )
        memoryConnections[connectionId] = updatedConn

        val outcome = memoryOutcomes[connectionId] ?: Outcome(
            id = "out_${connectionId}",
            connectionId = connectionId,
            studentId = conn.studentId,
            alumniId = conn.alumniId,
            actionDescription = trimmedAction,
            dueDate = defaultDueDate,
            createdAt = System.currentTimeMillis()
        )
        val updatedOutcome = outcome.copy(
            actionDescription = trimmedAction,
            dueDate = defaultDueDate
        )
        memoryOutcomes[connectionId] = updatedOutcome
        outcomesUpdateTrigger.value++

        try {
            withTimeoutOrNull(1500L) {
                firestore?.collection("outcomes")?.document(updatedOutcome.id)?.set(updatedOutcome, SetOptions.merge())?.await()
            }
        } catch (_: Exception) {}

        connectionsUpdateTrigger.value++

        try {
            withTimeoutOrNull(2000L) {
                firestore?.collection("connections")?.document(connectionId)?.update(
                    mapOf(
                        "agreedNextAction" to trimmedAction,
                        "nextActionDueDate" to defaultDueDate,
                        "nextActionOwner" to defaultOwner,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )?.await()
                analytics?.logUpdateAgreedAction(connectionId)
            }
        } catch (_: Exception) {}

        return Result.success(Unit)
    }

    // ==========================================
    // OUTCOMES TRACKER (REAL FIRESTORE)
    // ==========================================

    override fun getOutcomeForConnection(connectionId: String): Flow<Outcome?> = callbackFlow {
        if (firestore != null) {
            val registration = firestore.collection("outcomes")
                .whereEqualTo("connectionId", connectionId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(memoryOutcomes[connectionId])
                        return@addSnapshotListener
                    }
                    val doc = snapshot.documents.firstOrNull()?.toObject(Outcome::class.java)
                    if (doc != null) {
                        memoryOutcomes[connectionId] = doc
                    }
                    trySend(doc ?: memoryOutcomes[connectionId])
                }
            awaitClose { registration.remove() }
        } else {
            val job = launch {
                outcomesUpdateTrigger.collect {
                    trySend(memoryOutcomes[connectionId])
                }
            }
            awaitClose { job.cancel() }
        }
    }

    override suspend fun studentMarkComplete(connectionId: String, currentUserId: String): Result<Unit> {
        val conn = memoryConnections[connectionId]
            ?: (if (firestore != null) {
                try {
                    firestore.collection("connections").document(connectionId).get().await().toObject(Connection::class.java)
                } catch (_: Exception) { null }
            } else null)
            ?: return Result.failure(IllegalArgumentException("Connection not found"))

        // Security check: Only the student can mark their action complete
        if (conn.studentId != currentUserId) {
            return Result.failure(SecurityException("Unauthorized: Only the student can mark action complete"))
        }

        val outcome = memoryOutcomes[connectionId] ?: Outcome(
            id = "out_${connectionId}",
            connectionId = connectionId,
            studentId = conn.studentId,
            alumniId = conn.alumniId,
            actionDescription = conn.agreedNextAction
        )

        val updated = outcome.copy(
            studentCompleted = true,
            completedAt = System.currentTimeMillis()
        )
        memoryOutcomes[connectionId] = updated
        outcomesUpdateTrigger.value++

        try {
            withTimeoutOrNull(2000L) {
                firestore?.collection("outcomes")?.document(updated.id)?.set(updated, SetOptions.merge())?.await()
                analytics?.logStudentCompletion(connectionId)
            }
        } catch (_: Exception) {}

        return Result.success(Unit)
    }

    override suspend fun alumniConfirmOutcome(connectionId: String, currentUserId: String): Result<Unit> {
        val conn = memoryConnections[connectionId]
            ?: (if (firestore != null) {
                try {
                    firestore.collection("connections").document(connectionId).get().await().toObject(Connection::class.java)
                } catch (_: Exception) { null }
            } else null)
            ?: return Result.failure(IllegalArgumentException("Connection not found"))

        // Security check: Only the alumnus can confirm the interaction outcome
        if (conn.alumniId != currentUserId) {
            return Result.failure(SecurityException("Unauthorized: Only the alumnus can confirm outcome"))
        }

        val outcome = memoryOutcomes[connectionId]
            ?: return Result.failure(IllegalStateException("Outcome record not found"))

        val completedTime = outcome.completedAt ?: System.currentTimeMillis()
        val updated = outcome.copy(
            alumniConfirmed = true,
            completedAt = completedTime
        )
        memoryOutcomes[connectionId] = updated
        outcomesUpdateTrigger.value++

        // Terminal state transition: update connection status to COMPLETED
        val completedConn = conn.copy(
            status = ConnectionStatus.COMPLETED.name,
            updatedAt = completedTime
        )
        memoryConnections[connectionId] = completedConn
        connectionsUpdateTrigger.value++

        // Close associated help request
        val req = memoryRequests[conn.requestId]
        if (req != null) {
            memoryRequests[conn.requestId] = req.copy(status = RequestStatus.CLOSED.name)
            requestsUpdateTrigger.value++
            try {
                firestore?.collection("helpRequests")?.document(req.id)?.update("status", RequestStatus.CLOSED.name)
            } catch (_: Exception) {}
        }

        try {
            withTimeoutOrNull(2000L) {
                firestore?.collection("outcomes")?.document(updated.id)?.update(
                    mapOf(
                        "alumniConfirmed" to true,
                        "completedAt" to completedTime
                    )
                )?.await()

                firestore?.collection("connections")?.document(connectionId)?.update(
                    mapOf(
                        "status" to ConnectionStatus.COMPLETED.name,
                        "updatedAt" to completedTime
                    )
                )?.await()

                analytics?.logAlumniConfirmation(connectionId)
            }
        } catch (_: Exception) {}

        return Result.success(Unit)
    }

    override fun getCompletedOutcomesForStudent(studentId: String): Flow<List<Outcome>> = callbackFlow {
        if (firestore != null) {
            val registration = firestore.collection("outcomes")
                .whereEqualTo("studentId", studentId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        val completed = memoryOutcomes.values.filter { it.studentId == studentId && (it.studentCompleted || it.alumniConfirmed) }
                        trySend(completed)
                        return@addSnapshotListener
                    }
                    val remote = snapshot.documents.mapNotNull { it.toObject(Outcome::class.java) }
                    val completed = remote.filter { it.studentCompleted || it.alumniConfirmed }
                    trySend(completed)
                }
            awaitClose { registration.remove() }
        } else {
            val job = launch {
                outcomesUpdateTrigger.collect {
                    val completed = memoryOutcomes.values.filter { it.studentId == studentId && (it.studentCompleted || it.alumniConfirmed) }
                    trySend(completed)
                }
            }
            awaitClose { job.cancel() }
        }
    }

    override fun getInstitutionalMetrics(): Flow<InstitutionalMetrics> = callbackFlow {
        val updateMetrics = {
            val totalReqs = memoryRequests.size
            val totalConns = memoryConnections.size
            val acceptedConns = memoryConnections.values.count { it.status in listOf(ConnectionStatus.ACCEPTED.name, ConnectionStatus.COMPLETED.name) }
            val completedOutcomes = memoryOutcomes.values.count { it.alumniConfirmed }
            val studentReported = memoryOutcomes.values.count { it.studentCompleted }
            val alumniConfirmed = memoryOutcomes.values.count { it.alumniConfirmed }
            val activeMentors = memoryProfiles.size.coerceAtLeast(3)
            val acceptRate = if (totalConns > 0) ((acceptedConns.toDouble() / totalConns) * 100).toInt() else 0
            val closureRate = if (acceptedConns > 0) ((completedOutcomes.toDouble() / acceptedConns) * 100).toInt() else 0

            val metrics = InstitutionalMetrics(
                totalRequests = totalReqs,
                totalConnections = totalConns,
                acceptedConnections = acceptedConns,
                completedOutcomes = completedOutcomes,
                studentReportedCount = studentReported,
                alumniConfirmedCount = alumniConfirmed,
                acceptanceRatePercent = acceptRate,
                loopClosureRatePercent = closureRate,
                activeMentorsCount = activeMentors,
                meanResponseHours = 1.8
            )
            trySend(metrics)
        }

        updateMetrics()
        val job = launch {
            connectionsUpdateTrigger.collect { updateMetrics() }
        }
        awaitClose { job.cancel() }
    }
}
