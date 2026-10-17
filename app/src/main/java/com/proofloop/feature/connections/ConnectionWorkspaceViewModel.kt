package com.proofloop.feature.connections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proofloop.ProofLoopApp
import com.proofloop.data.model.Connection
import com.proofloop.data.model.ConnectionStatus
import com.proofloop.data.model.Outcome
import com.proofloop.data.model.SessionStatus
import com.proofloop.data.model.UserAccount
import com.proofloop.data.repository.IProofLoopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.TimeZone

data class ConnectionWorkspaceUiState(
    val currentUser: UserAccount? = null,
    val connection: Connection? = null,
    val outcome: Outcome? = null,
    val actionText: String = "",
    val dueDateText: String = "Within 7 days",
    val ownerText: String = "Student",
    val sessionTimeInput: String = "Saturday 3:00 PM",
    val timeZoneInput: String = TimeZone.getDefault().id,
    val cancellationReasonInput: String = "",
    val isLoading: Boolean = true,
    val isActionInProgress: Boolean = false,
    val feedbackMessage: String? = null,
    val errorMessage: String? = null
)

class ConnectionWorkspaceViewModel(
    private val repository: IProofLoopRepository = ProofLoopApp.instance.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConnectionWorkspaceUiState())
    val uiState: StateFlow<ConnectionWorkspaceUiState> = _uiState.asStateFlow()

    fun loadConnection(connectionId: String) {
        val user = repository.getCurrentUser()
        _uiState.value = _uiState.value.copy(currentUser = user, isLoading = true)

        viewModelScope.launch {
            repository.getConnectionById(connectionId).collect { conn ->
                if (conn != null) {
                    _uiState.value = _uiState.value.copy(
                        connection = conn,
                        actionText = conn.agreedNextAction.ifBlank { _uiState.value.actionText },
                        dueDateText = conn.nextActionDueDate.ifBlank { "Within 7 days" },
                        ownerText = conn.nextActionOwner.ifBlank { conn.studentName.ifBlank { "Student" } },
                        sessionTimeInput = conn.sessionAt.ifBlank { _uiState.value.sessionTimeInput },
                        timeZoneInput = conn.timeZone.ifBlank { TimeZone.getDefault().id },
                        isLoading = false
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.getOutcomeForConnection(connectionId).collect { out ->
                _uiState.value = _uiState.value.copy(outcome = out)
            }
        }
    }

    fun onActionTextChange(text: String) {
        _uiState.value = _uiState.value.copy(actionText = text, errorMessage = null)
    }

    fun onDueDateTextChange(text: String) {
        _uiState.value = _uiState.value.copy(dueDateText = text, errorMessage = null)
    }

    fun onOwnerTextChange(text: String) {
        _uiState.value = _uiState.value.copy(ownerText = text, errorMessage = null)
    }

    fun onSessionTimeChange(time: String) {
        _uiState.value = _uiState.value.copy(sessionTimeInput = time)
    }

    fun onCancellationReasonChange(reason: String) {
        _uiState.value = _uiState.value.copy(cancellationReasonInput = reason)
    }

    fun scheduleSession(sessionTime: String = _uiState.value.sessionTimeInput) {
        val conn = uiState.value.connection ?: return
        val user = repository.getCurrentUser() ?: return
        if (_uiState.value.isActionInProgress) return

        _uiState.value = _uiState.value.copy(isActionInProgress = true, errorMessage = null)

        viewModelScope.launch {
            val tz = _uiState.value.timeZoneInput
            val result = repository.scheduleSession(conn.id, sessionTime, tz, user.uid)
            _uiState.value = _uiState.value.copy(isActionInProgress = false)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    connection = conn.copy(sessionAt = sessionTime, sessionStatus = SessionStatus.CONFIRMED.name, timeZone = tz),
                    feedbackMessage = "Session scheduled for $sessionTime ($tz)!"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Failed to schedule session"
                )
            }
        }
    }

    fun rescheduleSession(newTime: String, reason: String) {
        val conn = uiState.value.connection ?: return
        val user = repository.getCurrentUser() ?: return
        if (_uiState.value.isActionInProgress) return

        _uiState.value = _uiState.value.copy(isActionInProgress = true, errorMessage = null)

        viewModelScope.launch {
            val tz = _uiState.value.timeZoneInput
            val result = repository.rescheduleSession(conn.id, newTime, tz, reason, user.uid)
            _uiState.value = _uiState.value.copy(isActionInProgress = false)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    connection = conn.copy(sessionAt = newTime, sessionStatus = SessionStatus.RESCHEDULED.name, cancellationReason = reason, timeZone = tz),
                    feedbackMessage = "Session rescheduled to $newTime!"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Failed to reschedule session"
                )
            }
        }
    }

    fun cancelConnection(reason: String) {
        val conn = uiState.value.connection ?: return
        val user = repository.getCurrentUser() ?: return
        if (_uiState.value.isActionInProgress) return

        _uiState.value = _uiState.value.copy(isActionInProgress = true, errorMessage = null)

        viewModelScope.launch {
            val result = repository.cancelConnection(conn.id, reason, user.uid)
            _uiState.value = _uiState.value.copy(isActionInProgress = false)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    connection = conn.copy(status = ConnectionStatus.CANCELLED.name, cancellationReason = reason),
                    feedbackMessage = "Mission connection cancelled. Slot has been freed."
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Failed to cancel connection"
                )
            }
        }
    }

    fun saveAgreedAction() {
        val conn = uiState.value.connection ?: return
        val text = uiState.value.actionText.trim()
        val dueDate = uiState.value.dueDateText.trim().ifBlank { "Within 7 days" }
        val owner = uiState.value.ownerText.trim().ifBlank { conn.studentName.ifBlank { "Student" } }

        if (text.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Agreed action cannot be empty")
            return
        }
        if (_uiState.value.isActionInProgress) return

        _uiState.value = _uiState.value.copy(isActionInProgress = true)

        viewModelScope.launch {
            val result = repository.updateAgreedAction(conn.id, text, dueDate, owner)
            _uiState.value = _uiState.value.copy(isActionInProgress = false)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    connection = conn.copy(agreedNextAction = text, nextActionDueDate = dueDate, nextActionOwner = owner),
                    feedbackMessage = "Agreed next action & commitment timeline saved!"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Failed to save action"
                )
            }
        }
    }

    fun studentMarkComplete() {
        val conn = uiState.value.connection ?: return
        val user = repository.getCurrentUser() ?: return
        if (_uiState.value.isActionInProgress) return

        if (user.uid != conn.studentId) {
            _uiState.value = _uiState.value.copy(errorMessage = "Unauthorized: Only the student can mark action complete.")
            return
        }

        _uiState.value = _uiState.value.copy(isActionInProgress = true)

        viewModelScope.launch {
            val result = repository.studentMarkComplete(conn.id, user.uid)
            _uiState.value = _uiState.value.copy(isActionInProgress = false)
            if (result.isSuccess) {
                val currentOutcome = _uiState.value.outcome ?: Outcome(
                    id = "out_${conn.id}",
                    connectionId = conn.id,
                    studentId = conn.studentId,
                    alumniId = conn.alumniId,
                    actionDescription = conn.agreedNextAction
                )
                _uiState.value = _uiState.value.copy(
                    outcome = currentOutcome.copy(studentCompleted = true, completedAt = System.currentTimeMillis()),
                    feedbackMessage = "Action marked complete! Alumnus can now confirm the outcome."
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Failed to update completion"
                )
            }
        }
    }

    fun alumniConfirmOutcome() {
        val conn = uiState.value.connection ?: return
        val user = repository.getCurrentUser() ?: return
        if (_uiState.value.isActionInProgress) return

        if (user.uid != conn.alumniId) {
            _uiState.value = _uiState.value.copy(errorMessage = "Unauthorized: Only the alumnus can confirm the outcome.")
            return
        }

        _uiState.value = _uiState.value.copy(isActionInProgress = true)

        viewModelScope.launch {
            val result = repository.alumniConfirmOutcome(conn.id, user.uid)
            _uiState.value = _uiState.value.copy(isActionInProgress = false)
            if (result.isSuccess) {
                val currentOutcome = _uiState.value.outcome ?: Outcome(
                    id = "out_${conn.id}",
                    connectionId = conn.id,
                    studentId = conn.studentId,
                    alumniId = conn.alumniId,
                    actionDescription = conn.agreedNextAction
                )
                _uiState.value = _uiState.value.copy(
                    outcome = currentOutcome.copy(alumniConfirmed = true, completedAt = System.currentTimeMillis()),
                    connection = conn.copy(status = ConnectionStatus.COMPLETED.name),
                    feedbackMessage = "Outcome confirmed! Loop successfully closed."
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Failed to confirm outcome"
                )
            }
        }
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null, errorMessage = null)
    }
}
