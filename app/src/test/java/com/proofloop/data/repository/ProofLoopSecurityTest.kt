package com.proofloop.data.repository

import com.proofloop.data.model.Connection
import com.proofloop.data.model.ConnectionStatus
import com.proofloop.data.model.Outcome
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProofLoopSecurityTest {

    @Test
    fun `alumnus cannot accept request addressed to another alumnus`() = runBlocking {
        val repo = ProofLoopRepository()
        
        // Send a request from student to demo_alumni_priya
        val sendResult = repo.sendConnectionRequest(
            requestId = "req_101",
            requestTitle = "Android Architecture Help",
            studentId = "demo_student_aarav",
            studentName = "Aarav Sharma",
            alumniId = "demo_alumni_priya",
            alumniName = "Priya Sharma"
        )
        val connId = sendResult.getOrThrow()

        // Unauthorized alumnus tries to accept
        val unauthorizedResult = repo.respondToConnection(
            connectionId = connId,
            accept = true,
            currentUserId = "unauthorized_alumni_charlie"
        )

        assertTrue(unauthorizedResult.isFailure)
        assertTrue(unauthorizedResult.exceptionOrNull() is SecurityException)

        // Intended alumnus accepts
        val authorizedResult = repo.respondToConnection(
            connectionId = connId,
            accept = true,
            currentUserId = "demo_alumni_priya"
        )
        assertTrue(authorizedResult.isSuccess)
    }

    @Test
    fun `student completion does not count as alumni confirmation`() = runBlocking {
        val repo = ProofLoopRepository()

        val sendResult = repo.sendConnectionRequest(
            requestId = "req_102",
            requestTitle = "Mock Technical Interview",
            studentId = "demo_student_aarav",
            studentName = "Aarav Sharma",
            alumniId = "demo_alumni_priya",
            alumniName = "Priya Sharma"
        )
        val connId = sendResult.getOrThrow()

        // Alumnus accepts
        repo.respondToConnection(connId, true, "demo_alumni_priya")

        // Student marks action complete
        val markResult = repo.studentMarkComplete(connId, "demo_student_aarav")
        assertTrue(markResult.isSuccess)

        // Student tries to confirm the outcome on behalf of the alumnus
        val unauthorizedConfirm = repo.alumniConfirmOutcome(connId, "demo_student_aarav")
        assertTrue(unauthorizedConfirm.isFailure)
        assertTrue(unauthorizedConfirm.exceptionOrNull() is SecurityException)

        // Authorized alumnus confirms
        val authorizedConfirm = repo.alumniConfirmOutcome(connId, "demo_alumni_priya")
        assertTrue(authorizedConfirm.isSuccess)
    }
}
