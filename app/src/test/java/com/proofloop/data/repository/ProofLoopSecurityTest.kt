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

    @Test
    fun `alumnus cannot accept request when at active capacity limit of three`() = runBlocking {
        val repo = ProofLoopRepository()
        val alumniId = "demo_alumni_priya"

        // Create 3 active connections
        for (i in 1..3) {
            val sendResult = repo.sendConnectionRequest(
                requestId = "req_$i",
                requestTitle = "Session $i",
                studentId = "student_$i",
                studentName = "Student $i",
                alumniId = alumniId,
                alumniName = "Priya Sharma"
            )
            val connId = sendResult.getOrThrow()
            val acceptResult = repo.respondToConnection(connId, true, alumniId)
            assertTrue("Connection $i should be accepted", acceptResult.isSuccess)
        }

        // Verify active capacity count is 3
        val activeCount = repo.getAlumniActiveConnectionsCount(alumniId)
        org.junit.Assert.assertEquals(3, activeCount)

        // 4th request sent to the same alumnus
        val fourthSend = repo.sendConnectionRequest(
            requestId = "req_4",
            requestTitle = "Session 4",
            studentId = "student_4",
            studentName = "Student 4",
            alumniId = alumniId,
            alumniName = "Priya Sharma"
        )
        val fourthConnId = fourthSend.getOrThrow()

        // Alumnus attempts to accept 4th request while at capacity (3/3)
        val fourthAccept = repo.respondToConnection(fourthConnId, true, alumniId)
        assertTrue("Accepting 4th request beyond capacity must fail", fourthAccept.isFailure)
        assertTrue(fourthAccept.exceptionOrNull() is IllegalStateException)
        assertTrue(fourthAccept.exceptionOrNull()?.message?.contains("capacity") == true)
    }

    @Test
    fun `independent alumni confirmation transitions connection to COMPLETED and frees capacity`() = runBlocking {
        val repo = ProofLoopRepository()
        val alumniId = "demo_alumni_priya"

        // Accept 3 connections to reach capacity
        val connIds = mutableListOf<String>()
        for (i in 1..3) {
            val sendResult = repo.sendConnectionRequest(
                requestId = "req_cap_$i",
                requestTitle = "Session $i",
                studentId = "student_$i",
                studentName = "Student $i",
                alumniId = alumniId,
                alumniName = "Priya Sharma"
            )
            val id = sendResult.getOrThrow()
            connIds.add(id)
            repo.respondToConnection(id, true, alumniId)
        }

        org.junit.Assert.assertEquals(3, repo.getAlumniActiveConnectionsCount(alumniId))

        // Student completes session 1
        val studentResult = repo.studentMarkComplete(connIds[0], "student_1")
        assertTrue(studentResult.isSuccess)

        // Alumnus independently confirms outcome for session 1
        val confirmResult = repo.alumniConfirmOutcome(connIds[0], alumniId)
        assertTrue(confirmResult.isSuccess)

        // Active capacity must drop from 3 to 2 because session 1 is COMPLETED!
        val newActiveCount = repo.getAlumniActiveConnectionsCount(alumniId)
        org.junit.Assert.assertEquals(2, newActiveCount)

        // Now a 4th request can be safely accepted because a slot was freed up!
        val fourthSend = repo.sendConnectionRequest(
            requestId = "req_cap_4",
            requestTitle = "Session 4",
            studentId = "student_4",
            studentName = "Student 4",
            alumniId = alumniId,
            alumniName = "Priya Sharma"
        )
        val fourthConnId = fourthSend.getOrThrow()
        val acceptAfterFree = repo.respondToConnection(fourthConnId, true, alumniId)
        assertTrue("Should be able to accept request after completed mission freed slot", acceptAfterFree.isSuccess)
    }
}

