package com.proofloop.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.proofloop.data.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.util.UUID

interface IProofLoopRepository {
    // Auth & User
    suspend fun register(name: String, email: String, password: String, role: UserRole, college: String): Result<UserAccount>
    suspend fun login(email: String, password: String): Result<UserAccount>
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
    suspend fun updateAgreedAction(connectionId: String, actionText: String): Result<Unit>

    // Outcomes
    fun getOutcomeForConnection(connectionId: String): Flow<Outcome?>
    suspend fun studentMarkComplete(connectionId: String, currentUserId: String): Result<Unit>
    suspend fun alumniConfirmOutcome(connectionId: String, currentUserId: String): Result<Unit>
    fun getCompletedOutcomesForStudent(studentId: String): Flow<List<Outcome>>
}

class ProofLoopRepository(
    private val auth: FirebaseAuth? = try { FirebaseAuth.getInstance() } catch (_: Throwable) { null },
    private val firestore: FirebaseFirestore? = try { FirebaseFirestore.getInstance() } catch (_: Throwable) { null }
) : IProofLoopRepository {

    private val currentUserState = MutableStateFlow<UserAccount?>(null)

    // In-memory backing cache for zero-latency, offline resiliency, and seed data guarantee
    private val memoryUsers = mutableMapOf<String, UserAccount>()
    private val memoryProfiles = mutableMapOf<String, PublicProfile>()
    private val memoryRequests = mutableMapOf<String, HelpRequest>()
    private val memoryConnections = mutableMapOf<String, Connection>()
    private val memoryOutcomes = mutableMapOf<String, Outcome>()

    init {
        seedDemoData()
    }

    private fun seedDemoData() {
        // Seed standard demo profiles
        DemoData.ALL_DEMO_ALUMNI.forEach { profile ->
            memoryProfiles[profile.uid] = profile
            memoryUsers[profile.uid] = UserAccount(
                uid = profile.uid,
                name = profile.displayName,
                email = "${profile.displayName.lowercase().replace(" ", ".")}@alumni.edu",
                role = UserRole.ALUMNI.name,
                college = profile.college,
                skills = profile.expertise,
                interests = profile.helpCategories
            )
        }
        memoryUsers[DemoData.DEMO_STUDENT.uid] = DemoData.DEMO_STUDENT
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
        return try {
            val authResult = auth?.createUserWithEmailAndPassword(email.trim(), password)?.await()
            val uid = authResult?.user?.uid ?: UUID.randomUUID().toString()

            val account = UserAccount(
                uid = uid,
                name = name.trim(),
                email = email.trim(),
                role = role.name,
                college = college.trim(),
                createdAt = System.currentTimeMillis()
            )

            // Save private profile to `users/{uid}`
            firestore?.collection("users")?.document(uid)?.set(account)?.await()

            // If alumni, also initialize discoverable public profile in `publicProfiles/{uid}`
            if (role == UserRole.ALUMNI) {
                val publicProfile = PublicProfile(
                    uid = uid,
                    displayName = name.trim(),
                    role = UserRole.ALUMNI.name,
                    college = college.trim(),
                    acceptingRequests = true,
                    maxActiveRequests = 3
                )
                firestore?.collection("publicProfiles")?.document(uid)?.set(publicProfile)?.await()
                memoryProfiles[uid] = publicProfile
            }

            memoryUsers[uid] = account
            currentUserState.value = account
            Result.success(account)
        } catch (e: Exception) {
            // Fallback for offline mode or Firebase project configuration
            val uid = "local_" + UUID.randomUUID().toString().take(8)
            val account = UserAccount(
                uid = uid,
                name = name.trim(),
                email = email.trim(),
                role = role.name,
                college = college.trim(),
                createdAt = System.currentTimeMillis()
            )
            if (role == UserRole.ALUMNI) {
                val publicProfile = PublicProfile(
                    uid = uid,
                    displayName = name.trim(),
                    role = UserRole.ALUMNI.name,
                    college = college.trim(),
                    acceptingRequests = true,
                    maxActiveRequests = 3
                )
                memoryProfiles[uid] = publicProfile
            }
            memoryUsers[uid] = account
            currentUserState.value = account
            Result.success(account)
        }
    }

    override suspend fun login(email: String, password: String): Result<UserAccount> {
        val trimmedEmail = email.trim()
        
        // Fast-path demo login checking
        if (trimmedEmail.contains("aarav", ignoreCase = true) || trimmedEmail.contains("student", ignoreCase = true)) {
            return loginDemo(UserRole.STUDENT)
        }
        if (trimmedEmail.contains("priya", ignoreCase = true) || trimmedEmail.contains("alumni", ignoreCase = true)) {
            return loginDemo(UserRole.ALUMNI)
        }

        return try {
            val authResult = auth?.signInWithEmailAndPassword(trimmedEmail, password)?.await()
            val uid = authResult?.user?.uid ?: throw IllegalStateException("User ID not found")
            
            val snapshot = firestore?.collection("users")?.document(uid)?.get()?.await()
            val account = snapshot?.toObject(UserAccount::class.java) ?: UserAccount(
                uid = uid,
                email = trimmedEmail,
                name = trimmedEmail.substringBefore("@"),
                role = UserRole.STUDENT.name
            )

            memoryUsers[uid] = account
            currentUserState.value = account
            Result.success(account)
        } catch (e: Exception) {
            // Check in-memory accounts
            val localUser = memoryUsers.values.firstOrNull { it.email.equals(trimmedEmail, ignoreCase = true) }
            if (localUser != null) {
                currentUserState.value = localUser
                Result.success(localUser)
            } else {
                Result.failure(e)
            }
        }
    }

    override fun loginDemo(role: UserRole): Result<UserAccount> {
        val account = when (role) {
            UserRole.STUDENT -> DemoData.DEMO_STUDENT
            UserRole.ALUMNI -> memoryUsers[DemoData.DEMO_ALUMNI_1.uid]!!
        }
        currentUserState.value = account
        return Result.success(account)
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
    // ALUMNI PROFILES & DISCOVERY
    // ==========================================

    override fun getDiscoverableAlumni(): Flow<List<PublicProfile>> = callbackFlow {
        // Emit in-memory seed immediately for zero delay
        trySend(memoryProfiles.values.filter { it.acceptingRequests })

        var registration: ListenerRegistration? = null
        try {
            registration = firestore?.collection("publicProfiles")
                ?.whereEqualTo("acceptingRequests", true)
                ?.addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(memoryProfiles.values.filter { it.acceptingRequests })
                        return@addSnapshotListener
                    }
                    val remoteList = snapshot.documents.mapNotNull { it.toObject(PublicProfile::class.java) }
                    // Merge remote with demo profiles
                    val merged = mutableMapOf<String, PublicProfile>()
                    memoryProfiles.values.forEach { merged[it.uid] = it }
                    remoteList.forEach { merged[it.uid] = it }
                    trySend(merged.values.filter { it.acceptingRequests })
                }
        } catch (e: Exception) {
            trySend(memoryProfiles.values.filter { it.acceptingRequests })
        }

        awaitClose { registration?.remove() }
    }

    override suspend fun updateAlumniPublicProfile(profile: PublicProfile): Result<Unit> {
        return try {
            memoryProfiles[profile.uid] = profile
            firestore?.collection("publicProfiles")?.document(profile.uid)?.set(profile)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            memoryProfiles[profile.uid] = profile
            Result.success(Unit)
        }
    }

    override suspend fun getAlumniActiveConnectionsCount(alumniId: String): Int {
        val inMemoryCount = memoryConnections.values.count {
            it.alumniId == alumniId && it.status in listOf(ConnectionStatus.PENDING.name, ConnectionStatus.ACCEPTED.name)
        }
        return inMemoryCount
    }

    // ==========================================
    // HELP REQUESTS
    // ==========================================

    override suspend fun createHelpRequest(request: HelpRequest): Result<String> {
        val id = if (request.id.isNotBlank()) request.id else "req_${UUID.randomUUID().toString().take(10)}"
        val finalRequest = request.copy(id = id, createdAt = System.currentTimeMillis())
        
        memoryRequests[id] = finalRequest

        try {
            withTimeoutOrNull(1000L) {
                firestore?.collection("helpRequests")?.document(id)?.set(finalRequest)?.await()
            }
        } catch (_: Exception) {}

        return Result.success(id)
    }

    override fun getStudentHelpRequests(studentId: String): Flow<List<HelpRequest>> = callbackFlow {
        // Emit memory requests
        trySend(memoryRequests.values.filter { it.studentId == studentId }.sortedByDescending { it.createdAt })

        var registration: ListenerRegistration? = null
        try {
            registration = firestore?.collection("helpRequests")
                ?.whereEqualTo("studentId", studentId)
                ?.addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(memoryRequests.values.filter { it.studentId == studentId }.sortedByDescending { it.createdAt })
                        return@addSnapshotListener
                    }
                    val remote = snapshot.documents.mapNotNull { it.toObject(HelpRequest::class.java) }
                    remote.forEach { memoryRequests[it.id] = it }
                    val sorted = memoryRequests.values.filter { it.studentId == studentId }.sortedByDescending { it.createdAt }
                    trySend(sorted)
                }
        } catch (e: Exception) {
            trySend(memoryRequests.values.filter { it.studentId == studentId }.sortedByDescending { it.createdAt })
        }

        awaitClose { registration?.remove() }
    }

    override suspend fun getHelpRequestById(requestId: String): HelpRequest? {
        val local = memoryRequests[requestId]
        if (local != null) return local
        return try {
            withTimeoutOrNull(1000L) {
                val doc = firestore?.collection("helpRequests")?.document(requestId)?.get()?.await()
                doc?.toObject(HelpRequest::class.java)
            }
        } catch (e: Exception) {
            null
        }
    }

    // ==========================================
    // CONNECTIONS LIFECYCLE
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

        try {
            withTimeoutOrNull(1000L) {
                firestore?.collection("connections")?.document(id)?.set(connection)?.await()
            }
        } catch (_: Exception) {}

        return Result.success(id)
    }

    override fun getConnectionsForStudent(studentId: String): Flow<List<Connection>> = callbackFlow {
        trySend(memoryConnections.values.filter { it.studentId == studentId }.sortedByDescending { it.updatedAt })

        var registration: ListenerRegistration? = null
        try {
            registration = firestore?.collection("connections")
                ?.whereEqualTo("studentId", studentId)
                ?.addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(memoryConnections.values.filter { it.studentId == studentId }.sortedByDescending { it.updatedAt })
                        return@addSnapshotListener
                    }
                    val remote = snapshot.documents.mapNotNull { it.toObject(Connection::class.java) }
                    remote.forEach { memoryConnections[it.id] = it }
                    val sorted = memoryConnections.values.filter { it.studentId == studentId }.sortedByDescending { it.updatedAt }
                    trySend(sorted)
                }
        } catch (e: Exception) {
            trySend(memoryConnections.values.filter { it.studentId == studentId }.sortedByDescending { it.updatedAt })
        }

        awaitClose { registration?.remove() }
    }

    override fun getIncomingConnectionsForAlumni(alumniId: String): Flow<List<Connection>> = callbackFlow {
        trySend(memoryConnections.values.filter { it.alumniId == alumniId && it.status == ConnectionStatus.PENDING.name }.sortedByDescending { it.createdAt })

        var registration: ListenerRegistration? = null
        try {
            registration = firestore?.collection("connections")
                ?.whereEqualTo("alumniId", alumniId)
                ?.whereEqualTo("status", ConnectionStatus.PENDING.name)
                ?.addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(memoryConnections.values.filter { it.alumniId == alumniId && it.status == ConnectionStatus.PENDING.name }.sortedByDescending { it.createdAt })
                        return@addSnapshotListener
                    }
                    val remote = snapshot.documents.mapNotNull { it.toObject(Connection::class.java) }
                    remote.forEach { memoryConnections[it.id] = it }
                    val sorted = memoryConnections.values.filter { it.alumniId == alumniId && it.status == ConnectionStatus.PENDING.name }.sortedByDescending { it.createdAt }
                    trySend(sorted)
                }
        } catch (e: Exception) {
            trySend(memoryConnections.values.filter { it.alumniId == alumniId && it.status == ConnectionStatus.PENDING.name }.sortedByDescending { it.createdAt })
        }

        awaitClose { registration?.remove() }
    }

    override fun getAcceptedConnectionsForAlumni(alumniId: String): Flow<List<Connection>> = callbackFlow {
        trySend(memoryConnections.values.filter { it.alumniId == alumniId && it.status == ConnectionStatus.ACCEPTED.name }.sortedByDescending { it.updatedAt })

        var registration: ListenerRegistration? = null
        try {
            registration = firestore?.collection("connections")
                ?.whereEqualTo("alumniId", alumniId)
                ?.whereEqualTo("status", ConnectionStatus.ACCEPTED.name)
                ?.addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        trySend(memoryConnections.values.filter { it.alumniId == alumniId && it.status == ConnectionStatus.ACCEPTED.name }.sortedByDescending { it.updatedAt })
                        return@addSnapshotListener
                    }
                    val remote = snapshot.documents.mapNotNull { it.toObject(Connection::class.java) }
                    remote.forEach { memoryConnections[it.id] = it }
                    val sorted = memoryConnections.values.filter { it.alumniId == alumniId && it.status == ConnectionStatus.ACCEPTED.name }.sortedByDescending { it.updatedAt }
                    trySend(sorted)
                }
        } catch (e: Exception) {
            trySend(memoryConnections.values.filter { it.alumniId == alumniId && it.status == ConnectionStatus.ACCEPTED.name }.sortedByDescending { it.updatedAt })
        }

        awaitClose { registration?.remove() }
    }

    override fun getConnectionById(connectionId: String): Flow<Connection?> = callbackFlow {
        trySend(memoryConnections[connectionId])

        var registration: ListenerRegistration? = null
        try {
            registration = firestore?.collection("connections")?.document(connectionId)
                ?.addSnapshotListener { snapshot, error ->
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
        } catch (e: Exception) {
            trySend(memoryConnections[connectionId])
        }

        awaitClose { registration?.remove() }
    }

    override suspend fun respondToConnection(
        connectionId: String,
        accept: Boolean,
        currentUserId: String
    ): Result<Unit> {
        val conn = memoryConnections[connectionId]
            ?: return Result.failure(IllegalArgumentException("Connection not found"))

        // Security check: Only the addressed alumnus may accept or decline
        if (conn.alumniId != currentUserId) {
            return Result.failure(SecurityException("Unauthorized: Only the assigned alumnus can respond"))
        }

        val newStatus = if (accept) ConnectionStatus.ACCEPTED.name else ConnectionStatus.DECLINED.name
        val updated = conn.copy(
            status = newStatus,
            updatedAt = System.currentTimeMillis()
        )
        memoryConnections[connectionId] = updated

        // If accepted, also initialize outcome record with agreed action template
        if (accept && memoryOutcomes[connectionId] == null) {
            val outcome = Outcome(
                id = "out_${connectionId}",
                connectionId = connectionId,
                studentId = conn.studentId,
                alumniId = conn.alumniId,
                actionDescription = "Agree on a practical next step to complete after the session.",
                studentCompleted = false,
                alumniConfirmed = false,
                createdAt = System.currentTimeMillis()
            )
            memoryOutcomes[connectionId] = outcome
            try {
                withTimeoutOrNull(1000L) {
                    firestore?.collection("outcomes")?.document(outcome.id)?.set(outcome)?.await()
                }
            } catch (_: Exception) {}
        }

        try {
            withTimeoutOrNull(1000L) {
                firestore?.collection("connections")?.document(connectionId)?.update(
                    mapOf(
                        "status" to newStatus,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )?.await()
            }
        } catch (_: Exception) {}

        return Result.success(Unit)
    }

    override suspend fun updateAgreedAction(connectionId: String, actionText: String): Result<Unit> {
        val conn = memoryConnections[connectionId]
            ?: return Result.failure(IllegalArgumentException("Connection not found"))
        
        val updatedConn = conn.copy(agreedNextAction = actionText.trim(), updatedAt = System.currentTimeMillis())
        memoryConnections[connectionId] = updatedConn

        val outcome = memoryOutcomes[connectionId]
        if (outcome != null) {
            val updatedOutcome = outcome.copy(actionDescription = actionText.trim())
            memoryOutcomes[connectionId] = updatedOutcome
            try {
                withTimeoutOrNull(1000L) {
                    firestore?.collection("outcomes")?.document(outcome.id)?.update("actionDescription", actionText.trim())?.await()
                }
            } catch (_: Exception) {}
        }

        try {
            withTimeoutOrNull(1000L) {
                firestore?.collection("connections")?.document(connectionId)?.update(
                    mapOf(
                        "agreedNextAction" to actionText.trim(),
                        "updatedAt" to System.currentTimeMillis()
                    )
                )?.await()
            }
        } catch (_: Exception) {}

        return Result.success(Unit)
    }

    // ==========================================
    // OUTCOMES TRACKER
    // ==========================================

    override fun getOutcomeForConnection(connectionId: String): Flow<Outcome?> = callbackFlow {
        trySend(memoryOutcomes[connectionId])

        var registration: ListenerRegistration? = null
        try {
            registration = firestore?.collection("outcomes")
                ?.whereEqualTo("connectionId", connectionId)
                ?.addSnapshotListener { snapshot, error ->
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
        } catch (e: Exception) {
            trySend(memoryOutcomes[connectionId])
        }

        awaitClose { registration?.remove() }
    }

    override suspend fun studentMarkComplete(connectionId: String, currentUserId: String): Result<Unit> {
        val conn = memoryConnections[connectionId]
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

        try {
            withTimeoutOrNull(1000L) {
                firestore?.collection("outcomes")?.document(updated.id)?.set(updated)?.await()
            }
        } catch (_: Exception) {}

        return Result.success(Unit)
    }

    override suspend fun alumniConfirmOutcome(connectionId: String, currentUserId: String): Result<Unit> {
        val conn = memoryConnections[connectionId]
            ?: return Result.failure(IllegalArgumentException("Connection not found"))

        // Security check: Only the alumnus can confirm the interaction outcome
        if (conn.alumniId != currentUserId) {
            return Result.failure(SecurityException("Unauthorized: Only the alumnus can confirm outcome"))
        }

        val outcome = memoryOutcomes[connectionId]
            ?: return Result.failure(IllegalStateException("Outcome record not found"))

        val updated = outcome.copy(
            alumniConfirmed = true,
            completedAt = outcome.completedAt ?: System.currentTimeMillis()
        )
        memoryOutcomes[connectionId] = updated

        try {
            withTimeoutOrNull(1000L) {
                firestore?.collection("outcomes")?.document(updated.id)?.update(
                    mapOf(
                        "alumniConfirmed" to true,
                        "completedAt" to updated.completedAt
                    )
                )?.await()
            }
        } catch (_: Exception) {}

        return Result.success(Unit)
    }

    override fun getCompletedOutcomesForStudent(studentId: String): Flow<List<Outcome>> = callbackFlow {
        val completed = memoryOutcomes.values.filter { it.studentId == studentId && (it.studentCompleted || it.alumniConfirmed) }
        trySend(completed)
        awaitClose {}
    }
}
